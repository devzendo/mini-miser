package org.devzendo.minimiser.persistence;

import org.devzendo.minimiser.logging.LoggingTestCase;
import org.devzendo.minimiser.pluginmanager.PluginHelper;
import org.devzendo.minimiser.pluginmanager.PluginHelperFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * PluginHelper-based tests for creating a database.
 * @author matt
 *
 */
public final class TestCreateDatabase extends LoggingTestCase {

    private PluginHelper mPluginHelper;
    private PersistencePluginHelper mPersistencePluginHelper;

    /**
     *
     */
    @Before
    public void getPrerequisites() {
        mPluginHelper = PluginHelperFactory.createPluginHelperWithDummyPluginManager();
        mPersistencePluginHelper = new PersistencePluginHelper(false, mPluginHelper);
        mPersistencePluginHelper.validateTestDatabaseDirectory();
    }

    /**
     *
     */
    @After
    public void removeTestDatabases() {
        mPersistencePluginHelper.tidyTestDatabasesDirectory();
    }

    /**
     */
    @Test
    public void canCreateDatabaseWithNullObserver() {
        final String dbName = "nullobserverok";
        final MiniMiserDAOFactory daoFactory = mPersistencePluginHelper.
            createDatabase(dbName, "", null).
            getInstanceOf(MiniMiserDAOFactory.class);
        Assert.assertNotNull(daoFactory);
    }
}
