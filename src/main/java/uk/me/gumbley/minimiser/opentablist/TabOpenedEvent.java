package uk.me.gumbley.minimiser.opentablist;


/**
 * A tab has been opened, this is the notification you receive from the 
 * OpenTabList, if you're an observer.
 * 
 * @author matt
 *
 */
public final class TabOpenedEvent extends TabDescriptorEvent {

    /**
     * Create a TabOpenedEvent describing a specific tab.
     * @param tabDescriptor the descriptor of the tab that has opened.
     */
    public TabOpenedEvent(final TabDescriptor tabDescriptor) {
        super(tabDescriptor);
    }
}
