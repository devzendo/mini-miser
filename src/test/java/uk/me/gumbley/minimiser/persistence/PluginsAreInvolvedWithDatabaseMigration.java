package uk.me.gumbley.minimiser.persistence;

import java.io.File;

import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataAccessException;

import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.migrator.DatabaseMigrationNewAppPlugin;
import uk.me.gumbley.minimiser.migrator.DatabaseMigrationNewFailMigrationAppPlugin;
import uk.me.gumbley.minimiser.migrator.PersistenceMigratorHelper;
import uk.me.gumbley.minimiser.opener.DatabaseOpenObserver;
import uk.me.gumbley.minimiser.opener.OpenerAdapter;
import uk.me.gumbley.minimiser.opener.OpenerAdapter.ProgressStage;
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
    
    private void checkForClosureViaLockFileMissing(final String dbName) {
        // Although it looks closed, it had been opened before
        // check for migration. Since we rejected, it must be
        // closed. Since we don't get back a database object, 
        // we have to do a low-level check like this...
        final File lockFile = new File(mPersistencePluginHelper.getAbsoluteDatabaseDirectory(dbName) + ".lock.db");
        LOGGER.info("lockfile: " + lockFile.getAbsolutePath());
        Assert.assertFalse(lockFile.exists());
    }

    /**
     * @throws PluginException never
     */
    @Test
    public void migrationCanBePreventedByUser() throws PluginException {
        // setup
        final String dbName = MIGRATIONDB + "prevent";
        mPersistenceMigratorHelper.createOldDatabase(dbName);

        final DatabaseOpenObserver obs = new DatabaseOpenObserver();
        mPersistencePluginOpenerHelper.addDatabaseOpenObserver(obs);
        mPersistencePluginHelper.loadPlugins("uk/me/gumbley/minimiser/migrator/persistencemigrationnewplugin.properties");
        final OpenerAdapter openerAdapter = EasyMock.createNiceMock(OpenerAdapter.class);
        EasyMock.checkOrder(openerAdapter, true);
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
        mPersistencePluginOpenerHelper.openDatabase(dbName, openerAdapter);
        
        // test
        obs.assertDatabaseNotOpen();
        EasyMock.verify(openerAdapter);
        checkForClosureViaLockFileMissing(dbName);
    }

    /**
     * @throws PluginException never
     */
    @Test
    public void migrationAccepted() throws PluginException {
        // setup
        final String dbName = MIGRATIONDB + "accepted";
        mPersistenceMigratorHelper.createOldDatabase(dbName);

        final DatabaseOpenObserver obs = new DatabaseOpenObserver();
        mPersistencePluginOpenerHelper.addDatabaseOpenObserver(obs);
        mPersistencePluginHelper.loadPlugins("uk/me/gumbley/minimiser/migrator/persistencemigrationnewplugin.properties");
        final OpenerAdapter openerAdapter = EasyMock.createNiceMock(OpenerAdapter.class);
        EasyMock.checkOrder(openerAdapter, true);
        openerAdapter.startOpening();
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.STARTING), EasyMock.isA(String.class));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.OPENING), EasyMock.isA(String.class));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.MIGRATION_REQUIRED), EasyMock.isA(String.class));
        openerAdapter.requestMigration();
        EasyMock.expectLastCall().andReturn(true);
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.MIGRATING), EasyMock.isA(String.class));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.MIGRATED), EasyMock.isA(String.class));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.OPENED), EasyMock.isA(String.class));
        openerAdapter.stopOpening();
        EasyMock.replay(openerAdapter);

        // run
        LOGGER.info("test is opening the database, with migration to complete");
        final InstanceSet<DAOFactory> openDatabase = mPersistencePluginOpenerHelper.openDatabase(dbName, openerAdapter);
        
        mPersistenceMigratorHelper.checkForUpgradedData(openDatabase);
        mPersistenceMigratorHelper.checkForUpgradedVersions(openDatabase);

        try {
            // test
            obs.assertDatabaseOpen();
            EasyMock.verify(openerAdapter);
            Assert.assertNotNull(openDatabase);
            final DatabaseMigrationNewAppPlugin appPlugin = (DatabaseMigrationNewAppPlugin) mPersistencePluginHelper.getApplicationPlugin();
            Assert.assertTrue(appPlugin.allMigrationMethodsCalled());
            Assert.assertEquals("1.0", appPlugin.getPreMigrationSchemaVersion());
        } finally {
            try {
                openDatabase.getInstanceOf(MiniMiserDAOFactory.class).close();
            } catch (final Exception e) {
                LOGGER.warn("Could not close database: " + e.getMessage(), e);
            }
        }
    }

    /**
     * @throws PluginException never
     */
    @Test
    public void cannotOpenADatabaseNewerThanThisSchema() throws PluginException {
        // setup
        final String dbName = MIGRATIONDB + "future";
        mPersistenceMigratorHelper.createNewDatabase(dbName);
    
        final DatabaseOpenObserver obs = new DatabaseOpenObserver();
        mPersistencePluginOpenerHelper.addDatabaseOpenObserver(obs);
        mPersistencePluginHelper.loadPlugins("uk/me/gumbley/minimiser/migrator/persistencemigrationoldplugin.properties");
        final OpenerAdapter openerAdapter = EasyMock.createNiceMock(OpenerAdapter.class);
        EasyMock.checkOrder(openerAdapter, true);
        openerAdapter.startOpening();
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.STARTING), EasyMock.isA(String.class));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.OPENING), EasyMock.isA(String.class));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.MIGRATION_NOT_POSSIBLE), EasyMock.isA(String.class));
        openerAdapter.migrationNotPossible();
        openerAdapter.stopOpening();
        EasyMock.replay(openerAdapter);
    
        // run
        LOGGER.info("test is opening the database, with migration not possible");
        mPersistencePluginOpenerHelper.openDatabase(dbName, openerAdapter);
        
        // test
        obs.assertDatabaseNotOpen();
        EasyMock.verify(openerAdapter);
        checkForClosureViaLockFileMissing(dbName);
    }

    /**
     * @throws PluginException on error - never
     */
    @Test
    public void cannotOpenADatabaseCreatedBySomeOtherApplication() throws PluginException {
        // setup
        final String dbName = MIGRATIONDB + "otherapp";
        mPersistenceMigratorHelper.createOtherApplicationDatabase(dbName);

        final DatabaseOpenObserver obs = new DatabaseOpenObserver();
        mPersistencePluginOpenerHelper.addDatabaseOpenObserver(obs);
        mPersistencePluginHelper.loadPlugins("uk/me/gumbley/minimiser/migrator/persistencemigrationoldplugin.properties");
        final OpenerAdapter openerAdapter = EasyMock.createNiceMock(OpenerAdapter.class);
        EasyMock.checkOrder(openerAdapter, true);
        openerAdapter.startOpening();
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.STARTING), EasyMock.isA(String.class));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.OPENING), EasyMock.isA(String.class));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.OTHER_APPLICATION_DATABASE), EasyMock.isA(String.class));
        openerAdapter.createdByOtherApplication();
        openerAdapter.stopOpening();
        EasyMock.replay(openerAdapter);
    
        // run
        LOGGER.info("test is opening the database created by another app");
        mPersistencePluginOpenerHelper.openDatabase(dbName, openerAdapter);
        
        // test
        obs.assertDatabaseNotOpen();
        EasyMock.verify(openerAdapter);
        checkForClosureViaLockFileMissing(dbName);
    }

    /**
     * @throws PluginException never
     */
    @Test
    public void migrationFailureRollsBackTransaction() throws PluginException {
        // setup
        final String dbName = MIGRATIONDB + "rollback";
        mPersistenceMigratorHelper.createOldDatabase(dbName);

        final DatabaseOpenObserver obs = new DatabaseOpenObserver();
        mPersistencePluginOpenerHelper.addDatabaseOpenObserver(obs);
        mPersistencePluginHelper.loadPlugins("uk/me/gumbley/minimiser/migrator/persistencemigrationnewfailmigrationplugin.properties");
        final OpenerAdapter openerAdapter = EasyMock.createNiceMock(OpenerAdapter.class);
        EasyMock.checkOrder(openerAdapter, true);
        openerAdapter.startOpening();
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.STARTING), EasyMock.isA(String.class));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.OPENING), EasyMock.isA(String.class));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.MIGRATION_REQUIRED), EasyMock.isA(String.class));
        openerAdapter.requestMigration();
        EasyMock.expectLastCall().andReturn(true);
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.MIGRATING), EasyMock.isA(String.class));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.MIGRATION_FAILED), EasyMock.isA(String.class));
        openerAdapter.migrationFailed(EasyMock.isA(DataAccessException.class));
        openerAdapter.stopOpening();
        EasyMock.replay(openerAdapter);

        // run
        LOGGER.info("test is opening the database, with migration to complete with failure");
        final InstanceSet<DAOFactory> openDatabase = mPersistencePluginOpenerHelper.openDatabase(dbName, openerAdapter);
        Assert.assertNull(openDatabase);

        // Now re-open without the opener so we can test that the
        // upgrade has been rolled back. We have to re-open since
        // the database would have been closed as part of the
        // failed upgrade, and we don't have openDatabase above.
        // We open without the opener, as we don't want to trigger
        // a migration again.
        final InstanceSet<DAOFactory> reopenedDatabase = mPersistencePluginHelper.openDatabase(dbName, "");
        
        try {
            // test
            mPersistenceMigratorHelper.checkForNoUpgradedData(reopenedDatabase);
            mPersistenceMigratorHelper.checkForNoUpgradedVersions(reopenedDatabase);
            obs.assertDatabaseNotOpen();
            EasyMock.verify(openerAdapter);
            final DatabaseMigrationNewFailMigrationAppPlugin appPlugin =
                (DatabaseMigrationNewFailMigrationAppPlugin) mPersistencePluginHelper.
                getApplicationPlugin();
            Assert.assertTrue(appPlugin.allMigrationMethodsCalled());
            Assert.assertEquals("1.0", appPlugin.getPreMigrationSchemaVersion());
        } finally {
            try {
                reopenedDatabase.getInstanceOf(MiniMiserDAOFactory.class).close();
            } catch (final Exception e) {
                LOGGER.warn("Could not close database: " + e.getMessage(), e);
            }
        }
    }
}
