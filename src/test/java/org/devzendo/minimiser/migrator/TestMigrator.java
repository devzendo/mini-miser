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

import org.devzendo.commoncode.logging.LoggingUnittestHelper;
import org.devzendo.minimiser.persistence.DAOFactory;
import org.devzendo.minimiser.persistence.MiniMiserDAOFactory;
import org.devzendo.minimiser.persistence.PersistencePluginHelper;
import org.devzendo.minimiser.pluginmanager.PluginException;
import org.devzendo.minimiser.pluginmanager.PluginHelper;
import org.devzendo.minimiser.pluginmanager.PluginHelperFactory;
import org.devzendo.minimiser.util.InstanceSet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;



/**
 * Tests the migration of databases from old to new versions of
 * schema.
 *
 * @author matt
 *
 */
public final class TestMigrator {
    private static final String MIGRATIONDB = "migrator";
    private PluginHelper mPluginHelper;
    private PersistencePluginHelper mPersistencePluginHelper;
    private PersistenceMigratorHelper mPersistenceMigratorHelper;
    private Migrator mMigrator;

    /**
     * 
     */
    @BeforeClass
    public static void setupLogging() {
        LoggingUnittestHelper.setupLogging();
    }

    /**
     *
     */
    @Before
    public void getPrerequisites() {
        mPluginHelper = PluginHelperFactory.createPluginHelper();
        mPersistencePluginHelper = new PersistencePluginHelper(false, mPluginHelper);
        mPersistencePluginHelper.validateTestDatabaseDirectory();

        mPersistenceMigratorHelper = new PersistenceMigratorHelper(mPluginHelper, mPersistencePluginHelper);

        mMigrator = new DefaultMigrator(mPluginHelper.getPluginManager());
    }

    /**
     *
     */
    @After
    public void removeTestDatabases() {
        mPersistencePluginHelper.tidyTestDatabasesDirectory();
    }

    /**
     * @throws PluginException never
     */
    @Test
    public void oldDatabaseIsDetectedAsNeedingMigration() throws PluginException {
        // setup
        mPersistenceMigratorHelper.createOldDatabase(MIGRATIONDB);
        mPluginHelper.loadPlugins("org/devzendo/minimiser/migrator/persistencemigrationnewplugin.properties");
        final InstanceSet<DAOFactory> daoFactories = mPersistencePluginHelper.openDatabase(MIGRATIONDB, "");
        Assert.assertNotNull(daoFactories);

        // preliminary test - can the schema versions be retrieved from the database?
        final PluginSchemaVersions databaseSchemaVersions = ((DefaultMigrator) mMigrator).getDatabaseSchemaVersions(daoFactories);
        Assert.assertEquals("DatabaseMigrationAppPlugin:1.0", databaseSchemaVersions.toString());
        // preliminary test - can the plugin schema versions be retrieved?
        final PluginSchemaVersions pluginSchemaVersions = ((DefaultMigrator) mMigrator).getPluginSchemaVersions();
        Assert.assertEquals("DatabaseMigrationAppPlugin:2.0", pluginSchemaVersions.toString());

        try {
            // test
            Assert.assertEquals(Migrator.MigrationVersion.OLD, mMigrator.requiresMigration(daoFactories));

        } finally {
            // teardown
            daoFactories.getInstanceOf(MiniMiserDAOFactory.class).close();
        }
    }

    /**
     * @throws PluginException never
     */
    @Test
    public void currentDatabaseIsDetectedAsNotNeedingMigration() throws PluginException {
        // setup
        mPersistenceMigratorHelper.createOldDatabase(MIGRATIONDB);
        mPluginHelper.loadPlugins("org/devzendo/minimiser/migrator/persistencemigrationoldplugin.properties");
        final InstanceSet<DAOFactory> daoFactories = mPersistencePluginHelper.openDatabase(MIGRATIONDB, "");
        Assert.assertNotNull(daoFactories);

        // preliminary test - can the schema versions be retrieved from the database?
        final PluginSchemaVersions databaseSchemaVersions = ((DefaultMigrator) mMigrator).getDatabaseSchemaVersions(daoFactories);
        Assert.assertEquals("DatabaseMigrationAppPlugin:1.0", databaseSchemaVersions.toString());
        // preliminary test - can the plugin schema versions be retrieved?
        final PluginSchemaVersions pluginSchemaVersions = ((DefaultMigrator) mMigrator).getPluginSchemaVersions();
        Assert.assertEquals("DatabaseMigrationAppPlugin:1.0", pluginSchemaVersions.toString());

        try {
            // test
            Assert.assertEquals(Migrator.MigrationVersion.CURRENT, mMigrator.requiresMigration(daoFactories));

        } finally {
            // teardown
            daoFactories.getInstanceOf(MiniMiserDAOFactory.class).close();
        }
    }

    /**
     * @throws PluginException never
     */
    @Test
    public void futureDatabaseCannotBeMigrated() throws PluginException {
        // setup
        mPersistenceMigratorHelper.createNewDatabase(MIGRATIONDB);
        mPluginHelper.loadPlugins("org/devzendo/minimiser/migrator/persistencemigrationoldplugin.properties");
        final InstanceSet<DAOFactory> daoFactories = mPersistencePluginHelper.openDatabase(MIGRATIONDB, "");
        Assert.assertNotNull(daoFactories);

        // preliminary test - can the schema versions be retrieved from the database?
        final PluginSchemaVersions databaseSchemaVersions = ((DefaultMigrator) mMigrator).getDatabaseSchemaVersions(daoFactories);
        Assert.assertEquals("DatabaseMigrationAppPlugin:2.0", databaseSchemaVersions.toString());
        // preliminary test - can the plugin schema versions be retrieved?
        final PluginSchemaVersions pluginSchemaVersions = ((DefaultMigrator) mMigrator).getPluginSchemaVersions();
        Assert.assertEquals("DatabaseMigrationAppPlugin:1.0", pluginSchemaVersions.toString());

        try {
            // test
            Assert.assertEquals(Migrator.MigrationVersion.FUTURE, mMigrator.requiresMigration(daoFactories));

        } finally {
            // teardown
            daoFactories.getInstanceOf(MiniMiserDAOFactory.class).close();
        }
    }

    /**
     * @throws PluginException never
     */
    @Test
    public void oldDatabaseIsMigrated() throws PluginException {
        // setup
        mPersistenceMigratorHelper.createOldDatabase(MIGRATIONDB);
        mPluginHelper.loadPlugins("org/devzendo/minimiser/migrator/persistencemigrationnewplugin.properties");
        final InstanceSet<DAOFactory> daoFactories = mPersistencePluginHelper.openDatabase(MIGRATIONDB, "");
        Assert.assertNotNull(daoFactories);

        try {
            mMigrator.migrate(daoFactories);

            // test that migration has occurred
            mPersistenceMigratorHelper.checkForUpgradedData(daoFactories);
            mPersistenceMigratorHelper.checkForUpgradedVersions(daoFactories);

            final DatabaseMigrationNewAppPlugin applicationPlugin = (DatabaseMigrationNewAppPlugin) mPluginHelper.getApplicationPlugin();
            Assert.assertTrue(applicationPlugin.allMigrationMethodsCalled());
            // test that the versions have been updated
            Assert.assertEquals(Migrator.MigrationVersion.CURRENT, mMigrator.requiresMigration(daoFactories));

        } finally {
            // teardown
            daoFactories.getInstanceOf(MiniMiserDAOFactory.class).close();
        }
    }
}
