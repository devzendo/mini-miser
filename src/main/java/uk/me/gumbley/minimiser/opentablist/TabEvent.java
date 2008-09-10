package uk.me.gumbley.minimiser.opentablist;

import uk.me.gumbley.commoncode.patterns.observer.ObservableEvent;

/**
 * Something has happened with the OpenTabList, this is the notification
 * you receive from the OpenTabList, if you're an observer.
 * 
 * @author matt
 *
 */
public abstract class TabEvent implements ObservableEvent {
    /**
     * Create a TabEvent
     */
    public TabEvent() {
    }
}
