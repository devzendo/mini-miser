package org.devzendo.minimiser.gui.tab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A toolkit of routines for working with TabIdentifiers.
 *
 * @author matt
 *
 */
public class TabIdentifierToolkit {

    /**
     * Translate a list of tab identifier names to a List<TabIdentifier>,
     * omitting any that fail to translate. This does not discard duplicates or
     * order according to TabIdentifier order.
     * @param names the input names (tab identifiers, e.g. "OVERVIEW", not
     * displayable names (e.g. "Overview")
     * @return the output TabIdentifiers, always a list, could be empty.
     */
    public static List<TabIdentifier> toTabIdentifiersFromTabNames(final String[] names) {
        final ArrayList<TabIdentifier> arrayList = new ArrayList<TabIdentifier>();
        if (names != null) {
            for (final String name : names) {
                final TabIdentifier tabId = toTabIdentifierFromTabName(name);
                if (tabId != null) {
                    arrayList.add(tabId);
                }
            }
        }
        return arrayList;
    }

    /**
     * Given a tab name, return the TabIdentifier. Like valueOf, but returns null
     * rather than throwing an IllegalArgumentException on lookup failure.
     * @param tabName a tab name
     * @return the TabIdentifier, or null if the name does not correspond to one.
     */
    public static TabIdentifier toTabIdentifierFromTabName(final String tabName) {
        if (tabName == null) {
            return null;
        }
        try {
            return SystemTabIdentifiers.valueOf(tabName);
        } catch (final IllegalArgumentException iae) {
            return null;
        }
    }

    /**
     * Given a display name, return the TabIdentifier. Like valueOf, but returns null
     * rather than throwing an IllegalArgumentException on lookup failure.
     * @param tabDisplayName a tab display name
     * @return the TabIdentifier, or null if the display name does not correspond to one.
     */
    public static TabIdentifier toTabIdentifierFromDisplayName(final String tabDisplayName) {
        if (tabDisplayName == null) {
            return null;
        }
        for (final TabIdentifier tabId : SystemTabIdentifiers.values()) {
            if (tabId.getDisplayableName().equals(tabDisplayName)) {
                return tabId;
            }
        }
        return null;
    }

    /**
     * Translate a list of tab identifiers to an array of display names.
     * @param ids the TabIdentifiers
     * @return an array of String names, never null
     */
    public static String[] toDisplayNames(final List<TabIdentifier> ids) {
        if (ids != null) {
            final String[] names = new String[ids.size()];
            for (int i = 0; i < ids.size(); i++) {
                names[i] = ids.get(i).getDisplayableName();
            }
            return names;
        }
        return new String[0];
    }

    /**
     * Sorts into TabIdentifier order and removes duplicates from a list of
     * valid TabIdentifier names, discarding those that don't translate.
     * @param names the names
     * @return a List<TabIdentifier>.
     */
    public static List<TabIdentifier> sortAndDeDupe(final List<String> names) {
        if (names == null) {
            return Collections.emptyList();
        }
        return sort(new HashSet<TabIdentifier>(toTabIdentifiersFromTabNames(names.toArray(new String[0]))));
    }

    /**
     * Sorts into TabIdentifier order and removes duplicates from a list of
     * TabIdentifiers.
     * @param tabIds the TabIdentifiers
     * @return a List<TabIdentifier>.
     */
    public static List<TabIdentifier> sortAndDeDupeTabIdentifiers(final List<TabIdentifier> tabIds) {
        if (tabIds == null) {
            return Collections.emptyList();
        }
        return sort(new HashSet<TabIdentifier>(tabIds));
    }

    private static List<TabIdentifier> sort(final Set<TabIdentifier> uniques) {
        final ArrayList<TabIdentifier> sortedDeDuped = new ArrayList<TabIdentifier>();
        for (final TabIdentifier tabId : SystemTabIdentifiers.values()) {
            if (uniques.contains(tabId)) {
                sortedDeDuped.add(tabId);
            }
        }
        return sortedDeDuped;
    }
}
