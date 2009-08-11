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
    private Migrator mMigrator;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        mPersistencePluginHelper = new PersistencePluginHelper();
        mPersistencePluginHelper.validateTestDatabaseDirectory();
        
        mMigrator = new DefaultMigrator(mPersistencePluginHelper.getPluginManager());
    }

    /**
     * 
     */
    @After
    public void removeTestDatabases() {
        mPersistencePluginHelper.deleteCreatedDatabases();
    }
    
    private void createOldDatabase() throws PluginException {
        // need a helper separate from the main one since it needs
        // to have old plugins loaded, and the main one loads the
        // new plugins
        final PersistencePluginHelper persistencePluginHelper = new PersistencePluginHelper();
        persistencePluginHelper.loadPlugins("uk/me/gumbley/minimiser/persistence/persistencemigrationoldplugin.properties");
        persistencePluginHelper.createDatabase(MIGRATIONDB, "").getInstanceOf(MiniMiserDAOFactory.class).close();
        
        // Let the main helper delete the old database
        mPersistencePluginHelper.addDatabaseToDelete(MIGRATIONDB);
    }

    /**
     * @throws PluginException never
     */
    @Test
    public void oldDatabaseIsDetectedAsNeedingMigration() throws PluginException {
        createOldDatabase();
        mPersistencePluginHelper.loadPlugins("uk/me/gumbley/minimiser/persistence/persistencemigrationoldplugin.properties");
        final InstanceSet<DAOFactory> daoFactories = mPersistencePluginHelper.openDatabase(MIGRATIONDB, "");
        Assert.assertNotNull(daoFactories);
        
        try {
            Assert.assertTrue(mMigrator.requiresMigration(daoFactories));
            
        } finally {
            daoFactories.getInstanceOf(MiniMiserDAOFactory.class).close();
        }
    }
}
