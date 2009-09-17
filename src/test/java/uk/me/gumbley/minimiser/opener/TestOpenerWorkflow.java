package uk.me.gumbley.minimiser.opener;

import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.opener.OpenerAdapter.ProgressStage;
import uk.me.gumbley.minimiser.persistence.DAOFactory;
import uk.me.gumbley.minimiser.persistence.MiniMiserDAOFactory;
import uk.me.gumbley.minimiser.persistence.PersistencePluginHelper;
import uk.me.gumbley.minimiser.persistence.PersistencePluginOpenerHelper;
import uk.me.gumbley.minimiser.util.InstanceSet;

/**
 * Tests the simpler cases of Opener workflow. e.g. opening, password entry,
 * etc. Note that migration is not covered here, see
 * PluginsAreInvolvedWithDatabaseMigration
 * 
 * @author matt
 * 
 */
public final class TestOpenerWorkflow extends LoggingTestCase {
    private static final Logger LOGGER = Logger
            .getLogger(TestOpenerWorkflow.class);
    private PersistencePluginHelper mPersistencePluginHelper;
    private PersistencePluginOpenerHelper mPersistencePluginOpenerHelper;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        mPersistencePluginHelper = new PersistencePluginHelper();
        mPersistencePluginHelper.validateTestDatabaseDirectory();
        mPersistencePluginOpenerHelper = new PersistencePluginOpenerHelper(
                mPersistencePluginHelper, new AlwaysCurrentMigrator());
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
     * 
     */
    @Test(timeout = 9000)
    public void progressNotificationsOnPlainOpen() {
        // setup
        final String dbName = "progressplain";
        mPersistencePluginHelper.createDatabase(dbName, "").getInstanceOf(MiniMiserDAOFactory.class).close();
        
        final DatabaseOpenObserver obs = new DatabaseOpenObserver();
        mPersistencePluginOpenerHelper.addDatabaseOpenObserver(obs);

        final OpenerAdapter openerAdapter = EasyMock.createNiceMock(OpenerAdapter.class);
        EasyMock.checkOrder(openerAdapter, true);
        openerAdapter.startOpening();
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.STARTING), EasyMock.isA(String.class));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.OPENING), EasyMock.eq("Opening database '" + dbName + "'"));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.OPENED), EasyMock.isA(String.class));
        openerAdapter.stopOpening();
        EasyMock.replay(openerAdapter);

        // run
        // Note that the password isn't passed into the opener, since
        // it's prompted for (and provided by the OpenerAdapter). It's
        // used by createDatabaseWithPluggableBehaviourBeforeDeletion
        // since we may be creating an encrypted database for tests.
        LOGGER.info("test is opening " + dbName);
        final InstanceSet<DAOFactory> database = mPersistencePluginOpenerHelper.openDatabase(dbName, openerAdapter);
        LOGGER.info("db open");
        try { 
            // test
            Assert.assertNotNull(database);
            obs.assertDatabaseOpen();
            EasyMock.verify(openerAdapter);
        } finally {
            if (database != null) {
                database.getInstanceOf(MiniMiserDAOFactory.class).close();
            }
        }
    }

    /**
     * 
     */
    @Test(timeout = 8000)
    public void progressNotificationsAndCorrectPasswordEnteredOnEncryptedOpen() {
        // setup
        final String dbName = "progressenc";
        final String dbPassword = "Squeamish Ossifrage";
        mPersistencePluginHelper.createDatabase(dbName, dbPassword).getInstanceOf(MiniMiserDAOFactory.class).close();
        
        final DatabaseOpenObserver obs = new DatabaseOpenObserver();
        mPersistencePluginOpenerHelper.addDatabaseOpenObserver(obs);

        final OpenerAdapter openerAdapter = EasyMock.createNiceMock(OpenerAdapter.class);
        EasyMock.checkOrder(openerAdapter, true);
        openerAdapter.startOpening();
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.STARTING), EasyMock.isA(String.class));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.OPENING), EasyMock.eq("Opening database '" + dbName + "'"));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.PASSWORD_REQUIRED), EasyMock.eq("Password required for '" + dbName + "'"));
        openerAdapter.requestPassword();
        EasyMock.expectLastCall().andReturn(dbPassword);
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.OPENING), EasyMock.eq("Trying to open database '" + dbName + "'"));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.OPENED), EasyMock.isA(String.class));
        openerAdapter.stopOpening();
        EasyMock.replay(openerAdapter);

        // run
        // Note that the password isn't passed into the opener, since
        // it's prompted for (and provided by the OpenerAdapter). It's
        // used by createDatabaseWithPluggableBehaviourBeforeDeletion
        // since we may be creating an encrypted database for tests.
        LOGGER.info("test is opening " + dbName);
        final InstanceSet<DAOFactory> database = mPersistencePluginOpenerHelper.openDatabase(dbName, openerAdapter);
        LOGGER.info("db open");
        try { 
            // test
            Assert.assertNotNull(database);
            obs.assertDatabaseOpen();
            EasyMock.verify(openerAdapter);
        } finally {
            if (database != null) {
                database.getInstanceOf(MiniMiserDAOFactory.class).close();
            }
        }
    }

    /**
     * 
     */
    @Test(timeout = 8000)
    public void progressNotificationsAndPasswordEntryAbandonedOnEncryptedOpen() {
        // setup
        final String dbName = "progressencabandon";
        final String dbPassword = "Squeamish Ossifrage";
        mPersistencePluginHelper.createDatabase(dbName, dbPassword).getInstanceOf(MiniMiserDAOFactory.class).close();
        
        final DatabaseOpenObserver obs = new DatabaseOpenObserver();
        mPersistencePluginOpenerHelper.addDatabaseOpenObserver(obs);

        final OpenerAdapter openerAdapter = EasyMock.createNiceMock(OpenerAdapter.class);
        EasyMock.checkOrder(openerAdapter, true);
        openerAdapter.startOpening();
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.STARTING), EasyMock.isA(String.class));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.OPENING), EasyMock.eq("Opening database '" + dbName + "'"));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.PASSWORD_REQUIRED), EasyMock.eq("Password required for '" + dbName + "'"));
        openerAdapter.requestPassword();
        EasyMock.expectLastCall().andReturn("");
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.PASSWORD_CANCELLED), EasyMock.eq("Open of '" + dbName + "' cancelled"));
        openerAdapter.stopOpening();
        EasyMock.replay(openerAdapter);

        // run
        // Note that the password isn't passed into the opener, since
        // it's prompted for (and provided by the OpenerAdapter). It's
        // used by createDatabaseWithPluggableBehaviourBeforeDeletion
        // since we may be creating an encrypted database for tests.
        LOGGER.info("test is opening " + dbName);
        final InstanceSet<DAOFactory> database = mPersistencePluginOpenerHelper.openDatabase(dbName, openerAdapter);
        LOGGER.info("db open");
        // test
        Assert.assertNull(database);
        obs.assertDatabaseNotOpen();
        EasyMock.verify(openerAdapter);
    }

    /* replace this...
@Test(timeout = 8000)
    public void progressNotificationsAndPasswordEntryAbandonedOnEncryptedOpen() {
        LOGGER.info(">>> progressNotificationsAndPasswordEntryAbandonedOnEncryptedOpen");
        final String dbName = "progressencabandon";
        final String dbPassword = "Squeamish Ossifrage";
        doCreateDatabaseBoilerplate(accessFactory, dbName, dbPassword, new RunOnCreatedDb() {
            public void runOnCreatedDb(final String dbName, final String dbPassword, final String dbDirPlusDbName) {
                
                final OpenerAdapter openerAdapter = new AbstractTestOpenerAdapter(progressRecorder) {
                    public String requestPassword() {
                        return "";
                    }
                };

                final DatabaseOpenObserver obs = new DatabaseOpenObserver();
                opener.addDatabaseOpenObserver(obs);
                                
                // Note that the password isn't passed into the opener, since
                // it's prompted for (and provided by the OpenerAdapter). It's
                // used by createDatabaseWithPluggableBehaviourBeforeDeletion
                // since we may be creating an encrypted database for tests.
                final InstanceSet<DAOFactory> database = opener.openDatabase(dbName, dbDirPlusDbName, openerAdapter);
                try {
                    progressRecorder.assertProgressWasReceived(OpenerAdapter.ProgressStage.STARTING);
                    
                    progressRecorder.assertProgressWasReceived(OpenerAdapter.ProgressStage.OPENING);

                    Assert.assertEquals(1, progressRecorder.getDescriptions(OpenerAdapter.ProgressStage.OPENING).size());
                    // First time round, expect this.
                    Assert.assertEquals("Opening database 'progressencabandon'",
                        progressRecorder.getDescriptions(OpenerAdapter.ProgressStage.OPENING).get(0));
                    // We shouldn't get a "Trying to open database"
                    
                    progressRecorder.assertProgressWasReceived(OpenerAdapter.ProgressStage.PASSWORD_REQUIRED);

                    progressRecorder.assertProgressWasReceived(OpenerAdapter.ProgressStage.PASSWORD_CANCELLED);

                    Assert.assertNull(database);
                    
                    progressRecorder.assertNotFoundNotReceived();
                    progressRecorder.assertSeriousProblemNotReceived();

                    obs.assertDatabaseNotOpen();
                } finally {
                    if (database != null) {
                        database.getInstanceOf(MiniMiserDAOFactory.class).close();
                    }
                }
            }
        });
        LOGGER.info("<<< progressNotificationsAndPasswordEntryAbandonedOnEncryptedOpen");
    } */
}
