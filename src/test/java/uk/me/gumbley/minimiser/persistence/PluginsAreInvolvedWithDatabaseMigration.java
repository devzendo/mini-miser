package uk.me.gumbley.minimiser.persistence;

import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.migrator.PersistenceMigratorHelper;
import uk.me.gumbley.minimiser.opener.DatabaseOpenObserver;
import uk.me.gumbley.minimiser.opener.OpenerAdapter;
import uk.me.gumbley.minimiser.opener.OpenerAdapter.ProgressStage;
import uk.me.gumbley.minimiser.pluginmanager.PluginException;


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
    private static final Logger LOGGER = Logger
            .getLogger(PluginsAreInvolvedWithDatabaseMigration.class);
    private static final String MIGRATIONDB = "openermigration";
    private PersistencePluginHelper mPersistencePluginHelper;
    private PersistenceMigratorHelper mPersistenceMigratorHelper;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        mPersistencePluginHelper = new PersistencePluginHelper();
        mPersistencePluginHelper.validateTestDatabaseDirectory();
        
        mPersistenceMigratorHelper = new PersistenceMigratorHelper(mPersistencePluginHelper);
    }

    /**
     * 
     */
    @After
    public void removeTestDatabases() {
        LOGGER.info("tidying up");
        mPersistencePluginHelper.tidyTestDatabasesDirectory();
    }
    
    /**
     * @throws PluginException never
     */
    @Test
    public void migrationCanBePreventedByUser() throws PluginException {
        // setup
        mPersistenceMigratorHelper.createOldDatabase(MIGRATIONDB);

        final DatabaseOpenObserver obs = new DatabaseOpenObserver();
        mPersistencePluginHelper.addDatabaseOpenObserver(obs);
        mPersistencePluginHelper.loadPlugins("uk/me/gumbley/minimiser/migrator/persistencemigrationnewplugin.properties");
        final OpenerAdapter openerAdapter = EasyMock.createNiceMock(OpenerAdapter.class);
        openerAdapter.startOpening();
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.STARTING), EasyMock.isA(String.class));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.OPENING), EasyMock.isA(String.class));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.MIGRATION_REQUIRED), EasyMock.isA(String.class));
        openerAdapter.requestMigration();
        EasyMock.expectLastCall().andReturn(false);
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.MIGRATION_REJECTED), EasyMock.isA(String.class));
        openerAdapter.stopOpening();
        EasyMock.replay(openerAdapter);

        // run
        mPersistencePluginHelper.openDatabase(MIGRATIONDB, openerAdapter);
        
        // test
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
