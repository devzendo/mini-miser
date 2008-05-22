package uk.me.gumbley.minimiser.gui;

/**
 * A bean that gives a database a name and references to the persistence/
 * domain layer.
 * 
 * @author matt
 *
 */
public class DatabaseDescriptor {
    private final String dbName;

    /**
     * Create a new DatabaseDesriptor
     * @param databaseName the database name
     */
    public DatabaseDescriptor(final String databaseName) {
        this.dbName = databaseName;
    }

    /**
     * Obtain the database name
     * @return the database name
     */
    public String getDatabaseName() {
        return dbName;
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
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
