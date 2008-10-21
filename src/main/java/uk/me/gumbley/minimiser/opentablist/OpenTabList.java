package uk.me.gumbley.minimiser.opentablist;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.commoncode.patterns.observer.ObserverList;
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;

/**
 * Maintains a list of open tabs, for each open database, and fires events
 * when this changes.
 * 
 * @author matt
 *
 */
public final class OpenTabList {
    private static final Logger LOGGER = Logger.getLogger(OpenTabList.class);
    private Map<String, Set<TabDescriptor>> tabMap = new HashMap<String, Set<TabDescriptor>>();
    private final ObserverList<TabEvent> observerList = new ObserverList<TabEvent>();

    /**
     * Obtain a list of TabDescriptors for a named database, in TabIdentifier
     * order.
     * @param databaseName the database name
     * @return the list of TabDescriptors for this database, which can be empty
     * if this database has not been added yet.
     */
    public List<TabDescriptor> getTabsForDatabase(final String databaseName) {
        checkDatabaseName(databaseName);
        final Set<TabDescriptor> tabSet = tabMap.get(databaseName);
        if (tabSet == null) {
            return Collections.emptyList();
        }
        return Arrays.asList((TabDescriptor[]) tabSet.toArray(new TabDescriptor[0]));
    }

    /**
     * Adds a named database to the open tab list, containing no tabs.
     * <p>
     * addDatabase isn't really needed, as you can add tabs to a nonexistant
     * database with addTab, but it's here for symmetry with removeDatabase
     * for which no other mechanism exists.
     * <p>
     * However, we need the insertion point for each tab before we add it,
     * and you can't get the insertion point if the database has not been
     * added, so we call this before adding any tabs.
     *  
     * @param database the database to add.
     */
    public void addDatabase(final DatabaseDescriptor database) {
        if (database == null) {
            throw new IllegalArgumentException("null database descriptor supplied");
        }
        final String databaseName = database.getDatabaseName();
        checkDatabaseName(databaseName);
        LOGGER.info("Adding database '" + databaseName + "'");
        createTabSetIfItDoesntExist(databaseName);
    }

    private void checkDatabaseName(final String databaseName) {
        if (databaseName == null || databaseName.length() == 0) {
            throw new IllegalArgumentException("null or empty database supplied");
        }
    }

    /**
     * Add a tab descriptor to a tab. If the tab already exists, an
     * IllegalStateException will be thrown.
     * @param database the database
     * @param tab the tab
     */
    public void addTab(final DatabaseDescriptor database, final TabDescriptor tab) {
        addDatabase(database);
        final String databaseName = database.getDatabaseName(); 
        final Set<TabDescriptor> tabSet = tabMap.get(databaseName);
        if (tabSet.contains(tab)) {
            throw new IllegalStateException("Tab '" + tab.getTabIdentifier().toString() + "' already exists for database '" + databaseName + "'");
        }
        LOGGER.info("Adding tab '" + tab.getTabIdentifier() + "' for database '" + databaseName + "'");
        tabSet.add(tab);
        observerList.eventOccurred(new TabOpenedEvent(database, tab));
    }

    private void createTabSetIfItDoesntExist(final String databaseName) {
        if (!tabMap.containsKey(databaseName)) {
            final Comparator<TabDescriptor> comparator = new Comparator<TabDescriptor>() {
                public int compare(final TabDescriptor o1, final TabDescriptor o2) {
                    final TabIdentifier i1 = o1.getTabIdentifier();
                    final TabIdentifier i2 = o2.getTabIdentifier();
                    return i1.compareTo(i2);
                }
            };
            tabMap.put(databaseName, new TreeSet<TabDescriptor>(comparator));        
        }
    }

    /**
     * Does the named database contain a given tab?
     * @param databaseName the database name
     * @param tabId a tab identifier
     * @return true iff the named database contains the given tab
     */
    public boolean containsTab(final String databaseName, final TabIdentifier tabId) {
        checkDatabaseName(databaseName);
        if (!tabMap.containsKey(databaseName)) {
            return false;
        }
        final Set<TabDescriptor> tabSet = tabMap.get(databaseName);
        return tabSet.contains(new TabDescriptor(tabId, null));
    }

    /**
     * Add a tab event observer
     * @param obs the observer
     */
    public void addTabEventObserver(final Observer<TabEvent> obs) {
        observerList.addObserver(obs);
    }

    /**
     * Remove a tab event observer
     * @param obs the observer
     */
    public void removeTabEventObserver(final Observer<TabEvent> obs) {
        observerList.removeListener(obs);
    }

    /**
     * Given the current list for a database, at which position should the
     * given tab identifier's tab be inserted?
     * <p>
     * Tabs are placed on the JTabbedPane in TabIdentifier order.
     * <p>
     * An IllegalStateException will be thrown if an attempt to get the insertion
     * position of an existing tab is requested - there can be no duplicates.
     * @param databaseName the database whose list is to be consulted
     * @param tabId the tab identifier that should be added
     * @return the integer position at which the tab should be inserted, or -1
     * if the named database has not been added yet.
     */
    public int getInsertionPosition(final String databaseName, final TabIdentifier tabId) {
        checkDatabaseName(databaseName);
        LOGGER.debug("Getting insertion point for database '" + databaseName + "' tab id '" + tabId + "'");
        if (!tabMap.containsKey(databaseName)) {
            return -1;
        }
        final Set<TabDescriptor> tabSet = tabMap.get(databaseName);
        LOGGER.debug("Tab set for this db is: " + tabSet);
        if (tabSet.contains(new TabDescriptor(tabId, null))) {
            throw new IllegalStateException("Database '" + databaseName
                + "' already contains tab identifier " + tabId
                + ": cannot obtain the insertion point of a duplicate");
        }
        if (tabSet.size() == 0) {
            return 0;
        }
        // since I'm using a TreeSet, existingTabIds is ordered on TabIdentifier order
        final TabDescriptor[] existingTabDescs = tabSet.toArray(new TabDescriptor[0]);
        final TabIdentifier[] existingTabIds = new TabIdentifier[existingTabDescs.length];
        for (int i = 0; i < existingTabDescs.length; i++) {
            existingTabIds[i] = existingTabDescs[i].getTabIdentifier();
        }
        for (int i = 0; i < existingTabIds.length; i++) {
            final TabIdentifier existingId = existingTabIds[i];
            if (existingId.ordinal() > tabId.ordinal()) {
                return i;
            }
        }
        return existingTabIds.length;
    }

    /**
     * Remove a tab from a database. If the tab does not exist, an
     * IllegalStateException will be thrown.
     * @param databaseDescriptor the database containing the tab
     * @param tabDescriptor the tab
     */
    public void removeTab(final DatabaseDescriptor databaseDescriptor, final TabDescriptor tabDescriptor) {
        if (databaseDescriptor == null) {
            throw new IllegalArgumentException("null database descriptor supplied");
        }
        if (tabDescriptor == null) {
            throw new IllegalArgumentException("null tab descriptor supplied");
        }
        final String databaseName = databaseDescriptor.getDatabaseName();
        checkDatabaseName(databaseName);
        LOGGER.info("Removing " + tabDescriptor.getTabIdentifier() + " tab from database '" + databaseName + "'");
        if (!tabMap.containsKey(databaseName)) {
            final String warning = "Tab " + tabDescriptor.getTabIdentifier() + " tab does not exist in database '" + databaseName + "'";
            LOGGER.warn(warning);
            throw new IllegalStateException(warning);
        }
        final Set<TabDescriptor> tabSet = tabMap.get(databaseName);
        if (!tabSet.contains(tabDescriptor)) {
            final String warning = "No such tab " + tabDescriptor.getTabIdentifier() + " in database '" + databaseName + "'";
            LOGGER.warn(warning);
            throw new IllegalStateException(warning);
        }
        tabSet.remove(tabDescriptor);
    }

    /**
     * Remove a database that should be empty of tabs. If the database contains
     * tabs, an IllegalStateException will be thrown.
     * @param databaseDescriptor the database to remove
     */
    public void removeDatabase(final DatabaseDescriptor databaseDescriptor) {
        if (databaseDescriptor == null) {
            throw new IllegalArgumentException("null database descriptor supplied");
        }
        final String databaseName = databaseDescriptor.getDatabaseName();
        checkDatabaseName(databaseName);
        LOGGER.info("Removing database '" + databaseName + "'");
        if (!tabMap.containsKey(databaseName)) {
            final String warning = "Database '" + databaseName + "' does not exist";
            LOGGER.warn(warning);
            throw new IllegalStateException(warning);
        }
        final Set<TabDescriptor> tabSet = tabMap.get(databaseName);
        if (tabSet.size() != 0) {
            final String warning = "Database has " + tabSet.size() + " tab(s); cannot delete database while populated";
            LOGGER.warn(warning);
            throw new IllegalStateException(warning);
        }
        tabMap.remove(databaseName);
    }
}
