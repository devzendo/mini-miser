/**
 * 
 */
package uk.me.gumbley.minimiser.gui.menu;

import java.util.ArrayList;
import java.util.List;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.commoncode.patterns.observer.ObserverList;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.recentlist.RecentFilesList;
import uk.me.gumbley.minimiser.recentlist.RecentListEvent;

/**
 * A stub recent file list that is pretty dumb - accepts observers, and
 * notifies them whenever ANY change occurs to the list.
 * 
 * @author matt
 *
 */
public final class StubRecentFilesList implements RecentFilesList {
    private final ObserverList<RecentListEvent> observerList;
    private final List<DatabaseDescriptor> databaseList;

    /**
     * 
     */
    StubRecentFilesList() {
        observerList = new ObserverList<RecentListEvent>();
        databaseList = new ArrayList<DatabaseDescriptor>();
    }

    /**
     * {@inheritDoc}
     */
    public void add(final DatabaseDescriptor databaseDescriptor) {
        databaseList.add(databaseDescriptor);
        observerList.eventOccurred(new RecentListEvent());
    }

    /**
     * {@inheritDoc}
     */
    public void addRecentListEventObserver(
            final Observer<RecentListEvent> observer) {
        observerList.addObserver(observer);
    }

    /**
     * {@inheritDoc}
     */
    public int getCapacity() {
        return 4;
    }

    /**
     * {@inheritDoc}
     */
    public int getNumberOfEntries() {
        return databaseList.size();
    }

    /**
     * {@inheritDoc}
     */
    public DatabaseDescriptor[] getRecentFiles() {
        return databaseList.toArray(new DatabaseDescriptor[0]);
    }
    
    /**
     * {@inheritDoc}
     */
    public String[] getRecentFileNames() {
        return new String[0];
    }
}