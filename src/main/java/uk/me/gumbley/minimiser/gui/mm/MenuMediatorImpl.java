package uk.me.gumbley.minimiser.gui.mm;

import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.gui.odl.DatabaseClosedEvent;
import uk.me.gumbley.minimiser.gui.odl.DatabaseEvent;
import uk.me.gumbley.minimiser.gui.odl.DatabaseListEmptyEvent;
import uk.me.gumbley.minimiser.gui.odl.DatabaseOpenedEvent;
import uk.me.gumbley.minimiser.gui.odl.DatabaseSwitchedEvent;
import uk.me.gumbley.minimiser.gui.odl.OpenDatabaseList;

/**
 * Mediates between application events and menu updates.
 * 
 * @author matt
 *
 */
public final class MenuMediatorImpl implements MenuMediator {
    private final Menu menu;
    private final OpenDatabaseList openDatabaseList;
    
    /**
     * Create a Mediator between application events and the menu
     * @param leMenu ici un menu
     * @param odl the open database list
     */
    public MenuMediatorImpl(final Menu leMenu, final OpenDatabaseList odl) {
        menu = leMenu;
        openDatabaseList = odl;
        initialiseMenu();
        wireAdapters();
    }

    private void initialiseMenu() {
        menu.enableCloseMenu(false);
    }

    private void wireAdapters() {
        openDatabaseList.addDatabaseEventObserver(new DatabaseEventObserver());
    }
    
    /**
     * Adapts between DatabaseEvents and Menu updates.
     * 
     * @author matt
     *
     */
    private class DatabaseEventObserver implements Observer<DatabaseEvent> {
        /**
         * {@inheritDoc}
         */
        public void eventOccurred(final DatabaseEvent event) {
            if (event instanceof DatabaseListEmptyEvent) {
                //menu.enableCloseMenu(false);
            } else if (event instanceof DatabaseClosedEvent) {
                menu.enableCloseMenu(false); // TODO WOZERE won't DTRT with multiple open, then close one
            } else if (event instanceof DatabaseOpenedEvent) {
                menu.enableCloseMenu(true);
            } else if (event instanceof DatabaseSwitchedEvent) {
                
            } else {
                throw new IllegalStateException("Unexpected a " + event.getClass().getSimpleName());
            }
        }
    }
}
