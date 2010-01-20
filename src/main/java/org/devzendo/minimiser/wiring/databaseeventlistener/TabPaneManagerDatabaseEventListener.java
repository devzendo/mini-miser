package org.devzendo.minimiser.wiring.databaseeventlistener;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.gui.tabpanemanager.TabPaneManager;
import org.devzendo.minimiser.openlist.DatabaseClosedEvent;
import org.devzendo.minimiser.openlist.DatabaseEvent;
import org.devzendo.minimiser.openlist.DatabaseListEmptyEvent;
import org.devzendo.minimiser.openlist.DatabaseOpenedEvent;
import org.devzendo.minimiser.openlist.DatabaseSwitchedEvent;

import uk.me.gumbley.commoncode.patterns.observer.Observer;

/**
 * Adapts between database events and the tab pane manager's card layout.
 * 
 * @author matt
 *
 */
public final class TabPaneManagerDatabaseEventListener implements Observer<DatabaseEvent> {

    private static final Logger LOGGER = Logger
            .getLogger(TabPaneManagerDatabaseEventListener.class);
    private final TabPaneManager paneManager;

    /**
     * Construct an adapter between database events and the tab pane manager
     * @param tabPaneManager the tab pane manager.
     */
    public TabPaneManagerDatabaseEventListener(final TabPaneManager tabPaneManager) {
        this.paneManager = tabPaneManager;
    }

    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final DatabaseEvent databaseEvent) {
        LOGGER.info("Received a " + databaseEvent.getClass().getSimpleName());
        if (databaseEvent instanceof DatabaseOpenedEvent) {
            final DatabaseOpenedEvent openEvent = (DatabaseOpenedEvent) databaseEvent;
            paneManager.addTabPane(openEvent.getDatabaseDescriptor());
        } else if (databaseEvent instanceof DatabaseClosedEvent) {
            final DatabaseClosedEvent closedEvent = (DatabaseClosedEvent) databaseEvent;
            paneManager.removeTabPane(closedEvent.getDatabaseDescriptor());
        } else if (databaseEvent instanceof DatabaseListEmptyEvent) {
            paneManager.hideTabPanes();
        } else if (databaseEvent instanceof DatabaseSwitchedEvent) {
            final DatabaseSwitchedEvent switchedEvent = (DatabaseSwitchedEvent) databaseEvent;
            paneManager.switchToTabPane(switchedEvent.getDatabaseDescriptor());
        }
    }
}
