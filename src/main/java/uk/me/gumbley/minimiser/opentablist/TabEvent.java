package uk.me.gumbley.minimiser.opentablist;

import uk.me.gumbley.commoncode.patterns.observer.ObservableEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;

/**
 * Something has happened with the OpenTabList, this is the notification
 * you receive from the OpenTabList, if you're an observer.
 * 
 * Note that only the TabDescriptor is included in the equality code.
 * 
 * @author matt
 *
 */
public abstract class TabEvent implements ObservableEvent {
    private final TabDescriptor tabDescriptor;
    private final DatabaseDescriptor databaseDescriptor;

    /**
     * Create a TabEvent describing a specific tab.
     * @param database the database descriptor that owns this tab
     * @param tab the tab descriptor of the tab that
     * has opened.
     */
    public TabEvent(final DatabaseDescriptor database, final TabDescriptor tab) {
        this.databaseDescriptor = database;
        this.tabDescriptor = tab;
    }

    /**
     * Obtain the DatabaseDescriptor associated with this event.
     * @return the DatabaseDescriptor
     */
    public final DatabaseDescriptor getDatabaseDescriptor() {
        return databaseDescriptor;
    }

    /**
     * Obtain the TabDescriptor associated with this event.
     * @return the TabDescriptor
     */
    public final TabDescriptor getTabDescriptor() {
        return tabDescriptor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((tabDescriptor == null) ? 0 : tabDescriptor.hashCode());
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
        final TabEvent other = (TabEvent) obj;
        if (tabDescriptor == null) {
            if (other.tabDescriptor != null) {
                return false;
            }
        } else if (!tabDescriptor.equals(other.tabDescriptor)) {
            return false;
        }
        return true;
    }
}
