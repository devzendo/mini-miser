package uk.me.gumbley.minimiser.util;

/**
 * A bean for encapsulating a database name and path.
 * @author matt
 *
 */
public final class DatabasePair {
    private final String name;
    private final String path;

    /**
     * Construct a pair
     * @param dbName the database name
     * @param dbPath th edatabase path
     */
    public DatabasePair(final String dbName, final String dbPath) {
        this.name = dbName;
        this.path = dbPath;
    }

    /**
     * @return the database name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the database path
     */
    public String getPath() {
        return path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
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
        final DatabasePair other = (DatabasePair) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (path == null) {
            if (other.path != null) {
                return false;
            }
        } else if (!path.equals(other.path)) {
            return false;
        }
        return true;
    }
}