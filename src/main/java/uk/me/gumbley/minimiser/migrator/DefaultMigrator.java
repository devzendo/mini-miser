package uk.me.gumbley.minimiser.migrator;

import uk.me.gumbley.minimiser.persistence.DAOFactory;
import uk.me.gumbley.minimiser.pluginmanager.PluginManager;
import uk.me.gumbley.minimiser.util.InstanceSet;

/**
 * The default database schema migrator.
 * 
 * @author matt
 *
 */
public final class DefaultMigrator implements Migrator {

    private final PluginManager mPluginManager;

    /**
     * Construct the default migrator, given the plugin manager
     * which will be used to query plugins for MigrateDatabase
     * facades.
     * @param pluginManager the plugin manager
     */
    public DefaultMigrator(final PluginManager pluginManager) {
        mPluginManager = pluginManager;
    }

    /**
     * {@inheritDoc}
     */
    public boolean requiresMigration(final InstanceSet<DAOFactory> daoFactories) {
        // TODO Auto-generated method stub
        return false;
    }
}
