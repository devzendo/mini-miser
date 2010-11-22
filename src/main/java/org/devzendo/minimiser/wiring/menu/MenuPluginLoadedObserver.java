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

package org.devzendo.minimiser.wiring.menu;

import org.apache.log4j.Logger;
import org.devzendo.commonapp.gui.GUIUtils;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.gui.menu.Menu;
import org.devzendo.minimiser.gui.menu.MenuWiringAdapter;
import org.devzendo.minimiser.pluginmanager.ApplicationPluginLoadedEvent;
import org.devzendo.minimiser.pluginmanager.PluginDescriptor;
import org.devzendo.minimiser.pluginmanager.PluginEvent;
import org.devzendo.minimiser.pluginmanager.PluginManager;
import org.devzendo.minimiser.pluginmanager.PluginRegistry;


/**
 * Adapts between plugin manager 'application plugin loaded' events
 * and help menu to change the text of the help menu.
 * 
 * @author matt
 * 
 */
public final class MenuPluginLoadedObserver implements MenuWiringAdapter,
        Observer<PluginEvent> {
    private static final Logger LOGGER = Logger
            .getLogger(MenuPluginLoadedObserver.class);

    private final Menu mMenu;
    private final PluginManager mPluginManager;
    private final PluginRegistry mPluginRegistry;

    /**
     * Construct the adapter given other system objects for interaction.
     * 
     * @param menu
     *        the menu
     * @param pluginManager
     *        the plugin manager
     * @param pluginRegistry the plugin registry
     */
    public MenuPluginLoadedObserver(final Menu menu,
            final PluginManager pluginManager,
            final PluginRegistry pluginRegistry) {
        mMenu = menu;
        mPluginManager = pluginManager;
        mPluginRegistry = pluginRegistry;
    }

    /**
     * {@inheritDoc}
     */
    public void connectWiring() {
        LOGGER.debug("Wiring plugin manager to help menu");
        // plugin manager -> menu (update the help menu)
        mPluginManager.addPluginEventObserver(this);
        // The code here deals mostly with changes to the app name
        // after the menu is wired up. The system won't usually
        // work like this, but this wiring is here just in case
        // the ordering of startup changes.
        // In the normal case, menu wiring happens after the
        // plugins are loaded, and so no notification is sent,
        // so poke the help menu now. 
        // KLUDGE: this next line hasn't been done TDD
        GUIUtils.invokeLaterOnEventThread(new Runnable() {
            public void run() {
                LOGGER.debug("Optimistically rebuilding help menu with "
                    + mPluginRegistry.getApplicationName());
                mMenu.rebuildHelpMenu(mPluginRegistry.getApplicationName());
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final PluginEvent observableEvent) {
        LOGGER.info("PluginEvent received");
        if (observableEvent instanceof ApplicationPluginLoadedEvent) {
            final ApplicationPluginLoadedEvent appLoadEvent = (ApplicationPluginLoadedEvent) observableEvent;
            final PluginDescriptor pluginDescriptor = appLoadEvent.getPluginDescriptor();
            LOGGER.info("Requesting help menu update with application name " + pluginDescriptor.getName()
                    + " version " + pluginDescriptor.getVersion());
            mMenu.rebuildHelpMenu(pluginDescriptor.getName());
        }
    }
}
