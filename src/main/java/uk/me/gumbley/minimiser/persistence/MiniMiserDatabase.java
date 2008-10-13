package uk.me.gumbley.minimiser.persistence;

import uk.me.gumbley.minimiser.persistence.dao.VersionDao;
import uk.me.gumbley.minimiser.persistence.sql.SQLAccess;

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
     * Obtain low-lvel access to the database. Can throw an SQLAccessExcception
     * (a RuntimeException) on failure to get low-level access.
     * @return an interface for performing low-level direct SQL access to the
     * database, e.g. for migration and validating/parsing SQL statements.
     */
    SQLAccess getSQLAccess();
    
    /**
     * Close the database.
     */
    void close();

    /**
     * @return true iff the database is closed
     */
    boolean isClosed();
}
