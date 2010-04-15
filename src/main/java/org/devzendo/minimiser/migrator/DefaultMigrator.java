/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devzendo.minimiser.migrator;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.persistence.DAOFactory;
import org.devzendo.minimiser.persistence.MiniMiserDAOFactory;
import org.devzendo.minimiser.persistence.dao.VersionsDao;
import org.devzendo.minimiser.persistence.domain.Version;
import org.devzendo.minimiser.persistence.domain.VersionableEntity;
import org.devzendo.minimiser.plugin.ApplicationPlugin;
import org.devzendo.minimiser.plugin.Plugin;
import org.devzendo.minimiser.plugin.facade.migratedatabase.DatabaseMigration;
import org.devzendo.minimiser.plugin.facade.migratedatabase.DatabaseMigrationFacade;
import org.devzendo.minimiser.pluginmanager.PluginManager;
import org.devzendo.minimiser.util.InstanceSet;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;


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
     * and return their MigrationVersion
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
        final VersionsDao versionsDao = daoFactories
            .getInstanceOf(MiniMiserDAOFactory.class)
            .getVersionDao();
        final List<String> pluginNames = getPluginNames();
        for (final String pluginName : pluginNames) {
            LOGGER.debug("Finding schema version stored for plugin " + pluginName);
            final Version schemaVersion = versionsDao.findVersion(pluginName, VersionableEntity.SCHEMA_VERSION);
            LOGGER.debug("Schema version for plugin " + pluginName + " is " + schemaVersion.getVersion());
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
        final VersionsDao versionsDao = miniMiserDaoFactory.getVersionDao();
        final DataSource dataSource = miniMiserDaoFactory.getSQLAccess().getDataSource();
        final SimpleJdbcTemplate simpleJdbcTemplate = miniMiserDaoFactory.getSQLAccess().getSimpleJdbcTemplate();
        migrateFacades(versionsDao, dataSource, simpleJdbcTemplate);
        updateCurrentSchemaVersions(versionsDao);
        updateCurrentVersions(versionsDao);
    }

    private void migrateFacades(
            final VersionsDao versionsDao,
            final DataSource dataSource,
            final SimpleJdbcTemplate simpleJdbcTemplate) {
        for (final DatabaseMigration databaseMigration : mPluginManager.getPluginsImplementingFacade(DatabaseMigration.class)) {
            final String pluginName = ((Plugin) databaseMigration).getName();
            final DatabaseMigrationFacade databaseMigrationFacade = databaseMigration.getDatabaseMigrationFacade();
            if (databaseMigrationFacade != null) {
                final Version schemaVersion = versionsDao.findVersion(pluginName, VersionableEntity.SCHEMA_VERSION);
                final String schemaVersionString = schemaVersion.getVersion();
                LOGGER.info(
                    "Plugin " + databaseMigration.getClass().getName() + " '"
                    + pluginName + "' migrating database from version "
                    + schemaVersionString);
                databaseMigrationFacade.migrateSchema(dataSource, simpleJdbcTemplate, schemaVersionString);
                LOGGER.info(
                    "Plugin " + databaseMigration.getClass().getName() + " '"
                    + pluginName + "' migrated database from version "
                    + schemaVersionString);
            }
        }
    }

    private void updateCurrentSchemaVersions(final VersionsDao versionsDao) {
        final ApplicationPlugin appPlugin = mPluginManager.getApplicationPlugin();
        for (final Plugin plugin : mPluginManager.getPlugins()) {
            final String pluginName = plugin.getName();
            final String pluginSchemaVersion = plugin.getSchemaVersion();
            LOGGER.info("Plugin " + plugin.getClass().getName() + " '"
                + pluginName + "' schema version is now "
                + pluginSchemaVersion);
            versionsDao.persistVersion(
                new Version(
                    pluginName,
                    VersionableEntity.SCHEMA_VERSION,
                    plugin == appPlugin,
                    pluginSchemaVersion));
        }
    }

    private void updateCurrentVersions(final VersionsDao versionsDao) {
        final ApplicationPlugin appPlugin = mPluginManager.getApplicationPlugin();
        for (final Plugin plugin : mPluginManager.getPlugins()) {
            final String pluginName = plugin.getName();
            final String pluginVersion = plugin.getVersion();
            LOGGER.info("Plugin " + plugin.getClass().getName() + " '"
                + pluginName + "' plugin version is now "
                + pluginVersion);
            versionsDao.persistVersion(
                new Version(pluginName,
                    VersionableEntity.PLUGIN_CODE_VERSION,
                    plugin == appPlugin,
                    pluginVersion));
        }
    }

}
