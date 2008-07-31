package uk.me.gumbley.minimiser.gui.menu;

import java.awt.Frame;
import org.apache.log4j.Logger;

import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.opener.AbstractOpenerAdapter;
import uk.me.gumbley.minimiser.opener.DatabaseOpenEvent;
import uk.me.gumbley.minimiser.opener.Opener;
import uk.me.gumbley.minimiser.opener.OpenerAdapter;
import uk.me.gumbley.minimiser.opener.OpenerAdapterFactory;
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
    private final Opener opener;
    private final OpenerAdapterFactory openerAdapterFactory;
    
    /**
     * Create a Mediator between application events and the menu
     * @param leMenu ici un menu
     * @param odl the open database list
     * @param recentFiles the recent files list
     * @param ope the opener
     * @param oaf the OpenerAdapterFactory
     */
    public MenuMediatorImpl(final Menu leMenu, final OpenDatabaseList odl,
            final RecentFilesList recentFiles, final Opener ope,
            final OpenerAdapterFactory oaf) {
        LOGGER.info("initialising MenuMediatorImpl");
        menu = leMenu;
        openDatabaseList = odl;
        recentFilesList = recentFiles;
        opener = ope;
        openerAdapterFactory = oaf;
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
        // opener -> ODL
        opener.addDatabaseOpenObserver(new DatabaseOpenEventObserver());
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
     * 
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
     * 
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
     * 
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
                // TODO pass the mian frame in here
                final OpenerAdapter openerAdapter = openerAdapterFactory.createOpenerAdapter(null, databaseName);
                // WOZERE recent list doesn't pass thru the database path, damnit!
                //opener.openDatabase(databaseName, observableEvent.getDatabasePath, openerAdapter);
            }
        }
    }
    
    /**
     * Adapts between the Opener opening a database and it being added to the
     * OpenDatabaseList.
     * 
     * @author matt
     *
     */
    private final class DatabaseOpenEventObserver implements Observer<DatabaseOpenEvent> {
        /**
         * {@inheritDoc}
         */
        public void eventOccurred(final DatabaseOpenEvent observableEvent) {
            openDatabaseList.addOpenedDatabase(observableEvent.getDatabase());
        }
    }
}
