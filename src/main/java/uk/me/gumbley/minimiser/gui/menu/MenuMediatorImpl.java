package uk.me.gumbley.minimiser.gui.menu;

import org.apache.log4j.Logger;

import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.openlist.DatabaseClosedEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.DatabaseEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseListEmptyEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseOpenedEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseSwitchedEvent;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.recentlist.RecentFilesList;
import uk.me.gumbley.minimiser.recentlist.RecentListEvent;

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
    private final RecentFilesList recentFilesList;
    
    /**
     * Create a Mediator between application events and the menu
     * @param leMenu ici un menu
     * @param odl the open database list
     * @param recentFiles the recent files list
     */
    public MenuMediatorImpl(final Menu leMenu, final OpenDatabaseList odl, final RecentFilesList recentFiles) {
        LOGGER.info("initialising MenuMediatorImpl");
        menu = leMenu;
        openDatabaseList = odl;
        recentFilesList = recentFiles;
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
        // menu -> opener (for the recent list)
        // TODO NONTDD
        menu.addOpenRecentObserver(new OpenRecentObserver());
        // recent list -> menu
        recentFilesList.addRecentListEventObserver(new RecentListEventObserver());
    }
    
    /**
     * Adapts between DatabaseEvents and Menu updates.
     * 
     * @author matt
     *
     */
    private final class DatabaseEventObserver implements Observer<DatabaseEvent> {
        /**
         * {@inheritDoc}
         */
        public void eventOccurred(final DatabaseEvent event) {
            if (event instanceof DatabaseListEmptyEvent) {
                menu.emptyDatabaseList();
            } else if (event instanceof DatabaseClosedEvent) {
                final DatabaseClosedEvent dce = (DatabaseClosedEvent) event;
                menu.enableCloseMenu(openDatabaseList.getNumberOfDatabases() > 0);
                menu.removeDatabase(dce.getDatabaseName());
            } else if (event instanceof DatabaseOpenedEvent) {
                final DatabaseOpenedEvent doe = (DatabaseOpenedEvent) event;
                menu.enableCloseMenu(true);
                recentFilesList.add(new DatabaseDescriptor(doe.getDatabaseName(), doe.getDatabasePath()));
                menu.addDatabase(doe.getDatabaseName());
            } else if (event instanceof DatabaseSwitchedEvent) {
                final DatabaseSwitchedEvent dse = (DatabaseSwitchedEvent) event;
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
    private final class DatabaseSwitchObserver implements Observer<DatabaseNameChoice> {
        /**
         * {@inheritDoc}
         */
        public void eventOccurred(final DatabaseNameChoice windowMenuChoice) {
            openDatabaseList.switchDatabase(windowMenuChoice.getDatabaseName());
        }
    }

    /**
     * Adapts between recent list updates and the menu, to update the recent
     * list menu.
     * @author matt
     *
     */
    private final class RecentListEventObserver implements Observer<RecentListEvent> {
        /**
         * {@inheritDoc}
         */
        public void eventOccurred(final RecentListEvent observableEvent) {
            menu.refreshRecentList(recentFilesList.getRecentFileNames());
        }
    }

    /**
     * Adapts between recent list choices and the opening/switching mechanism.
     * If already opened, switch to it.
     * If not opened, open it.
     * @author matt
     *
     */
    private final class OpenRecentObserver implements Observer<DatabaseNameChoice> {
        /**
         * {@inheritDoc}
         */
        public void eventOccurred(final DatabaseNameChoice observableEvent) {
            final String databaseName = observableEvent.getDatabaseName();
            if (openDatabaseList.containsDatabase(new DatabaseDescriptor(databaseName))) {
                openDatabaseList.switchDatabase(databaseName);
            } else {
                
            }
        }
    }
}
