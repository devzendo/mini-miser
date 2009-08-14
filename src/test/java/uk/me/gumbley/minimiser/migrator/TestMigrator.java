package uk.me.gumbley.minimiser.migrator;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.persistence.DAOFactory;
import uk.me.gumbley.minimiser.persistence.MiniMiserDAOFactory;
import uk.me.gumbley.minimiser.persistence.PersistencePluginHelper;
import uk.me.gumbley.minimiser.pluginmanager.PluginException;
import uk.me.gumbley.minimiser.util.InstanceSet;


/**
 * Tests the migration of databases from old to new versions of
 * schema.
 * 
 * @author matt
 *
 */
public final class TestMigrator extends LoggingTestCase {
    private static final String MIGRATIONDB = "migrator";
    private PersistencePluginHelper mPersistencePluginHelper;
    private PersistenceMigratorHelper mPersistenceMigratorHelper;
    private Migrator mMigrator;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        mPersistencePluginHelper = new PersistencePluginHelper();
        mPersistencePluginHelper.validateTestDatabaseDirectory();
        
        mPersistenceMigratorHelper = new PersistenceMigratorHelper(mPersistencePluginHelper);
        
        mMigrator = new DefaultMigrator(mPersistencePluginHelper.getPluginManager());
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
        mPersistencePluginHelper.loadPlugins("uk/me/gumbley/minimiser/migrator/persistencemigrationnewplugin.properties");
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
        mPersistencePluginHelper.loadPlugins("uk/me/gumbley/minimiser/migrator/persistencemigrationoldplugin.properties");
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
        mPersistencePluginHelper.loadPlugins("uk/me/gumbley/minimiser/migrator/persistencemigrationoldplugin.properties");
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

}
