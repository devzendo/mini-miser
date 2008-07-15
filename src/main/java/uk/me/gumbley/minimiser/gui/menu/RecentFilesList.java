package uk.me.gumbley.minimiser.gui.menu;

import java.util.ArrayList;
import java.util.List;

import uk.me.gumbley.minimiser.gui.odl.DatabaseDescriptor;
import uk.me.gumbley.minimiser.prefs.IPrefs;

/**
 * Handles CRUD operations on the Recent Files List, persists via the prefs
 * object.
 * 
 * @author matt
 *
 */
public final class RecentFilesList {
    private static final int DEFAULT_CAPACITY = 4;
    private final List<DatabaseDescriptor> databaseList;
    private int currentDatabaseIndex; 
    private final IPrefs preferences;

    /**
     * Construct a recent files list
     * @param prefs the prefs in which to store the recent files list
     */
    public RecentFilesList(final IPrefs prefs) {
        preferences = prefs;
        databaseList = new ArrayList<DatabaseDescriptor>();
        currentDatabaseIndex = -1;
    }

    /**
     * @return the number of entries in the recent files list.
     */
    public int getNumberOfEntries() {
        return databaseList.size();
    }

    /**
     * Add an entry to the recent files list, pushing out older items if this
     * new entry is not currently present. If it is present, move it to the head
     * of the list.
     * @param databaseDescriptor the database descriptor to add.
     */
    public void add(final DatabaseDescriptor databaseDescriptor) {
        if (databaseList.contains(databaseDescriptor)) {
        } else {
            databaseList.add(databaseDescriptor);
            save();
        }
    }

    private void save() {
        final ArrayList<String> listAsStringPaths = new ArrayList<String>();
        for (DatabaseDescriptor databaseDescriptor : databaseList) {
            listAsStringPaths.add(databaseDescriptor.getDatabaseName());
        }
        preferences.setRecentFiles(listAsStringPaths.toArray(new String[0]));
    }

    /**
     * How many unique entries can this list contain before throwing older
     * entries out upon addition?
     * @return the capacity of the list.
     */
    public int getCapacity() {
        // TODO later, possibly allow this to be configured via prefs.
        // reducing the size of the list would truncate.
        return DEFAULT_CAPACITY;
    }
}
