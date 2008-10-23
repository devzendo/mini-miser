package uk.me.gumbley.minimiser.wiring.menu;

import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.gui.menu.MenuWiringAdapter;
import uk.me.gumbley.minimiser.opener.DatabaseOpenEvent;
import uk.me.gumbley.minimiser.opener.Opener;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;

/**
 * Adapts between the Opener opening a database and it being added to the
 * OpenDatabaseList.
 * @author matt
 *
 */
public final class MenuDatabaseOpenEventObserver implements MenuWiringAdapter, Observer<DatabaseOpenEvent> {
    
    private final OpenDatabaseList openDatabaseList;
    private final Opener opener;

    /**
     * Construct the adapter given other system objects for interaction.
     * @param databaseList the open database list
     * @param openr the opener
     */
    public MenuDatabaseOpenEventObserver(
            final OpenDatabaseList databaseList,
            final Opener openr) {
                this.openDatabaseList = databaseList;
                this.opener = openr;
    }
    
    /**
     * {@inheritDoc}
     */
    public void connectWiring() {
        // opener -> ODL
        opener.addDatabaseOpenObserver(this);
    }
    
    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final DatabaseOpenEvent observableEvent) {
        openDatabaseList.addOpenedDatabase(observableEvent.getDatabase());
    }

}
