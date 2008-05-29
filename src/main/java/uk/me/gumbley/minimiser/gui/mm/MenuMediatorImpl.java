package uk.me.gumbley.minimiser.gui.mm;

import org.apache.log4j.Logger;
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
    private static final Logger LOGGER = Logger
            .getLogger(MenuMediatorImpl.class);
    private final Menu menu;
    private final OpenDatabaseList openDatabaseList;
    
    /**
     * Create a Mediator between application events and the menu
     * @param leMenu ici un menu
     * @param odl the open database list
     */
    public MenuMediatorImpl(final Menu leMenu, final OpenDatabaseList odl) {
        LOGGER.info("initialising MenuMediatorImpl");
        menu = leMenu;
        openDatabaseList = odl;
        initialiseMenu();
        wireAdapters();
        LOGGER.info("initialised MenuMediatorImpl");
    }

    private void initialiseMenu() {
        menu.enableCloseMenu(false);
    }

    private void wireAdapters() {
        // ODL -> menu and panels
        openDatabaseList.addDatabaseEventObserver(new DatabaseEventObserver());
        // menu -> ODL (which'll talk back to the menu)
        menu.addDatabaseSwitchObserver(new DatabaseSwitchObserver());
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
                menu.emptyDatabaseList();
            } else if (event instanceof DatabaseClosedEvent) {
                DatabaseClosedEvent dce = (DatabaseClosedEvent) event;
                menu.enableCloseMenu(openDatabaseList.getNumberOfDatabases() > 0);
                menu.removeDatabase(dce.getDatabaseName());
            } else if (event instanceof DatabaseOpenedEvent) {
                DatabaseOpenedEvent doe = (DatabaseOpenedEvent) event;
                menu.enableCloseMenu(true);
                menu.addDatabase(doe.getDatabaseName());
            } else if (event instanceof DatabaseSwitchedEvent) {
                DatabaseSwitchedEvent dse = (DatabaseSwitchedEvent) event;
                menu.switchDatabase(dse.getDatabaseName());
            } else {
                throw new IllegalStateException("Unexpected a " + event.getClass().getSimpleName());
            }
        }
    }
    
    /**
     * Adapts between window menu changes and open database switching
     * @author matt
     *
     */
    public class DatabaseSwitchObserver implements Observer<WindowMenuChoice> {
        /**
         * {@inheritDoc}
         */
        public void eventOccurred(final WindowMenuChoice windowMenuChoice) {
            openDatabaseList.switchDatabase(windowMenuChoice.getDatabaseName());
        }
    }
}
