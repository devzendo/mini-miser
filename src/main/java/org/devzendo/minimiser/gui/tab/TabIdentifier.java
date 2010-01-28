package org.devzendo.minimiser.gui.tab;



/**
 * Identifiers for the possible set of tabs that may be viewed.
 *
 * Some tabs are permanent and are always shown, and cannot be closed.
 *
 * The framework provides some tabs, these are System tabs - plugins can
 * contribute others.
 *
 * Tabs are always shown on the View menu is a specific order, and are
 * shown in the tabbed panes in that same order. System tabs are shown before
 * non-System tabs.
 *
 * You can't open the same tab twice.
 *
 * Toolkit routines in TabIdentifierToolkit help to enforce these rules.
 *
 * Equality is based only on the tab name (as is the hash code).
 *
 * Comparison is based on the system flag, then display name, then mnemonic.
 *
 * @author matt
 *
 */
public final class TabIdentifier implements Comparable<TabIdentifier> {
    // TODO: move these to the SystemTabIdentifiers
    /**
     * The SQL debug/developer tab
     */
    public static final TabIdentifier SQL = new TabIdentifier("SQL", "SQL", false, 'S', true);

    /**
     * The overview tab
     */
    public static final TabIdentifier OVERVIEW = new TabIdentifier("OVERVIEW", "Overview", true, 'O', true);

    /**
     * The Categories tab
     */
    public static final TabIdentifier CATEGORIES = new TabIdentifier("CATEGORIES", "Categories", false, 'C', true);

    private final String mName;
    private final String mDisplayName;
    private final boolean mTabPermanent;
    private final char mMnemonic;
    private final boolean mSystemTab;

    /**
     * TODO: SMELL: this is eeviiil.
     * @param tabName a tab name
     * @return the TabIdentifier for the given tabName
     */
    public static TabIdentifier valueOf(final String tabName) {
        // use reflection?
        if (tabName.equals("SQL")) {
            return SQL;
        } else if (tabName.equals("OVERVIEW")) {
            return OVERVIEW;
        } else if (tabName.equals("CATEGORIES")) {
            return CATEGORIES;
        }
        throw new IllegalArgumentException("Unknown tab name '" + tabName + "'");
    }

    /**
     * @return all the TabIdentifiers
     */
    public static TabIdentifier[] values() {
        return new TabIdentifier[] {SQL, OVERVIEW, CATEGORIES};
    }

    /**
     * Construct a plugin TabIdentifier.
     * @param name the unchanging name or key for this TabIdentifier that will be used to record hidden tabs.
     * @param displayName the displayable name of this tab that could change for different languages
     * @param permanent is this tab permanent? Can it be closed?
     * @param mne the tab's mnemonic, for the menu
     */
    public TabIdentifier(final String name, final String displayName, final boolean permanent, final char mne) {
        this(name, displayName, permanent, mne, false);
    }

    /**
     * Construct a TabIdentifier.
     * @param name the unchanging name or key for this TabIdentifier that will be used to record hidden tabs.
     * @param displayName the displayable name of this tab that could change for different languages
     * @param permanent is this tab permanent? Can it be closed?
     * @param mne the tab's mnemonic, for the menu
     * @param system is this a System tab?
     */
    TabIdentifier(final String name, final String displayName, final boolean permanent, final char mne, final boolean system) {
        this.mName = name;
        this.mDisplayName = displayName;
        this.mTabPermanent = permanent;
        this.mMnemonic = mne;
        this.mSystemTab = system;
    }

    /**
     * @return the tab name that can be used as a key in hidden tab storage, etc.
     */
    public String getTabName() {
        return mName;
    }

    /**
     * @return the displayable name
     */
    public String getDisplayableName() {
        return mDisplayName;
    }

    /**
     * Is this tab permanent?
     * @return true iff permanent
     */
    public boolean isTabPermanent() {
        return mTabPermanent;
    }

    /**
     * @return the tab mnemonic for the menu
     */
    public char getMnemonic() {
        return mMnemonic;
    }

    /**
     * @return true iff this is a System tab
     */
    public boolean isSystemTab() {
        return mSystemTab;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return mName.equals(((TabIdentifier) obj).getTabName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return mName.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(final TabIdentifier o) {
        if (o.isSystemTab() != this.isSystemTab()) {
            if (o.isSystemTab()) {
                return 1;
            } else {
                return -1;
            }
        }
        final int displayNameComparison = mDisplayName.compareTo(o.getDisplayableName());
        if (displayNameComparison != 0) {
            return displayNameComparison;
        }
        return new Character(mMnemonic).compareTo(new Character(o.getMnemonic()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("TabIdentifier:%s: '%s' %s tab; %s; mnemonic '%c'",
            mName, mDisplayName,
            (mSystemTab ? "system" : "plugin"),
            (mTabPermanent ? "permanent" : "impermanent"),
            mMnemonic);
    }
}