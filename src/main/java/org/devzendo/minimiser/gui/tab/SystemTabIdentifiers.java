package org.devzendo.minimiser.gui.tab;

import java.util.ArrayList;
import java.util.List;

/**
 * The TabIdentifiers provided by the framework are...
 *
 * @author matt
 *
 */
public class SystemTabIdentifiers {

    /**
     * The SQL debug/developer tab
     */
    public static final TabIdentifier SQL = new TabIdentifier("SQL", "SQL", false, 'S', true, "tabSQL", null);

    /**
     * The overview tab
     */
    public static final TabIdentifier OVERVIEW = new TabIdentifier("OVERVIEW", "Overview", true, 'O', true, "tabOVERVIEW", null);

    /**
     * The Categories tab
     */
    public static final TabIdentifier CATEGORIES = new TabIdentifier("CATEGORIES", "Categories", false, 'C', true, "tabCATEGORIES", null);

    private static List<TabIdentifier> permanentIds;
    static {
        permanentIds = new ArrayList<TabIdentifier>();
        for (final TabIdentifier tabId : SystemTabIdentifiers.values()) {
            if (tabId.isTabPermanent()) {
                permanentIds.add(tabId);
            }
        }
    }

    /**
     * Obtain a list of the permanent system tab identifiers, in TabIdentifier order.
     * @return the permanent TabIdentifiers.
     */
    public static List<TabIdentifier> getPermanentTabIdentifiers() {
        return permanentIds;
    }

    /**
     * @param tabName a tab name
     * @return the system TabIdentifier for the given tabName
     */
    public static TabIdentifier valueOf(final String tabName) {
        for (final TabIdentifier systemTabIdentifier : values()) {
            if (systemTabIdentifier.getTabName().equals(tabName)) {
                return systemTabIdentifier;
            }
        }
        throw new IllegalArgumentException("Unknown tab name '" + tabName + "'");
    }

    /**
     * @return all the system TabIdentifiers
     */
    public static TabIdentifier[] values() {
        return new TabIdentifier[] {SQL, OVERVIEW, CATEGORIES};
    }
}
