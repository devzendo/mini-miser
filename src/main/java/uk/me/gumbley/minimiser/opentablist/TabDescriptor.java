package uk.me.gumbley.minimiser.opentablist;

import uk.me.gumbley.minimiser.gui.tab.Tab;
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;

/**
 * A view tab. Contains an identifier, and a Tab (which contains a
 * graphical component).
 * 
 * @author matt
 */
public final class TabDescriptor {
    private final TabIdentifier tabIdentifier;

    private final Tab tab;

    /**
     * Create a TabDescriptor with an identifier and implementation.
     * 
     * @param id
     *        the TabIdentifier for this tab
     * @param tabImpl
     *        the implementation for this tab
     */
    public TabDescriptor(final TabIdentifier id, final Tab tabImpl) {
        this.tabIdentifier = id;
        this.tab = tabImpl;
    }

    /**
     * Create a TabDescriptor with an identifier but no implementation.
     * 
     * @param id
     *        the TabIdentifier for this tab
     */
    public TabDescriptor(final TabIdentifier id) {
        this(id, null);
    }
    
    /**
     * Get the tab's TabIdentifier
     * 
     * @return the TabIdentifier
     */
    public TabIdentifier getTabIdentifier() {
        return tabIdentifier;
    }

    /**
     * Get the tab's Tab implementation
     * 
     * @return the Tab implementation.
     */
    public Tab getTab() {
        return tab;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((tabIdentifier == null) ? 0 : tabIdentifier.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TabDescriptor other = (TabDescriptor) obj;
        if (tabIdentifier == null) {
            if (other.tabIdentifier != null) {
                return false;
            }
        } else if (!tabIdentifier.equals(other.tabIdentifier)) {
            return false;
        }
        return true;
    }
}
