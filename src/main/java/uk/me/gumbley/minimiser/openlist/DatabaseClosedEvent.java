package uk.me.gumbley.minimiser.openlist;

/**
 * A database has been closed, this is the notification you receive from the 
 * OpenDatabaseList, if you're an observer.
 * 
 * @author matt
 *
 */
public final class DatabaseClosedEvent extends DatabaseNameEvent {
    /**
     * Create a DatabaseClosedEvent describing a specific database.
     * @param databaseName the name of the database that has closed.
     */
    public DatabaseClosedEvent(final String databaseName) {
        super(databaseName);
    }
}
