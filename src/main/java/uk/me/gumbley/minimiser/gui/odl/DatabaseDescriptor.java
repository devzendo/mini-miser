package uk.me.gumbley.minimiser.gui.odl;

/**
 * A bean that gives a database a name, optionally a path, and (in subclasses)
 * references to the persistence/domain layer.
 * 
 * Note that only name is used in the equality and hashCode methods. We key
 * off the name elsewhere for uniqueness (from a UI perspective, it might
 * encourage users to give their DBs unique, meaningful names... yeah, right!)
 * 
 * @author matt
 *
 */
public class DatabaseDescriptor {
    private final String dbName;
    private final String dbPath;

    /**
     * Create a new DatabaseDesriptor, given just a name, used primarily in
     * tests.
     * @param databaseName the database name
     */
    public DatabaseDescriptor(final String databaseName) {
        this.dbName = databaseName;
        this.dbPath = "";
    }

    /**
     * Create a new DatabaseDesriptor, given just a name and its path.
     * @param databaseName the database name
     * @param databaseFullPath the full path to the database.
     */
    public DatabaseDescriptor(final String databaseName, final String databaseFullPath) {
        this.dbName = databaseName;
        this.dbPath = databaseFullPath == null ? "" : databaseFullPath;
    }

    /**
     * Obtain the database name
     * @return the database name
     */
    public final String getDatabaseName() {
        return dbName;
    }

    /**
     * Obtain the database path.
     * @return the database path, which may be an empty string, but never null. 
     */
    public String getDatabasePath() {
        return dbPath;
    }
    
    /**
     * {@inheritDoc}
     */
    public final String toString() {
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
        if (!DatabaseDescriptor.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final DatabaseDescriptor other = (DatabaseDescriptor) obj;
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
