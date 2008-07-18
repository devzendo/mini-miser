package uk.me.gumbley.minimiser.openlist;

/**
 * A database has been opened, this is the notification you receive from the 
 * OpenDatabaseList, if you're an observer.
 * 
 * @author matt
 *
 */
public final class DatabaseOpenedEvent extends DatabaseNameEvent {
    private final String dbPath;

    /**
     * Create a DatabaseOpenedEvent describing a specific database.
     * @param databaseName the name of the database that has opened.
     * @param databasePath the full path to the database that has been opened 
     */
    public DatabaseOpenedEvent(final String databaseName, final String databasePath) {
        super(databaseName);
        dbPath = databasePath;
    }

    /**
     * Get the database path.
     * @return the full path to the database that has been opened
     */
    public String getDatabasePath() {
        return dbPath;
    }
}
