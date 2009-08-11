package uk.me.gumbley.minimiser.migrator;

import uk.me.gumbley.minimiser.persistence.DAOFactory;
import uk.me.gumbley.minimiser.util.InstanceSet;

/**
 * Performs migrations - upgrades from an old schema to a new one
 * on open databases, and updates the versions of all plugins via
 * the VersionDao. All done within a transaction, which is rolled
 * back upon failure.
 *  
 * @author matt
 *
 */
public interface Migrator {

    /**
     * Does the opened database require migration?
     * @param daoFactories the DAO Factories
     * @return true iff it requires migration
     */
    boolean requiresMigration(final InstanceSet<DAOFactory> daoFactories);
}
