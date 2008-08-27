package uk.me.gumbley.minimiser.openlist;

/**
 * A database has been opened, this is the notification you receive from the 
 * OpenDatabaseList, if you're an observer.
 * 
 * @author matt
 *
 */
public final class DatabaseOpenedEvent extends DatabaseDescriptorEvent {

    /**
     * Create a DatabaseOpenedEvent describing a specific database.
     * @param databaseDescriptor the descriptor of the database that has opened.
     */
    public DatabaseOpenedEvent(final DatabaseDescriptor databaseDescriptor) {
        super(databaseDescriptor);
    }
}
