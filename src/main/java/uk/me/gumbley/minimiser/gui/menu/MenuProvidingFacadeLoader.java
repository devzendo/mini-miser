package uk.me.gumbley.minimiser.gui.menu;

import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.pluginmanager.PluginManager;

/**
 * Obtains all MenuProvidingFacade plugins from the PluginManager, and calls them
 * to initialise the global and database-specific menus.
 *
 * @author matt
 *
 */
public final class MenuProvidingFacadeLoader {

    private final PluginManager mPluginManager;
    private final OpenDatabaseList mOpenDatabaseList;

    /**
     * @param pluginManager the PluginManager from which to obtain the
     * MenuProviding plugins (there should only be one)
     * @param openDatabaseList the OpenDatabaseList
     * @param menu TODO
     */
    public MenuProvidingFacadeLoader(final PluginManager pluginManager,
            final OpenDatabaseList openDatabaseList, Menu menu) {
                mPluginManager = pluginManager;
                mOpenDatabaseList = openDatabaseList;
    }

    public void initialise() {
        // TODO Auto-generated method stub

    }
}
