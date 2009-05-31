package uk.me.gumbley.minimiser.wiring.menu;

import org.apache.log4j.Logger;

import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.gui.menu.Menu;
import uk.me.gumbley.minimiser.gui.menu.MenuWiringAdapter;
import uk.me.gumbley.minimiser.pluginmanager.AppDetails;
import uk.me.gumbley.minimiser.pluginmanager.ApplicationPluginLoadedEvent;
import uk.me.gumbley.minimiser.pluginmanager.PluginEvent;
import uk.me.gumbley.minimiser.pluginmanager.PluginManager;

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
    private final AppDetails mAppDetails;

    /**
     * Construct the adapter given other system objects for interaction.
     * 
     * @param menu
     *        the menu
     * @param pluginManager
     *        the plugin manager
     * @param appDetails the application details
     */
    public MenuPluginLoadedObserver(final Menu menu,
            final PluginManager pluginManager,
            final AppDetails appDetails) {
        mMenu = menu;
        mPluginManager = pluginManager;
        mAppDetails = appDetails;
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
                LOGGER.debug("Optimistically rebuilding help menu with " + mAppDetails.getApplicationName());
                mMenu.rebuildHelpMenu(mAppDetails.getApplicationName());
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
            LOGGER.info("Requesting help menu update with application name " + appLoadEvent.getName()
                    + " version " + appLoadEvent.getVersion());
            mMenu.rebuildHelpMenu(appLoadEvent.getName());
        }
    }
}
