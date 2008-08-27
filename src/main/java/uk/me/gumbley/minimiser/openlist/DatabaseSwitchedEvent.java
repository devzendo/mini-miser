package uk.me.gumbley.minimiser.openlist;

/**
 * A database has been opened and automatically switched to, or the user has
 * switched to another database from the ODL, this is the notification you
 * receive from the OpenDatabaseList, if you're an observer.
 *
 * @author matt
 *
 */
public final class DatabaseSwitchedEvent extends DatabaseDescriptorEvent {
    /**
     * Create a DatabaseSwitchedEvent describing a specific database.
     * @param databaseDescriptor the descriptor of the database that is now current
     */
    public DatabaseSwitchedEvent(final DatabaseDescriptor databaseDescriptor) {
        super(databaseDescriptor);
    }
}
