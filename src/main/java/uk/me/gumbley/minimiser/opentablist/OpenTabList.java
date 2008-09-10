package uk.me.gumbley.minimiser.opentablist;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.commoncode.patterns.observer.ObserverList;
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;

/**
 * Maintains a list of open tabs, for each open database, and fires events
 * when this changes.
 * 
 * @author matt
 *
 */
public final class OpenTabList {
    private Map<String, Set<TabDescriptor>> tabMap = new HashMap<String, Set<TabDescriptor>>();
    private final ObserverList<TabEvent> observerList = new ObserverList<TabEvent>();

    /**
     * Obtain a list of TabDescriptors for a named database, in TabIdentifier
     * order.
     * @param databaseName the database name
     * @return the list of TabDescriptors for this database, or null if this
     * database has not been added yet.
     */
    public List<TabDescriptor> getTabsForDatabase(final String databaseName) {
        checkDatabaseName(databaseName);
        final Set<TabDescriptor> tabSet = tabMap.get(databaseName);
        if (tabSet == null) {
            return null;
        }
        return Arrays.asList((TabDescriptor[]) tabSet.toArray(new TabDescriptor[0]));
    }

    /**
     * Adds a named database to the open tab list, containing no tabs.
     * addDatabase isn't really needed, as you can add tabs to a nonexistant
     * database with addTab, but it's here for symmetry with removeDatabase
     * for which no other mechanism exists.
     * @param databaseName the name of the database to add.
     */
    public void addDatabase(final String databaseName) {
        checkDatabaseName(databaseName);
        createTabSet(databaseName);
    }

    private void checkDatabaseName(final String databaseName) {
        if (databaseName == null || databaseName.length() == 0) {
            throw new IllegalArgumentException("null or empty database supplied");
        }
    }

    /**
     * Add a tab descriptor to a tab. If the tab already exists, an
     * IllegalStateException will be thrown.
     * @param databaseName the database name
     * @param tab the tab
     */
    public void addTab(final String databaseName, final TabDescriptor tab) {
        addDatabase(databaseName);
        final Set<TabDescriptor> tabSet = tabMap.get(databaseName);
        if (tabSet.contains(tab)) {
            throw new IllegalStateException("Tab '" + tab.getTabIdentifier().toString() + "' already exists for database '" + databaseName + "'");
        }
        tabSet.add(tab);
        observerList.eventOccurred(new TabOpenedEvent(tab));
    }

    private void createTabSet(final String databaseName) {
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
}
