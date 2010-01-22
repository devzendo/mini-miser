package org.devzendo.minimiser.wiring.menu;

import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.gui.MainFrameTitle;
import org.devzendo.minimiser.gui.menu.Menu;
import org.devzendo.minimiser.gui.menu.MenuWiringAdapter;
import org.devzendo.minimiser.openlist.DatabaseClosedEvent;
import org.devzendo.minimiser.openlist.DatabaseEvent;
import org.devzendo.minimiser.openlist.DatabaseListEmptyEvent;
import org.devzendo.minimiser.openlist.DatabaseOpenedEvent;
import org.devzendo.minimiser.openlist.DatabaseSwitchedEvent;
import org.devzendo.minimiser.openlist.OpenDatabaseList;
import org.devzendo.minimiser.recentlist.RecentFilesList;


/**
 * Adapts between DatabaseEvents and Menu/Title/RecentFilesList updates.
 * @author matt
 *
 */
public final class MenuDatabaseEventObserver implements MenuWiringAdapter, Observer<DatabaseEvent> {

    private final Menu menu;
    private final OpenDatabaseList openDatabaseList;
    private final MainFrameTitle mainFrameTitle;
    private final RecentFilesList recentFilesList;

    /**
     * Construct the adapter given other system objects for interaction.
     * @param leMenu the menu
     * @param databaseList the open database list
     * @param title the main window title abstraction (for database switches)
     * @param recents the recent file list
     */
    public MenuDatabaseEventObserver(
            final Menu leMenu,
            final OpenDatabaseList databaseList,
            final MainFrameTitle title,
            final RecentFilesList recents) {
                this.menu = leMenu;
                this.openDatabaseList = databaseList;
                this.mainFrameTitle = title;
                this.recentFilesList = recents;
    }

    /**
     * {@inheritDoc}
     */
    public void connectWiring() {
        // ODL -> menu and panels
        openDatabaseList.addDatabaseEventObserver(this);
    }

    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final DatabaseEvent event) {
        if (event instanceof DatabaseListEmptyEvent) {
            menu.emptyDatabaseList();
        } else if (event instanceof DatabaseClosedEvent) {
            final DatabaseClosedEvent dce = (DatabaseClosedEvent) event;
            final boolean anyDatabasesOpen = openDatabaseList.getNumberOfDatabases() > 0;
            menu.enableCloseMenu(anyDatabasesOpen);
            if (!anyDatabasesOpen) {
                mainFrameTitle.clearCurrentDatabaseName();
            }
            menu.removeDatabase(dce.getDatabaseName());
        } else if (event instanceof DatabaseOpenedEvent) {
            final DatabaseOpenedEvent doe = (DatabaseOpenedEvent) event;
            menu.enableCloseMenu(true);
            recentFilesList.add(doe.getDatabaseDescriptor());
            menu.addDatabase(doe.getDatabaseName());
        } else if (event instanceof DatabaseSwitchedEvent) {
            final String databaseName = ((DatabaseSwitchedEvent) event).getDatabaseName();
            menu.switchDatabase(databaseName);
            mainFrameTitle.setCurrentDatabaseName(databaseName);
        } else {
            throw new IllegalStateException("Unexpected a " + event.getClass().getSimpleName());
        }
    }
}
