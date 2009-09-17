package uk.me.gumbley.minimiser.opener;

import org.springframework.dao.DataAccessException;

import uk.me.gumbley.minimiser.migrator.Migrator;
import uk.me.gumbley.minimiser.persistence.DAOFactory;
import uk.me.gumbley.minimiser.util.InstanceSet;

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
