package uk.me.gumbley.minimiser.recentlist;

import java.util.ArrayList;
import java.util.List;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.commoncode.patterns.observer.ObserverList;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;

/**
 * The core of the RecentFilesList implementation that handles the logic of
 * addition, and attachment/notification of observers, but provides overridable
 * persistence methods.
 * 
 * 
 * TODO a couple of smells here:
 * 1) The prefs-loading call to load then setDatabaseList, since there's now an
 * inheritance hierarchy... a little smelly.
 * 
 * 2) The way the stub calls the addSilently method via a call with a slightly
 * different name.
 *  
 * @author matt
 *
 */
public abstract class AbstractRecentFilesListImpl implements RecentFilesList {
    private final ObserverList<RecentListEvent> observerList;
    private final List<DatabaseDescriptor> databaseList;

    /**
     * Construct an abstract recent files list
     */
    public AbstractRecentFilesListImpl() {
        observerList = new ObserverList<RecentListEvent>();
        databaseList = new ArrayList<DatabaseDescriptor>();
    }

    /**
     * {@inheritDoc}
     */
    
    public final int getNumberOfEntries() {
        return databaseList.size();
    }

    /**
     * {@inheritDoc}
     */
    public final void add(final DatabaseDescriptor databaseDescriptor) {
        final int indexOf = databaseList.indexOf(databaseDescriptor);
        if (indexOf != -1) {
            // the one being added is already present
            if (!databaseList.get(0).equals(databaseDescriptor)) {
                // the one being added is not at the head, so put it there  
                databaseList.remove(indexOf);
                databaseList.add(0, databaseDescriptor);
                observerList.eventOccurred(new RecentListEvent());
                save();
            }
            // else it's already at the head, so don't bother saving
        } else {
            // the one being added is not present
            databaseList.add(0, databaseDescriptor);
            if (databaseList.size() > DEFAULT_CAPACITY) {
                databaseList.remove(DEFAULT_CAPACITY);
            }
            observerList.eventOccurred(new RecentListEvent());
            save();
        }
    }
    
    /**
     * For tests, add a database as though it had been loaded from prefs, and
     * don't notify anyone. For initial setup.
     * 
     * Inserts at the front, and assumes that your test won't add more than the
     * maximum. Normal add would take care of truncating the list for you.
     * This won't. With great power, comes great responsibility.
     * 
     * @param databaseDescriptor the database
     */
    protected final void addSilently(final DatabaseDescriptor databaseDescriptor) {
        databaseList.add(0, databaseDescriptor);
    }

    /**
     * Allow subclasses to set the initial list.
     * @param list the list to set
     */
    protected final void setDatabaseList(final List<DatabaseDescriptor> list) {
        databaseList.clear();
        databaseList.addAll(list);
    }

    /**
     * Allow subclasses to get the list.
     * @return the list
     */
    protected final List<DatabaseDescriptor> getDatabaseList() {
        return databaseList;
    }

    /**
     * Save the database to whatever persistence mechanism is needed. 
     */
    protected abstract void save();
    
    
    /**
     * Retrieve the list from whatever persistence mechanism is needed.
     * @return a list of reconstituted DatabaseDescriptors.
     */
    protected abstract List<DatabaseDescriptor> load();
    
    /**
     * {@inheritDoc}
     */
    public final int getCapacity() {
        // TODO later, possibly allow this to be configured via prefs.
        // reducing the size of the list would truncate.
        return DEFAULT_CAPACITY;
    }

    /**
     * {@inheritDoc}
     */
    public final DatabaseDescriptor[] getRecentFiles() {
        return databaseList.toArray(new DatabaseDescriptor[0]);
    }

    /**
     * {@inheritDoc}
     */
    public final String[] getRecentFileNames() {
        final String[] dbNames = new String[databaseList.size()];
        for (int i = 0; i < dbNames.length; i++) {
            dbNames[i] = databaseList.get(i).getDatabaseName();
        }
        return dbNames;
    }

    /**
     * {@inheritDoc}
     */
    public final void addRecentListEventObserver(final Observer<RecentListEvent> observer) {
        observerList.addObserver(observer);
    }
}