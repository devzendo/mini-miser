package org.devzendo.minimiser.wiring.menu;

import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.gui.menu.Menu;
import org.devzendo.minimiser.gui.menu.MenuWiringAdapter;
import org.devzendo.minimiser.recentlist.RecentFilesList;
import org.devzendo.minimiser.recentlist.RecentListChangedEvent;


/**
 * Adapts between recent list updates and the menu, to update the recent
 * list menu.
 * @author matt
 *
 */
public final class MenuRecentListEventObserver implements MenuWiringAdapter, Observer<RecentListChangedEvent> {
    private final Menu menu;
    private final RecentFilesList recentFilesList;

    /**
     * Construct the adapter given other system objects for interaction.
     * @param leMenu the menu
     * @param recents the recent file list
     */
    public MenuRecentListEventObserver(
            final Menu leMenu,
            final RecentFilesList recents) {
                this.menu = leMenu;
                this.recentFilesList = recents;
    }
    
    /**
     * {@inheritDoc}
     */
    public void connectWiring() {
        // recent list -> menu
        recentFilesList.addRecentListEventObserver(this);
    }
    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final RecentListChangedEvent observableEvent) {
        menu.refreshRecentList(recentFilesList.getRecentDatabases());
    }
}
