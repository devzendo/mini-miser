package org.devzendo.minimiser.wiring.menu;

import org.devzendo.minimiser.gui.menu.DatabaseNameChoice;
import org.devzendo.minimiser.gui.menu.Menu;
import org.devzendo.minimiser.gui.menu.MenuWiringAdapter;
import org.devzendo.minimiser.openlist.OpenDatabaseList;

import uk.me.gumbley.commoncode.patterns.observer.Observer;

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
