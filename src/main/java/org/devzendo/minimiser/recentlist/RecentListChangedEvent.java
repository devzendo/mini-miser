package org.devzendo.minimiser.recentlist;

import org.devzendo.commoncode.patterns.observer.ObservableEvent;

/**
 * The recent list has changed - something has been added that wasn't there
 * before, or something that was there before has been moved to the head,
 * and this is the notification you receive from it, if you're an observer.
 * 
 * At the moment, there's only one event, hence this being final.
 * 
 * @author matt
 *
 */
public final class RecentListChangedEvent implements ObservableEvent {
    /**
     * Create a RecentListChangedEvent
     */
    public RecentListChangedEvent() {
    }
}
