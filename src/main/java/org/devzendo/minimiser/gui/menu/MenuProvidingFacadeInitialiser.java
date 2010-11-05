/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devzendo.minimiser.gui.menu;

import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.devzendo.commongui.GUIUtils;
import org.devzendo.minimiser.gui.dialog.problem.ProblemReporter;
import org.devzendo.minimiser.openlist.OpenDatabaseList;
import org.devzendo.minimiser.plugin.facade.providemenu.MenuProviding;
import org.devzendo.minimiser.plugin.facade.providemenu.MenuProvidingFacade;
import org.devzendo.minimiser.pluginmanager.PluginManager;


/**
 * Obtains all MenuProvidingFacade plugins from the PluginManager, and calls
 * them to initialise the global and database-specific menus.
 *
 * @author matt
 *
 */
public final class MenuProvidingFacadeInitialiser {
    private static final Logger LOGGER = Logger
            .getLogger(MenuProvidingFacadeInitialiser.class);
    private final PluginManager mPluginManager;
    private final OpenDatabaseList mOpenDatabaseList;
    private final MenuFacade mMenuFacade;
    private final ProblemReporter mProblemReporter;
    private final ApplicationMenu mGlobalApplicationMenu;
    // Used by the run-on-EDT code in callInitialiseOnSwingEventThread
    private final Object lock = new Object();

    /**
     * @param pluginManager the PluginManager from which to obtain the MenuProviding plugins
     *        (there should only be one)
     * @param openDatabaseList the OpenDatabaseList
     * @param menuFacade the menu rebuild facade
     * @param globalApplicationMenu the global Application Menu
     * @param problemReporter the ProblemReporter used to display any problems
     * with the facade initialisation
     */
    public MenuProvidingFacadeInitialiser(
            final PluginManager pluginManager,
            final OpenDatabaseList openDatabaseList,
            final MenuFacade menuFacade,
            final ApplicationMenu globalApplicationMenu,
            final ProblemReporter problemReporter) {
        synchronized (lock) {
            mPluginManager = pluginManager;
            mOpenDatabaseList = openDatabaseList;
            mMenuFacade = menuFacade;
            mGlobalApplicationMenu = globalApplicationMenu;
            mProblemReporter = problemReporter;
        }
    }

    /**
     * Initialise any MenuProviding plugins' facades on the Swing Event thread.
     */
    public void initialise() {
        LOGGER.info("Initialising MenuProviding facades");
        assert !SwingUtilities.isEventDispatchThread();

        initialiseOnSwingEventThread();
    }

    private void initialiseOnSwingEventThread() {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                synchronized (lock) {
                    LOGGER.info("Initialising MenuProviding facades on Event Thread");
                    final List<MenuProviding> pluginsImplementingFacade = mPluginManager.getPluginsImplementingFacade(MenuProviding.class);
                    for (final MenuProviding menuProviding : pluginsImplementingFacade) {
                        try {
                            final MenuProvidingFacade menuProvidingFacade = menuProviding.getMenuProvidingFacade();
                            if (menuProvidingFacade == null) {
                                LOGGER.warn(
                                    "MenuProviding class "
                                    + menuProviding.getClass().getName()
                                    + " returned a null facade - ignoring");
                            } else {
                                LOGGER.info("Initialising " + menuProvidingFacade.getClass().getName());
                                    menuProvidingFacade.initialise(mGlobalApplicationMenu, mOpenDatabaseList, mMenuFacade);
                                    LOGGER.info("Initialised " + menuProvidingFacade.getClass().getName());
                            }
                        } catch (final RuntimeException re) {
                            mProblemReporter.reportProblem("while initialising the application menu", re);
                        }
                    }
                    LOGGER.info("Initialised MenuProviding facades on Event Thread");
                }
            }
        });
        // this might be voodoo, but it might help update caches on multiprocessors?
        synchronized (lock) {
            return;
        }
    }
}
