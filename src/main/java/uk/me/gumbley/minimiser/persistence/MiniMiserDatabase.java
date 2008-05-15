package uk.me.gumbley.minimiser.persistence;

import uk.me.gumbley.minimiser.persistence.dao.VersionDao;

/**
 * A factory for the database schema's DAO objects, for normal application
 * use, rather than for migration.
 *  
 * @author matt
 *
 */
public interface MiniMiserDatabase extends Database {
    /**
     * @return the DAO for accessing the Versions table
     */
    VersionDao getVersionDao();
}
