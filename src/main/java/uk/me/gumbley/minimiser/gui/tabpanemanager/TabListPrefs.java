package uk.me.gumbley.minimiser.gui.tabpanemanager;

import java.util.ArrayList;
import java.util.List;
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;
import uk.me.gumbley.minimiser.prefs.Prefs;

/**
 * Sits atop Prefs to provide a higher-level storage mechanism for open
 * tabs.
 * 
 * Translates the prefs open tab storage from an array of String names to a List
 * of TabIdentifiers.
 * 
 * Enforces that some tabs are permanent, and therefore always in the open list.
 * 
 * @author matt
 *
 */
public final class TabListPrefs {
    private final Prefs prefs;

    /**
     * Construct a TabListPrefs.
     * @param prefstore the Prefs to read and write from
     */
    public TabListPrefs(final Prefs prefstore) {
        this.prefs = prefstore;
    }

    /**
     * Obtain the set of tabs that should be opened - these comprise the
     * permanent tabs, and those that have been saved. There are no duplicates,
     * and the list is ordered according to the ordering of TabIdentifier.
     * The list will never be null, never empty.
     * 
     * @param databaseName the database name
     * 
     * @return the open tab list.
     */
    public List<TabIdentifier> getOpenTabs(final String databaseName) {
        final List<TabIdentifier> permanentTabIdentifiers = TabIdentifier.getPermanentTabIdentifiers();
        final List<TabIdentifier> openTabIdentifiers = TabIdentifier.toTabIdentifiers(prefs.getOpenTabs(databaseName));
        final List<TabIdentifier> both = new ArrayList<TabIdentifier>();
        both.addAll(permanentTabIdentifiers);
        both.addAll(openTabIdentifiers);
        return TabIdentifier.sortAndDeDupeTabIdentifiers(both);
    }
}
