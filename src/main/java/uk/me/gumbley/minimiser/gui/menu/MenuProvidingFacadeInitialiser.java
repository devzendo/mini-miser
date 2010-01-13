package uk.me.gumbley.minimiser.gui.menu;

import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.minimiser.gui.dialog.problem.ProblemReporter;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.plugin.facade.providemenu.MenuProviding;
import uk.me.gumbley.minimiser.plugin.facade.providemenu.MenuProvidingFacade;
import uk.me.gumbley.minimiser.pluginmanager.PluginManager;

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
    private final Menu mMenu;
    private final ProblemReporter mProblemReporter;
    private final ApplicationMenu mGlobalApplicationMenu;
    // Used by the run-on-EDT code in callInitialiseOnSwingEventThread
    private final Object lock = new Object();

    /**
     * @param pluginManager the PluginManager from which to obtain the MenuProviding plugins
     *        (there should only be one)
     * @param openDatabaseList the OpenDatabaseList
     * @param menu the actual system menu controller
     * @param globalApplicationMenu the global Application Menu
     * @param problemReporter the ProblemReporter used to display any problems
     * with the facade initialisation
     */
    public MenuProvidingFacadeInitialiser(
            final PluginManager pluginManager,
            final OpenDatabaseList openDatabaseList,
            final Menu menu,
            final ApplicationMenu globalApplicationMenu,
            final ProblemReporter problemReporter) {
        synchronized (lock) {
            mPluginManager = pluginManager;
            mOpenDatabaseList = openDatabaseList;
            mMenu = menu;
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
                    final MenuFacade menuFacade = new MenuFacade() {

                        public void rebuildEntireMenu() {
                            mMenu.rebuildEntireMenu();
                        }

                        public void rebuildFileMenu() {
                            mMenu.rebuildFileMenu();
                        }

                        public void rebuildViewMenu() {
                            mMenu.rebuildViewMenu();
                        }
                    };
                    LOGGER.info("Initialising MenuProviding facades on Event Thread");
                    final List<MenuProviding> pluginsImplementingFacade = mPluginManager.getPluginsImplementingFacade(MenuProviding.class);
                    for (final MenuProviding menuProviding : pluginsImplementingFacade) {
                        final MenuProvidingFacade menuProvidingFacade = menuProviding.getMenuProvidingFacade();
                        if (menuProvidingFacade == null) {
                            LOGGER.warn(
                                "MenuProviding class "
                                + menuProviding.getClass().getName()
                                + " returned a null facade - ignoring");
                        } else {
                            LOGGER.info("Initialising " + menuProvidingFacade.getClass().getName());
                            try {
                                menuProvidingFacade.initialise(mGlobalApplicationMenu, mOpenDatabaseList, menuFacade);
                                LOGGER.info("Initialised " + menuProvidingFacade.getClass().getName());
                            } catch (final RuntimeException re) {
                                mProblemReporter.reportProblem("while initialising the application menu", re);
                            }
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
