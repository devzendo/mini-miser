package uk.me.gumbley.minimiser.persistence;

import uk.me.gumbley.minimiser.persistence.dao.VersionDao;

/**
 * A factory for the database schema's DAO objects.
 *  
 * @author matt
 *
 */
public interface MiniMiserDatabase {
    /**
     * @return the DAO for accessing the Versions table
     */
    VersionDao getVersionDao();
    
    /**
     * Close the database.
     */
    void close();

    /**
     * @return true iff the database is closed
     */
    boolean isClosed();
}
