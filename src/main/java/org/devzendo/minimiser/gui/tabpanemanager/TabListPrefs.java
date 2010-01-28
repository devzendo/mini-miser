package org.devzendo.minimiser.gui.tabpanemanager;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.gui.tab.SystemTabIdentifiers;
import org.devzendo.minimiser.gui.tab.TabIdentifier;
import org.devzendo.minimiser.gui.tab.TabIdentifierToolkit;
import org.devzendo.minimiser.prefs.Prefs;

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
    private static final Logger LOGGER = Logger.getLogger(TabListPrefs.class);

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
        LOGGER.debug("getOpenTabs for database " + databaseName);
        final List<TabIdentifier> permanentTabIdentifiers = SystemTabIdentifiers.getPermanentTabIdentifiers();
        final List<TabIdentifier> openTabIdentifiers = TabIdentifierToolkit.toTabIdentifiers(prefs.getOpenTabs(databaseName));
        final List<TabIdentifier> both = new ArrayList<TabIdentifier>();
        both.addAll(permanentTabIdentifiers);
        both.addAll(openTabIdentifiers);
        final List<TabIdentifier> returnTabIdentifiers = TabIdentifierToolkit.sortAndDeDupeTabIdentifiers(both);
        LOGGER.debug("getOpenTabs returning " + returnTabIdentifiers);
        return returnTabIdentifiers;
    }

    /**
     * Store the set of tabs that should be opened - the list stored will not
     * contain any permanent tabs, nor any duplicates.
     *
     * @param databaseName the database name
     * @param tabs the tabs to store
     */
    public void setOpenTabs(final String databaseName, final List<TabIdentifier> tabs) {
        LOGGER.debug("setOpenTabs for database " + databaseName + ": " + tabs);
        final List<TabIdentifier> noPermies = new ArrayList<TabIdentifier>(tabs);
        noPermies.removeAll(SystemTabIdentifiers.getPermanentTabIdentifiers());
        final List<TabIdentifier> sortedDeDupedNoPermies = TabIdentifierToolkit.sortAndDeDupeTabIdentifiers(noPermies);
        final List<String> sortedDeDupedNoPermiesNames = new ArrayList<String>();
        for (final TabIdentifier tabId : sortedDeDupedNoPermies) {
            sortedDeDupedNoPermiesNames.add(tabId.getTabName());
        }
        LOGGER.debug("setOpenTabs after removing permanent tabs, sorting and de-duping, storing: " + sortedDeDupedNoPermiesNames);
        prefs.setOpenTabs(databaseName, sortedDeDupedNoPermiesNames.toArray(new String[0]));
    }

    /**
     * Set the active tab for a given database.
     * <p>
     * Delegates directly to the underlying Prefs object.
     *
     * TODO should take a TabIdentifier?
     *
     * @param databaseName the name of the database with an active tab
     * @param tabName the name of the tab to store against that database
     */
    public void setActiveTab(final String databaseName, final String tabName) {
        LOGGER.debug("setActiveTab for database " + databaseName + ": " + tabName);
        prefs.setActiveTab(databaseName, tabName);
    }

    /**
     * Get the active tab for a given database
     * @param databaseName the name of the database with an active tab
     * @return the tab identifier of the previously active tab
     */
    public TabIdentifier getActiveTab(final String databaseName) {
        final String tabName = prefs.getActiveTab(databaseName);
        LOGGER.debug("getActiveTab for database " + databaseName + ": " + tabName);
        return TabIdentifierToolkit.toTabIdentifier(tabName);
    }
}
