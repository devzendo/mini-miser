package uk.me.gumbley.minimiser.opentablist;

/**
 * Something has happened to a specific tab, this is the notification you
 * receive from the OpenTabList, if you're an observer.
 * 
 * @author matt
 *
 */
public abstract class TabDescriptorEvent extends TabEvent {
    private final TabDescriptor tabDescriptor;

    /**
     * Create a DatabaseEvent describing a specific tab.
     * @param descriptor the tab descriptor of the tab that
     * has opened.
     */
    public TabDescriptorEvent(final TabDescriptor descriptor) {
        super();
        this.tabDescriptor = descriptor;
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
        final TabDescriptorEvent other = (TabDescriptorEvent) obj;
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
