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
import uk.me.gumbley.minimiser.pluginmanager.PluginHelper;
import uk.me.gumbley.minimiser.util.InstanceSet;

/**
 * Tests a corner case of Opener workflow, when an open is
 * attempted with no application plugin defined.
 * 
 * @author matt
 * 
 */
public final class TestOpenerWorkflowWithNoPlugins extends LoggingTestCase {
    private static final Logger LOGGER = Logger
            .getLogger(TestOpenerWorkflowWithNoPlugins.class);
    private PluginHelper mPluginHelper;
    private PersistencePluginHelper mPersistencePluginHelper;
    private PersistencePluginOpenerHelper mPersistencePluginOpenerHelper;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        mPluginHelper = new PluginHelper(false);
        mPersistencePluginHelper = new PersistencePluginHelper(false, mPluginHelper);
        // Note: NO PLUGINS WILL BE LOADED IN THIS TEST
        mPersistencePluginHelper.validateTestDatabaseDirectory();
        mPersistencePluginOpenerHelper = new PersistencePluginOpenerHelper(
            mPluginHelper,
            mPersistencePluginHelper,
            new AlwaysCurrentMigrator());
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
    public void progressNotificationsWithNoPluginsLoadedOnPlainOpen() {
        // setup
        final String dbName = "progressplainnoplugins";
        mPersistencePluginHelper.createDatabase(dbName, "").getInstanceOf(MiniMiserDAOFactory.class).close();
        
        final DatabaseOpenObserver obs = new DatabaseOpenObserver();
        mPersistencePluginOpenerHelper.addDatabaseOpenObserver(obs);

        final OpenerAdapter openerAdapter = EasyMock.createNiceMock(OpenerAdapter.class);
        EasyMock.checkOrder(openerAdapter, true);
        openerAdapter.startOpening();
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.STARTING), EasyMock.isA(String.class));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.OPENING), EasyMock.eq("Opening database '" + dbName + "'"));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.NO_APPLICATION_PLUGIN), EasyMock.isA(String.class));
        openerAdapter.noApplicationPluginAvailable();
        openerAdapter.stopOpening();
        EasyMock.replay(openerAdapter);

        // run
        // Note that the password isn't passed into the opener, since
        // it's prompted for (and provided by the OpenerAdapter). It's
        // used by createDatabaseWithPluggableBehaviourBeforeDeletion
        // since we may be creating an encrypted database for tests.
        LOGGER.info("test is opening " + dbName);
        final InstanceSet<DAOFactory> database = mPersistencePluginOpenerHelper.openDatabase(dbName, openerAdapter);
        LOGGER.info("db should not be open");
        // test
        Assert.assertNull(database);
        obs.assertDatabaseNotOpen();
        EasyMock.verify(openerAdapter);
    }
}
