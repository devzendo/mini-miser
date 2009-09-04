package uk.me.gumbley.minimiser.migrator;

import org.springframework.dao.DataAccessException;

import uk.me.gumbley.minimiser.persistence.DAOFactory;
import uk.me.gumbley.minimiser.util.InstanceSet;

/**
 * Performs migrations - upgrades from an old schema to a new one on open
 * databases, and updates the versions of all plugins via the VersionDao. All
 * done within a transaction, which is rolled back upon failure.
 * 
 * @author matt
 * 
 */
public interface Migrator {
    public enum MigrationVersion {
        /**
         * The database being considered is old w.r.t. the plugins
         * current version and should be migrated.
         */
        OLD,
        
        /**
         * The database being considered is the same version as
         * the plugins and does not need migrating. 
         */
        CURRENT,
        
        /**
         * The database being considered was created by newer
         * versions of the plugins, and so cannot be migrated or
         * opened. 
         */
        FUTURE
    };

    /**
     * Does the opened database require migration?
     * 
     * @param daoFactories
     *        the DAO factories
     * @return true iff it requires migration
     */
    MigrationVersion requiresMigration(InstanceSet<DAOFactory> daoFactories);

    /**
     * Migrate the database to the latest version and update the
     * database schema version numbers. If the exception
     * is thrown, the whole migration is rolled back.
     * 
     * @param daoFactories the DAO factories
     * @throws DataAccessException on migration failure
     */
    void migrate(InstanceSet<DAOFactory> daoFactories) throws DataAccessException;
}
