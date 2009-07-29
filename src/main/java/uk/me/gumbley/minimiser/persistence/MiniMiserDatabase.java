package uk.me.gumbley.minimiser.persistence;

import uk.me.gumbley.minimiser.persistence.dao.SequenceDao;
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
     * VersionDAO added in V1 of the schema.
     * @return the DAO for accessing the Versions table
     */
    VersionDao getVersionDao();

    /**
     * SequenceDao added in V1 of the schema. 
     * @return the DAO for accessing the Sequence sequence.
     */
    SequenceDao getSequenceDao();
    
    /**
     * Obtain low-lvel access to the database. Can throw an SQLAccessException
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
