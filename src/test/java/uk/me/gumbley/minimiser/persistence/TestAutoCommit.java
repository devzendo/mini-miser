package uk.me.gumbley.minimiser.persistence;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.persistence.sql.SQLAccess;
import uk.me.gumbley.minimiser.util.InstanceSet;

/**
 * Tests the setting and manipulation of H2's AUTOCOMMIT feature.
 * @author matt
 *
 */
public final class TestAutoCommit extends LoggingTestCase {
    private static final Logger LOGGER = Logger.getLogger(TestAutoCommit.class);
    private PersistencePluginHelper mPersistencePluginHelper;
    private SimpleJdbcTemplate mSimpleJdbcTemplate;
    private DataSourceTransactionManager mTransactionManager;

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
        mPersistencePluginHelper.tidyTestDatabasesDirectory();
    }

    /**
     * 
     */
    @Test
    public void autoCommitIsOnByDefault() {
        getSimpleJdbcTemplateForCreatedDatabase("autocommiton");
        Assert.assertTrue(isAutoCommitEnabled(mSimpleJdbcTemplate));
    }

    /**
     * 
     */
    @Test
    public void autoCommitCanBeDisabled() {
        getSimpleJdbcTemplateForCreatedDatabase("autocommitcanbedisabled");
        
        setAutoCommit(mSimpleJdbcTemplate, false);
        
        Assert.assertFalse(isAutoCommitEnabled(mSimpleJdbcTemplate));

        setAutoCommit(mSimpleJdbcTemplate, true);
        
        Assert.assertTrue(isAutoCommitEnabled(mSimpleJdbcTemplate));
    }

    private void setAutoCommit(final SimpleJdbcTemplate simpleJdbcTemplate, final boolean enabled) {
        simpleJdbcTemplate.update("set autocommit " + (enabled ? "on" : "off"));
    }

    private boolean isAutoCommitEnabled(final SimpleJdbcTemplate simpleJdbcTemplate) {
        final int autoCommit = simpleJdbcTemplate.queryForInt("call autocommit()");
        LOGGER.info("AutoCommit state is " + autoCommit);
        return autoCommit == 1;
    }

    private void getSimpleJdbcTemplateForCreatedDatabase(
            final String dbName) {
        final InstanceSet<DAOFactory> database = mPersistencePluginHelper.createDatabase(dbName, "");
        final MiniMiserDAOFactory miniMiserDaoFactory = database.getInstanceOf(MiniMiserDAOFactory.class);
        final SQLAccess sqlAccess = miniMiserDaoFactory.getSQLAccess();
        mSimpleJdbcTemplate = sqlAccess.getSimpleJdbcTemplate();
        mTransactionManager = new DataSourceTransactionManager(sqlAccess.getDataSource());
    }
}
