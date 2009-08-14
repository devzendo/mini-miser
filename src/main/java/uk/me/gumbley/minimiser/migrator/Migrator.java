package uk.me.gumbley.minimiser.migrator;

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
     *        the DAO Factories
     * @return true iff it requires migration
     */
    MigrationVersion requiresMigration(final InstanceSet<DAOFactory> daoFactories);
}
