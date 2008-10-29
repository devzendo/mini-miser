package uk.me.gumbley.minimiser.gui.tab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Identifiers for the possible set of tabs that may be viewed.
 * 
 * Some tabs are permanent and are always shown, and cannot be closed.
 * 
 * Tabs are always shown on the View menu is a specific order, and are
 * shown in the tabbed panes in that same order.
 * 
 * You can't open the same tab twice.
 * 
 * Toolkit routines in this enum help to enforce these rules.
 * 
 * @author matt
 *
 */
public enum TabIdentifier {
    /**
     * The SQL debug/developer tab
     */
    SQL("SQL", false, 'S'),

    /**
     * The overview tab 
     */
    OVERVIEW("Overview", true, 'O'),
    
    /**
     * The Categories tab 
     */
    CATEGORIES("Categories", false, 'C');
    
    private final String displayName;
    private final boolean tabPermanent;
    private final char mnemonic;
    
    private static List<TabIdentifier> permanentIds;
    static {
        permanentIds = new ArrayList<TabIdentifier>();
        for (TabIdentifier tabId : TabIdentifier.values()) {
            if (tabId.isTabPermanent()) {
                permanentIds.add(tabId);
            }
        }
    }

    /**
     * Construct a TabIdentifier.
     * @param name the displayable name of this tab
     * @param permanent is this tab permanent? Can it be closed?
     * @param mne the tab's mnemonic, for the menu
     */
    TabIdentifier(final String name, final boolean permanent, final char mne) {
        this.displayName = name;
        this.tabPermanent = permanent;
        this.mnemonic = mne;
    }

    /**
     * @return the displayable name
     */
    public String getDisplayableName() {
        return displayName;
    }

    /**
     * Is this tab permanent?
     * @return true iff permanent
     */
    public boolean isTabPermanent() {
        return tabPermanent;
    }

    /**
     * @return the tab mnemonic for the menu
     */
    public char getMnemonic() {
        return mnemonic;
    }
    
    /**
     * Translate a list of tab identifier names to a List<TabIdentifier>,
     * omitting any that fail to translate. This does not discard duplicates or
     * order according to TabIdentifier order.
     * @param names the input names (tab identifiers, e.g. "OVERVIEW", not
     * displayable names (e.g. "Overview") 
     * @return the output TabIdentifiers, always a list, could be empty.
     */
    public static List<TabIdentifier> toTabIdentifiers(final String[] names) {
        final ArrayList<TabIdentifier> arrayList = new ArrayList<TabIdentifier>();
        if (names != null) {
            for (final String name : names) {
                final TabIdentifier tabId = toTabIdentifier(name);
                if (tabId != null) {
                    arrayList.add(tabId);
                }
            }
        }
        return arrayList;
    }

    /**
     * Given a name, return the TabIdentifier. Like valueOf, but returns null
     * rather than throwing an IllegalArgumentException on lookup failure.
     * @param tabName a tab name
     * @return the TabIdentifier, or null if the name does not correspond to one.
     */
    public static TabIdentifier toTabIdentifier(final String tabName) {
        if (tabName == null) {
            return null;
        }
        try {
            return TabIdentifier.valueOf(tabName);
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
        for (TabIdentifier tabId : TabIdentifier.values()) {
            if (tabId.getDisplayableName().equals(tabDisplayName)) {
                return tabId;
            }
        }
        return null;
    }

    /**
     * Obtain a list of the permanent tab identifiers, in TabIdentifier order.
     * @return the permanent TabIdentifiers.
     */
    public static List<TabIdentifier> getPermanentTabIdentifiers() {
        return permanentIds;
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
     * Translate a list of tab identifiers to an array of tab names.
     * @param ids the TabIdentifiers 
     * @return an array of String names, never null
     */
    public static String[] toTabNames(final List<TabIdentifier> ids) {
        if (ids != null) {
            final String[] names = new String[ids.size()];
            for (int i = 0; i < ids.size(); i++) {
                names[i] = ids.get(i).toString();
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
        return sort(new HashSet<TabIdentifier>(toTabIdentifiers(names.toArray(new String[0]))));
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
        for (TabIdentifier tabId : values()) {
            if (uniques.contains(tabId)) {
                sortedDeDuped.add(tabId);
            }
        }
        return sortedDeDuped;
    }
}