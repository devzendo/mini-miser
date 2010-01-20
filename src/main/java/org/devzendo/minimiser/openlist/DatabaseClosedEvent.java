package org.devzendo.minimiser.openlist;

/**
 * A database has been closed, this is the notification you receive from the 
 * OpenDatabaseList, if you're an observer.
 * 
 * @author matt
 *
 */
public final class DatabaseClosedEvent extends DatabaseDescriptorEvent {
    /**
     * Create a DatabaseClosedEvent describing a specific database.
     * @param databaseDescriptor the descriptor of the database that has closed.
     */
    public DatabaseClosedEvent(final DatabaseDescriptor databaseDescriptor) {
        super(databaseDescriptor);
    }
}
