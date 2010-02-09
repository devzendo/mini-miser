package org.devzendo.minimiser.gui.tab;

import org.apache.commons.lang.StringUtils;

/**
 * Identifiers for tabs that may be viewed and presented on the View menu.
 *
 * Some tabs are permanent and are always present as Tabs, are not present on the View
 * menu, and cannot be closed. Only non-permanent tabs appear on the View menu.
 *
 * The framework provides some tabs, these are System tabs that are declared in
 * SystemTabIdentifiers; plugins can contribute others.
 *
 * Tabs are always shown on the View menu is a specific order, and are
 * shown in the tabbed panes in that same order. System tabs are shown before
 * non-System tabs. Tabs are then ordered on their Display Name.
 *
 * You can't open the same tab twice.
 *
 * Toolkit routines in TabIdentifierToolkit help to enforce these rules.
 *
 * Equality is based only on the tab name (as is the hash code).
 *
 * Comparison is based on the system flag, then display name, then mnemonic.
 *
 * TabIdentifiers contain a Tab Bean Name, and optional Tab Bean Parameter that's used
 * to load and initialise the Tab, by the TabFactory.
 *
 * @author matt
 *
 */
public final class TabIdentifier implements Comparable<TabIdentifier> {
    private final String mName;
    private final String mDisplayName;
    private final boolean mTabPermanent;
    private final char mMnemonic;
    private final boolean mSystemTab;
    private final String mTabBeanName;
    private final Object mTabBeanParameter;

    /**
     * Construct a plugin TabIdentifier.
     * @param name the unchanging name or key for this TabIdentifier that will be used to record hidden tabs.
     * @param displayName the displayable name of this tab that could change for different languages
     * @param permanent is this tab permanent? Can it be closed?
     * @param mne the tab's mnemonic, for the menu
     * @param tabBeanName the name of a prototype-scoped bean defined in the application context that
     * implements this Tab
     * @param tabBeanParameter if not null, this Object will be passed to the Tab by the TabFactory when
     * it has been loaded; it can be used to provide the context for the Tab, e.g. for an Account display
     * Tab, this could be the primary key into the Accounts table that holds the detail of the Account, or
     * it could be the DAO layer object that represents this Account.
     */
    public TabIdentifier(final String name, final String displayName, final boolean permanent,
            final char mne, final String tabBeanName, final Object tabBeanParameter) {
        this(name, displayName, permanent, mne, false, tabBeanName, tabBeanParameter);
    }

    /**
     * Construct a TabIdentifier.
     * @param name the unchanging name or key for this TabIdentifier that will be used to record hidden tabs.
     * @param displayName the displayable name of this tab that could change for different languages
     * @param permanent is this tab permanent? Can it be closed?
     * @param mne the tab's mnemonic, for the menu
     * @param system is this a System tab?
     * @param tabBeanName the name of a prototype-scoped bean defined in the application context that
     * implements this Tab
     * @param tabBeanParameter if not null, this Object will be passed to the Tab by the TabFactory when
     * it has been loaded; it can be used to provide the context for the Tab, e.g. for an Account display
     * Tab, this could be the primary key into the Accounts table that holds the detail of the Account, or
     * it could be the DAO layer object that represents this Account.
     *
     */
    TabIdentifier(final String name, final String displayName, final boolean permanent, final char mne,
        final boolean system, final String tabBeanName, final Object tabBeanParameter) {
        this.mName = name;
        this.mDisplayName = displayName;
        this.mTabPermanent = permanent;
        this.mMnemonic = mne;
        this.mSystemTab = system;
        this.mTabBeanName = tabBeanName;
        this.mTabBeanParameter = tabBeanParameter;
        if (StringUtils.isBlank(tabBeanName)) {
            throw new IllegalArgumentException("TabIdentifiers cannot have a blank tab bean name");
        }
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
     * @return the tabBeanName
     */
    public String getTabBeanName() {
        return mTabBeanName;
    }

    /**
     * @return the tabBeanParameter
     */
    public Object getTabBeanParameter() {
        return mTabBeanParameter;
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