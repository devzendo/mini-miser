package org.devzendo.minimiser.wiring.menu;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.gui.menu.DatabaseNameAndPathChoice;
import org.devzendo.minimiser.gui.menu.Menu;
import org.devzendo.minimiser.gui.menu.MenuWiringAdapter;
import org.devzendo.minimiser.opener.Opener;
import org.devzendo.minimiser.opener.OpenerAdapter;
import org.devzendo.minimiser.opener.OpenerAdapterFactory;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.OpenDatabaseList;

import uk.me.gumbley.commoncode.patterns.observer.Observer;

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
