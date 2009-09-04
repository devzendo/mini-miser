package uk.me.gumbley.minimiser.persistence;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.migrator.DatabaseMigrationNewAppPlugin;
import uk.me.gumbley.minimiser.migrator.PersistenceMigratorHelper;
import uk.me.gumbley.minimiser.migrator.DatabaseMigrationNewAppPlugin.SampleObject;
import uk.me.gumbley.minimiser.opener.DatabaseOpenObserver;
import uk.me.gumbley.minimiser.opener.OpenerAdapter;
import uk.me.gumbley.minimiser.opener.OpenerAdapter.ProgressStage;
import uk.me.gumbley.minimiser.persistence.sql.SQLAccess;
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

    // Tests for the correct issuing of progress reports during
    // the workflow. No database correctness verified - that's
    // below.
    
    private void checkForUpgradedVersions(final InstanceSet<DAOFactory> openDatabase) {
        // TODO Auto-generated method stub
        
    }
    
    private DatabaseMigrationNewAppPlugin.SampleObject findSampleObject(final SimpleJdbcTemplate jdbcTemplate, final String name) throws DataAccessException {
        final String sql = "select name, quantity from SampleObjects where name = ?";
        ParameterizedRowMapper<DatabaseMigrationNewAppPlugin.SampleObject> mapper = new ParameterizedRowMapper<DatabaseMigrationNewAppPlugin.SampleObject>() {
            
            // notice the return type with respect to Java 5 covariant return types
            public DatabaseMigrationNewAppPlugin.SampleObject mapRow(final ResultSet rs, final int rowNum) throws SQLException {
                return new DatabaseMigrationNewAppPlugin.SampleObject(rs.getString("name"), rs.getInt("quantity"));
            }
        };
        return jdbcTemplate.queryForObject(sql, mapper, name);
    }

    private void checkForUpgradedData(final InstanceSet<DAOFactory> openDatabase) {
        final MiniMiserDAOFactory miniMiserDaoFactory = openDatabase.getInstanceOf(MiniMiserDAOFactory.class);
        final SQLAccess sqlAccess = miniMiserDaoFactory.getSQLAccess();
        final SimpleJdbcTemplate simpleJdbcTemplate = sqlAccess.getSimpleJdbcTemplate();
        
        final SampleObject firstSampleObject = findSampleObject(simpleJdbcTemplate, "First");
        Assert.assertNotNull(firstSampleObject);
        Assert.assertEquals("First", firstSampleObject.getName());
        Assert.assertEquals(60, firstSampleObject.getQuantity());
        
        final SampleObject secondSampleObject = findSampleObject(simpleJdbcTemplate, "Second");
        Assert.assertNotNull(secondSampleObject);
        Assert.assertEquals("Second", secondSampleObject.getName());
        Assert.assertEquals(10, secondSampleObject.getQuantity());
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
        
        //checkForUpgradedData(openDatabase);
        checkForUpgradedVersions(openDatabase);
        
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
     * 
     */
    @Test
    public void cannotOpenADatabaseCreatedBySomeOtherApplication() {
        Assert.fail("cannot write this test until the application plugin is explicitly denoted in the versions table");        
    }

    /**
     * @throws PluginException never
     */
    @Test
    public void migrationFailureRollsBackTransaction() throws PluginException {
        Assert.fail("unfinished");
    }
}
