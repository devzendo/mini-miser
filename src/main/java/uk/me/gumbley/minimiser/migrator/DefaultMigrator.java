package uk.me.gumbley.minimiser.migrator;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

import uk.me.gumbley.minimiser.persistence.DAOFactory;
import uk.me.gumbley.minimiser.persistence.MiniMiserDAOFactory;
import uk.me.gumbley.minimiser.persistence.dao.VersionDao;
import uk.me.gumbley.minimiser.persistence.domain.Version;
import uk.me.gumbley.minimiser.persistence.domain.VersionableEntity;
import uk.me.gumbley.minimiser.pluginmanager.Plugin;
import uk.me.gumbley.minimiser.pluginmanager.PluginManager;
import uk.me.gumbley.minimiser.pluginmanager.facade.migratedatabase.DatabaseMigration;
import uk.me.gumbley.minimiser.pluginmanager.facade.migratedatabase.DatabaseMigrationFacade;
import uk.me.gumbley.minimiser.util.InstanceSet;

/**
 * The default database schema migrator.
 * 
 * @author matt
 *
 */
public final class DefaultMigrator implements Migrator {
    private static final Logger LOGGER = Logger
            .getLogger(DefaultMigrator.class);

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
    public MigrationVersion requiresMigration(final InstanceSet<DAOFactory> daoFactories) {
        final PluginSchemaVersions pluginSchemaVersions = getPluginSchemaVersions();
        final PluginSchemaVersions databaseSchemaVersions = getDatabaseSchemaVersions(daoFactories);
        return compareSchemaVersions(pluginSchemaVersions, databaseSchemaVersions);
    }

    /**
     * Compare PluginSchemaVersions for the plugins and database
     * and return their MigratorVersion
     * @param pluginSchemaVersions the schema versions of the plugins
     * @param databaseSchemaVersions the schema versions of the database
     * @return the MigrationVersion state of the comparison, either
     * CURRENT, OLD, or FUTURE
     */
    public static MigrationVersion compareSchemaVersions(
            final PluginSchemaVersions pluginSchemaVersions,
            final PluginSchemaVersions databaseSchemaVersions) {
        final int compareTo = pluginSchemaVersions.compareTo(databaseSchemaVersions);
        if (compareTo <= -1) {
            return MigrationVersion.FUTURE;
        } else if (compareTo >= 1) {
            return MigrationVersion.OLD;
        } else {
            return MigrationVersion.CURRENT;
        }
    }

    /**
     * Obtain the schema versions for the current set of plugins,
     * as stored in the database.
     * Made default visibility for unit testing.
     * @param daoFactories the DAO Factories
     * @return the PluginSchemaVersions holding the database schema
     * versions
     */
    PluginSchemaVersions getDatabaseSchemaVersions(
            final InstanceSet<DAOFactory> daoFactories) {
        final PluginSchemaVersions psv = new PluginSchemaVersions();
        final VersionDao versionDao = daoFactories
            .getInstanceOf(MiniMiserDAOFactory.class)
            .getVersionDao();
        final List<String> pluginNames = getPluginNames();
        for (final String pluginName : pluginNames) {
            final Version schemaVersion = versionDao.findVersion(pluginName, VersionableEntity.SCHEMA_VERSION);
            psv.addPluginSchemaVersion(pluginName, schemaVersion.getVersion());
        }
        return psv;
    }

    private List<String> getPluginNames() {
        final List<String> names = new ArrayList<String>();
        final List<Plugin> plugins = mPluginManager.getPlugins();
        for (final Plugin plugin : plugins) {
            names.add(plugin.getName());
        }
        return names;
    }

    /**
     * Obtain the schema versions for the current set of plugins.
     * Made default visibility for unit testing.
     * @return the PluginSchemaVersions holding the plugin schema
     * versions
     */
    PluginSchemaVersions getPluginSchemaVersions() {
        final PluginSchemaVersions psv = new PluginSchemaVersions();
        final List<Plugin> plugins = mPluginManager.getPlugins();
        for (final Plugin plugin : plugins) {
            psv.addPluginSchemaVersion(plugin.getName(), plugin.getSchemaVersion());
        }
        return psv;
    }

    /**
     * {@inheritDoc}
     */
    public void migrate(final InstanceSet<DAOFactory> daoFactories)
            throws DataAccessException {
        final MiniMiserDAOFactory miniMiserDaoFactory = daoFactories.getInstanceOf(MiniMiserDAOFactory.class);
        final VersionDao versionDao = miniMiserDaoFactory.getVersionDao();
        final List<DatabaseMigration> databaseMigrationPlugins = mPluginManager.getPluginsImplementingFacade(DatabaseMigration.class);
        for (final DatabaseMigration databaseMigration : databaseMigrationPlugins) {
            final String pluginName = ((Plugin)databaseMigration).getName();
            final DatabaseMigrationFacade databaseMigrationFacade = databaseMigration.getDatabaseMigrationFacade();
            if (databaseMigrationFacade != null) {
                final Version schemaVersion = versionDao.findVersion(pluginName, VersionableEntity.SCHEMA_VERSION);
                final String schemaVersionString = schemaVersion.getVersion();
                LOGGER.debug("Plugin " + databaseMigration.getClass().getName() + " '" + pluginName + "' migrating database from version " + schemaVersionString);
                databaseMigrationFacade.migrateSchema(schemaVersionString);
            }
        }
    }
}
