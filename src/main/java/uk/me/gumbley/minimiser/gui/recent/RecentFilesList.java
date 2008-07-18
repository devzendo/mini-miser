package uk.me.gumbley.minimiser.gui.recent;

import uk.me.gumbley.minimiser.gui.odl.DatabaseDescriptor;

/**
 * Maintains a short list of recently-opened files.
 * New entries are added at the head, pushing out the last entries if the
 * capacity has been exceeded.
 * Existing entries re-opened are moved to the head.
 * 
 * @author matt
 *
 */
public interface RecentFilesList {
    /**
     * The default capacity for the recent files list.
     */
    int DEFAULT_CAPACITY = 4;

    /**
     * @return the number of entries in the recent files list.
     */
    int getNumberOfEntries();

    /**
     * Add an entry to the recent files list, pushing out older items if this
     * new entry is not currently present. If it is present, move it to the head
     * of the list.
     * @param databaseDescriptor the database descriptor to add.
     */
    void add(final DatabaseDescriptor databaseDescriptor);

    /**
     * How many unique entries can this list contain before throwing older
     * entries out upon addition?
     * @return the capacity of the list.
     */
    int getCapacity();

    /**
     * Return the array of recent files
     * @return an array of database descriptors, never null
     */
    DatabaseDescriptor[] getRecentFiles();
}