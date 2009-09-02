package uk.me.gumbley.minimiser.persistence;

import java.io.File;

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
    private PersistencePluginOpenerHelper mPersistencePluginOpenerHelper;
    private PersistenceMigratorHelper mPersistenceMigratorHelper;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        mPersistencePluginHelper = new PersistencePluginHelper();
        mPersistencePluginHelper.validateTestDatabaseDirectory();

        mPersistencePluginOpenerHelper = new PersistencePluginOpenerHelper(mPersistencePluginHelper);
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
        mPersistencePluginOpenerHelper.addDatabaseOpenObserver(obs);
        mPersistencePluginHelper.loadPlugins("uk/me/gumbley/minimiser/migrator/persistencemigrationnewplugin.properties");
        final OpenerAdapter openerAdapter = EasyMock.createStrictMock(OpenerAdapter.class);
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
        LOGGER.info("test is opening the database, with migration to be rejected by the user");
        mPersistencePluginOpenerHelper.openDatabase(MIGRATIONDB, openerAdapter);
        
        // test
        obs.assertDatabaseNotOpen();
        EasyMock.verify(openerAdapter);
        // Although it looks closed, it had been opened before
        // check for migration. Since we rejected, it must be
        // closed. Since we don't get back a database object, 
        // we have to do a low-level check like this...
        final File lockFile = new File(mPersistencePluginHelper.getAbsoluteDatabaseDirectory(MIGRATIONDB) + ".lock.db");
        LOGGER.info("lockfile: " + lockFile.getAbsolutePath());
        Assert.assertFalse(lockFile.exists());
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
