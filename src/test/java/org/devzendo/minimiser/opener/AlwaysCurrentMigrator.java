package org.devzendo.minimiser.opener;

import org.devzendo.minimiser.migrator.Migrator;
import org.devzendo.minimiser.persistence.DAOFactory;
import org.devzendo.minimiser.util.InstanceSet;
import org.springframework.dao.DataAccessException;


/**
 * A Migrator that always says the data is current.
 * 
 * @author matt
 *
 */
public final class AlwaysCurrentMigrator implements Migrator {
    /**
     * {@inheritDoc}
     */
    public MigrationVersion requiresMigration(
            final InstanceSet<DAOFactory> daoFactories) {
        return MigrationVersion.CURRENT;
    }

    /**
     * {@inheritDoc}
     */
    public void migrate(final InstanceSet<DAOFactory> daoFactories)
            throws DataAccessException {
    }
}
