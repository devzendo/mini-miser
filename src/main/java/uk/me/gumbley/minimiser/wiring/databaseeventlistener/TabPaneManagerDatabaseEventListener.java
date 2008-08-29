package uk.me.gumbley.minimiser.wiring.databaseeventlistener;

import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.gui.tabpanemanager.TabPaneManager;
import uk.me.gumbley.minimiser.openlist.DatabaseClosedEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseListEmptyEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseOpenedEvent;

/**
 * Adapts between database events and the tab pane manager's card layout.
 * 
 * @author matt
 *
 */
public final class TabPaneManagerDatabaseEventListener implements Observer<DatabaseEvent> {

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
        if (databaseEvent instanceof DatabaseOpenedEvent) {
            final DatabaseOpenedEvent openEvent = (DatabaseOpenedEvent) databaseEvent;
            paneManager.addTabPane(openEvent.getDatabaseDescriptor());
        } else if (databaseEvent instanceof DatabaseClosedEvent) {
            final DatabaseClosedEvent closedEvent = (DatabaseClosedEvent) databaseEvent;
            paneManager.removeTabPane(closedEvent.getDatabaseDescriptor());
        } else if (databaseEvent instanceof DatabaseListEmptyEvent) {
            paneManager.hideTabPanes();
        }
    }
}
