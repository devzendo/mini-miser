package uk.me.gumbley.minimiser.plugin.facade.providemenu;

import uk.me.gumbley.minimiser.gui.menu.ApplicationMenu;
import uk.me.gumbley.minimiser.gui.menu.Menu;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;

/**
 * Facade provided to allow application plugins to customise the menu.
 *
 * @author matt
 *
 */
public interface MenuProvidingFacade {
    /**
     * Allow the plugin to populate the global (non-database-specific) ApplicationMenu,
     * wire itself to the OpenDatabaseList to be notified of database opening/closing (so
     * as to populate a database-specific ApplicationMenu or enable/disable menu items
     * based on the number of databases open), and to demand a rebuild of the menu.
     *
     * @param globalApplicationMenu the global ApplicationMenu that's not database-
     * specific
     * @param openDatabaseList the OpenDatabaseList to which the plugin can be added
     * as an Observer<DatabaseEvent>
     * @param menu the Menu which can be rebuilt from system menu items, the global
     * ApplicationMenu, and the current database-specific ApplicationMenu on demand.
     */
    void initialise(
            ApplicationMenu globalApplicationMenu,
            OpenDatabaseList openDatabaseList,
            Menu menu);
}
