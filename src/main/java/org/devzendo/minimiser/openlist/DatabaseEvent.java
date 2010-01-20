package org.devzendo.minimiser.openlist;

import uk.me.gumbley.commoncode.patterns.observer.ObservableEvent;

/**
 * Something has happened with the OpenDatabaseList, this is the notification
 * you receive from the OpenDatabaseList, if you're an observer.
 * 
 * @author matt
 *
 */
public abstract class DatabaseEvent implements ObservableEvent {
    /**
     * Create a DatabaseEvent
     */
    public DatabaseEvent() {
    }
}
