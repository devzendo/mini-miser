package uk.me.gumbley.minimiser.gui.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;

/**
 * Encapsulates the state of the menu, i.e. all the data that the MenuImpl and
 * the *Menu classes need to operate.
 * <p>
 * All this information is available elsewhere, this stash provides a synopsis
 * of it that's relevant to the menu.
 * 
 * TODO It may be necessary to subdivide this into several functional areas.
 * 
 * @author matt
 *
 */
public final class MenuState {
    private List<String> databases;
    private int currentDatabaseIndex;
    private DatabaseDescriptor[] recentDatabaseDescriptors;
    private Map<String, Boolean> hiddenTabs;

    /**
     * Initialise the menu state holder.
     */
    public MenuState() {
        databases = new ArrayList<String>();
        currentDatabaseIndex = -1;
        recentDatabaseDescriptors = new DatabaseDescriptor[0];
        hiddenTabs = new HashMap<String, Boolean>();
    }

    // Open database list ------------------------------------------------------
    
    /**
     * Adds a database name
     * @param dbName the database name
     */
    public void addDatabase(final String dbName) {
        databases.add(dbName);
    }

    /**
     * Remove everything from the databases list
     */
    public void clearDatabasesList() {
        databases.clear();
        currentDatabaseIndex = -1;
    }

    /**
     * Remove a database from the list
     * @param dbName the database name
     */
    public void removeDatabase(final String dbName) {
        databases.remove(dbName);
    }

    /**
     * Set the current database
     * @param dbName the name of the database to switch to
     */
    public void switchToDatabase(final String dbName) {
        currentDatabaseIndex = databases.indexOf(dbName);
    }

    /**
     * Get the number of databases
     * @return the number of databases
     */
    public int getNumberOfDatabases() {
        return databases.size();
    }

    /**
     * Get the name of a specific database by index
     * @param index the index into the database list
     * @return the name of the database
     */
    public String getDatabase(final int index) {
        return databases.get(index);
    }

    /**
     * Get index of the current database in the database list
     * @return the index of the current database
     */
    public int getCurrentDatabaseIndex() {
        return currentDatabaseIndex;
    }

    // Recent list -------------------------------------------------------------
    
    /**
     * Store the new set of recent database details
     * @param dbPairs the name/path pairs of the recent list
     */
    public void setRecentDatabaseDescriptors(final DatabaseDescriptor[] dbPairs) {
        recentDatabaseDescriptors = dbPairs;
    }

    /**
     * Get the number of entries in the recent list
     * @return the number of entries
     */
    public int getNumberOfRecentDatabaseDescriptors() {
        return recentDatabaseDescriptors.length;
    }
    
    /**
     * Get a database descriptor for an entry in the recent list by index
     * @param index the index into the recent list
     * @return the database descriptor
     */
    public DatabaseDescriptor getRecentDatabaseDescriptor(final int index) {
        return recentDatabaseDescriptors[index];
    }
    
    // View menu hidden entries ------------------------------------------------
    
    /**
     * Record that a given view menu item / tab is hidden or not
     * @param tabName the name of the tab
     * @param tabHidden true iff hidden
     */
    public void putHiddenTab(final String tabName, final boolean tabHidden) {
        hiddenTabs.put(tabName, tabHidden);
    }

    /**
     * Is a view menu item / tab hidden?
     * @param tabName the TabIdentifier.toString() name of the tab
     * @return true iff hidden
     */
    public boolean isViewMenuItemHidden(final String tabName) {
        final Boolean hidden = hiddenTabs.get(tabName);
        return hidden != null && hidden;
    }
}
