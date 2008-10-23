package uk.me.gumbley.minimiser.wiring.menu;

import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.gui.menu.DatabaseNameChoice;
import uk.me.gumbley.minimiser.gui.menu.Menu;
import uk.me.gumbley.minimiser.gui.menu.MenuWiringAdapter;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;

/**
 * Adapts between window menu changes and open database switching
 * 
 * @author matt
 *
 */
public final class MenuDatabaseSwitchObserver implements MenuWiringAdapter, Observer<DatabaseNameChoice> {

    private final Menu menu;
    private final OpenDatabaseList openDatabaseList;
    
    /**
     * Construct the adapter given other system objects for interaction.
     * @param leMenu the menu
     * @param databaseList the open database list
     */
    public MenuDatabaseSwitchObserver(
            final Menu leMenu,
            final OpenDatabaseList databaseList) {
        this.menu = leMenu;
        this.openDatabaseList = databaseList;
    }

    /**
     * {@inheritDoc}
     */
    public void connectWiring() {
        // menu -> ODL (which'll talk back to the menu)
        menu.addDatabaseSwitchObserver(this);
    }
    
    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final DatabaseNameChoice windowMenuChoice) {
        final String databaseName = windowMenuChoice.getDatabaseName();
        openDatabaseList.switchDatabase(databaseName);
    }
}
