package uk.me.gumbley.minimiser.openlist;

/**
 * Something has happened to a specific database, this is the notification you
 * receive from the OpenDatabaseList, if you're an observer.
 * 
 * @author matt
 *
 */
public abstract class DatabaseNameEvent extends DatabaseEvent {
    private final String dbName;

    /**
     * Create a DatabaseEvent describing a specific database.
     * @param databaseName the name of the database that has opened.
     */
    public DatabaseNameEvent(final String databaseName) {
        super();
        this.dbName = databaseName;
    }

    /**
     * Obtain the name of the database that has opened 
     * @return the database name
     */
    public String getDatabaseName() {
        return dbName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dbName == null) ? 0 : dbName.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DatabaseNameEvent other = (DatabaseNameEvent) obj;
        if (dbName == null) {
            if (other.dbName != null) {
                return false;
            }
        } else if (!dbName.equals(other.dbName)) {
            return false;
        }
        return true;
    }
}
