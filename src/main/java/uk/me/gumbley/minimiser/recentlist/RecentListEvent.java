package uk.me.gumbley.minimiser.recentlist;

import uk.me.gumbley.commoncode.patterns.observer.ObservableEvent;

/**
 * Something has happened with the recent list, this is the notification
 * you receive from it, if you're an observer.
 * 
 * At the moment, there's only one event, hence this being final.
 * 
 * @author matt
 *
 */
public final class RecentListEvent implements ObservableEvent {
    /**
     * Create a RecentListEvent
     */
    public RecentListEvent() {
    }
}
