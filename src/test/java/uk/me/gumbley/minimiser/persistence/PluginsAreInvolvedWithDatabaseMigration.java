package uk.me.gumbley.minimiser.persistence;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.opener.DatabaseOpenObserver;
import uk.me.gumbley.minimiser.opener.OpenerAdapter;
import uk.me.gumbley.minimiser.opener.OpenerAdapter.ProgressStage;
import uk.me.gumbley.minimiser.persistence.dao.VersionDao;
import uk.me.gumbley.minimiser.persistence.domain.Version;
import uk.me.gumbley.minimiser.persistence.domain.VersionableEntity;
import uk.me.gumbley.minimiser.pluginmanager.ApplicationPlugin;
import uk.me.gumbley.minimiser.pluginmanager.PluginException;
import uk.me.gumbley.minimiser.util.InstanceSet;


/**
 * The tests for the migration workflow in the opener.
 * 
 * Creates a database with an old plugin's schema version, then
 * opens it with the opener, and ensures that the opener adapter
 * is called to notify the user of the migration. Ensures that the
 * responses from the opener adapter cause the correct actions.
 * 
 * @author matt
 *
 */
public final class PluginsAreInvolvedWithDatabaseMigration extends LoggingTestCase {
    private static final String MIGRATIONDB = "openermigration";
    private PersistencePluginHelper mPersistencePluginHelper;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        mPersistencePluginHelper = new PersistencePluginHelper();
        mPersistencePluginHelper.validateTestDatabaseDirectory();
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
    public void oldDatabaseCanBeCreated() throws PluginException {
        createOldDatabase();
        
        mPersistencePluginHelper.loadPlugins("uk/me/gumbley/minimiser/persistence/persistencemigrationoldplugin.properties");
        final InstanceSet<DAOFactory> daoFactories = mPersistencePluginHelper.openDatabase(MIGRATIONDB, "");
        try {
            final MiniMiserDAOFactory miniMiserDAOFactory = daoFactories.getInstanceOf(MiniMiserDAOFactory.class);
            final VersionDao versionDao = miniMiserDAOFactory.getVersionDao();
            final ApplicationPlugin appPlugin = mPersistencePluginHelper.getApplicationPlugin();
            final Version version = versionDao.findVersion(appPlugin.getName(), VersionableEntity.SCHEMA_VERSION);
            Assert.assertEquals("1.0", version.getVersion());
        } finally {
            daoFactories.getInstanceOf(MiniMiserDAOFactory.class).close();
        }
    }
    
    /**
     * @throws PluginException never
     */
    @Test
    public void migrationCanBePreventedByUser() throws PluginException {
        createOldDatabase();
        final DatabaseOpenObserver obs = new DatabaseOpenObserver();
        mPersistencePluginHelper.addDatabaseOpenObserver(obs);
        mPersistencePluginHelper.loadPlugins("uk/me/gumbley/minimiser/persistence/persistencemigrationnewplugin.properties");
        final OpenerAdapter openerAdapter = EasyMock.createMock(OpenerAdapter.class);
        openerAdapter.startOpening();
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.STARTING), EasyMock.isA(String.class));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.OPENING), EasyMock.isA(String.class));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.MIGRATION_REQUIRED), EasyMock.isA(String.class));
        openerAdapter.requestMigration();
        EasyMock.expectLastCall().andReturn(false);
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.MIGRATION_REJECTED), EasyMock.isA(String.class));
        openerAdapter.stopOpening();
        EasyMock.replay(openerAdapter);

        mPersistencePluginHelper.openDatabase(MIGRATIONDB, openerAdapter);
        
        obs.assertDatabaseNotOpen();
        EasyMock.verify(openerAdapter);
    }
    
    /**
     * @throws PluginException never
     */
    @Test
    public void migrationAcceptedAndUpgradesViaPluginWithLatestSchemaVersionStored() throws PluginException {
        Assert.fail("unfinished");
    }
    
    /**
     * 
     */
    @Test
    public void cannotOpenADatabaseNewerThanThisSchema() {
        Assert.fail("unfinished");        
    }
    
    /**
     * 
     */
    @Test
    public void cannotOpenADatabaseCreatedBySomeOtherApplication() {
        Assert.fail("unfinished");        
    }
}
