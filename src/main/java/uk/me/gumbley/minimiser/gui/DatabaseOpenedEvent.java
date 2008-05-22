package uk.me.gumbley.minimiser.gui;

/**
 * A database has been opened, this is the notification you receive from the 
 * OpenDatabaseList, if you're an observer.
 * 
 * @author matt
 *
 */
public final class DatabaseOpenedEvent extends DatabaseNameEvent {
    /**
     * Create a DatabaseOpenedEvent describing a specific database.
     * @param databaseName the name of the database that has opened.
     */
    public DatabaseOpenedEvent(final String databaseName) {
        super(databaseName);
    }
}
