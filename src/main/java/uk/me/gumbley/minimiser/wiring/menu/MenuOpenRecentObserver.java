package uk.me.gumbley.minimiser.wiring.menu;

import org.apache.log4j.Logger;

import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.gui.menu.DatabaseNameAndPathChoice;
import uk.me.gumbley.minimiser.gui.menu.Menu;
import uk.me.gumbley.minimiser.gui.menu.MenuWiringAdapter;
import uk.me.gumbley.minimiser.opener.Opener;
import uk.me.gumbley.minimiser.opener.OpenerAdapter;
import uk.me.gumbley.minimiser.opener.OpenerAdapterFactory;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;

/**
 * Adapts between recent list choices and the opening/switching mechanism.
 * If already opened, switch to it.
 * If not opened, open it.
 * @author matt
 *
 */
public final class MenuOpenRecentObserver implements MenuWiringAdapter, Observer<DatabaseNameAndPathChoice> {
  
    private static final Logger LOGGER = Logger
            .getLogger(MenuOpenRecentObserver.class);
    
    private final Menu menu;
    private final OpenDatabaseList openDatabaseList;
    private final OpenerAdapterFactory openerAdapterFactory;
    private final Opener opener;
    
    /**
     * Construct the adapter given other system objects for interaction.
     * @param leMenu the menu
     * @param databaseList the open database list
     * @param oaFactory the opener adapter factory
     * @param openr the opener
     */
    public MenuOpenRecentObserver(
            final Menu leMenu,
            final OpenDatabaseList databaseList,
            final OpenerAdapterFactory oaFactory,
            final Opener openr) {
        this.menu = leMenu;
        this.openDatabaseList = databaseList;
        this.openerAdapterFactory = oaFactory;
        this.opener = openr;
    }
    
    /**
     * {@inheritDoc}
     */
    public void connectWiring() {
        // menu -> opener (for the recent list)
        menu.addOpenRecentObserver(this);
    }
    
    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final DatabaseNameAndPathChoice observableEvent) {
        final String databaseName = observableEvent.getDatabaseName();
        LOGGER.info("Open Recent: " + databaseName);
        if (openDatabaseList.containsDatabase(new DatabaseDescriptor(databaseName))) {
            LOGGER.info("Already open; switching to it");
            openDatabaseList.switchDatabase(databaseName);
        } else {
            LOGGER.info("Not already open; opening it");
            final OpenerAdapter openerAdapter = openerAdapterFactory.createOpenerAdapter(databaseName);
            opener.openDatabase(databaseName, observableEvent.getDatabasePath(), openerAdapter);
        }
    }
}
