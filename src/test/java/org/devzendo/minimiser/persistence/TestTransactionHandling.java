/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devzendo.minimiser.persistence;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.logging.LoggingUnittestHelper;
import org.devzendo.minimiser.persistence.dao.VersionsDao;
import org.devzendo.minimiser.persistence.domain.Version;
import org.devzendo.minimiser.persistence.domain.VersionableEntity;
import org.devzendo.minimiser.persistence.sql.SQLAccess;
import org.devzendo.minimiser.util.InstanceSet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;


/**
 * Tests use of transactions.
 * 
 * @author matt
 *
 */
public final class TestTransactionHandling {
    private static final Logger LOGGER = Logger
            .getLogger(TestTransactionHandling.class);
    private static final String TESTPLUGIN = "testplugin";
    private PersistencePluginHelper mPersistencePluginHelper;
    private TransactionTemplate mTransactionTemplate;
    private MiniMiserDAOFactory mMiniMiserDaoFactory;
    private SQLAccess mSqlAccess;
    private VersionsDao mVersionsDao;
    private SimpleJdbcTemplate mSimpleJdbcTemplate;

    /**
     * 
     */
    @BeforeClass
    public static void setupLogging() {
        LoggingUnittestHelper.setupLogging();
    }

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
    public void commitCausesDataCommit() {
        // setup
        createDatabase("commit");
        LOGGER.info("Starting transaction template");
        final Boolean existsInTransaction = (Boolean) mTransactionTemplate.execute(new TransactionCallback<Object>() {
            public Boolean doInTransaction(final TransactionStatus ts) {
                final Version version = new Version(TESTPLUGIN, VersionableEntity.PLUGIN_CODE_VERSION, false, "1.0");
                mVersionsDao.persistVersion(version);
                return mVersionsDao.exists(TESTPLUGIN, VersionableEntity.PLUGIN_CODE_VERSION);
            }
        });
        // test
        LOGGER.info("End of transaction template");
        Assert.assertTrue(existsInTransaction);
        Assert.assertTrue(mVersionsDao.exists(TESTPLUGIN, VersionableEntity.PLUGIN_CODE_VERSION));
    }

    /**
     * 
     */
    @Test
    public void exceptionCausesDataRollback() {
        // setup
        createDatabase("rollback");
        final boolean[] existsInTransaction = new boolean[] {false};
        boolean correctlyCaught = false;
        LOGGER.info("Starting transaction template");
        try {
            mTransactionTemplate.execute(new TransactionCallback<Object>() {
                public Object doInTransaction(final TransactionStatus ts) {
                    final Version version = new Version(TESTPLUGIN, VersionableEntity.PLUGIN_CODE_VERSION, false, "1.0");
                    mVersionsDao.persistVersion(version);
                    existsInTransaction[0] = mVersionsDao.exists(TESTPLUGIN, VersionableEntity.PLUGIN_CODE_VERSION);
                    throw new DataIntegrityViolationException("A simulated access failure");
                }
            });
        } catch (final DataAccessException dae) {
            correctlyCaught = true; 
        }
        // test
        LOGGER.info("End of transaction template");
        Assert.assertTrue(correctlyCaught);
        Assert.assertTrue(existsInTransaction[0]);
        Assert.assertFalse(mVersionsDao.exists(TESTPLUGIN, VersionableEntity.PLUGIN_CODE_VERSION));
    }


    /**
     * Unfortunately, H2 does not deal nicely DDL inside a
     * transaction - it'll not allow it to be rolled back.
     * <p>
     * This will change though:
     * http://markmail.org/message/3yd3jxfgia7lzpq5?q=h2+transaction+ddl+list:com%2Egooglegroups%2Eh2-database+from:%22Thomas+Mueller%22
     */
    @Test
    public void ddlInATransactionCausesCommit() {
        // setup
        createDatabase("ddlcommit");
        final boolean[] existsInTransaction = new boolean[] {false};
        LOGGER.info("Starting transaction template");
        try {
            mTransactionTemplate.execute(new TransactionCallback<Object>() {
                public Object doInTransaction(final TransactionStatus ts) {
                    final Version version = new Version(TESTPLUGIN, VersionableEntity.PLUGIN_CODE_VERSION, false, "1.0");
                    mVersionsDao.persistVersion(version);
                    existsInTransaction[0] = mVersionsDao.exists(TESTPLUGIN, VersionableEntity.PLUGIN_CODE_VERSION);
                    // if I do some DDL then force a rollback, the above DML will commit
                    mSimpleJdbcTemplate.update("CREATE TABLE TEST(ID INT PRIMARY KEY, NAME VARCHAR(255))");
                    mSimpleJdbcTemplate.update("INSERT INTO TEST (ID, NAME) VALUES(?, ?)", 69, "testobject");
                    throw new DataIntegrityViolationException("A simulated access failure");
                }
            });
        } catch (final DataAccessException dae) {
            // this is tested for elsewhere 
            LOGGER.warn("Correctly caught exception: " + dae.getMessage(), dae);
        }
        // test
        LOGGER.info("End of transaction template");
        Assert.assertTrue(existsInTransaction[0]);
        // Unfortunately, the DML for the Versions table will have been committed.
        Assert.assertTrue(mVersionsDao.exists(TESTPLUGIN, VersionableEntity.PLUGIN_CODE_VERSION));
        // But the DML for the TEST table will have been rolled back
        // The TEST table will exist, however.
        final int count = mSimpleJdbcTemplate.queryForInt("SELECT COUNT(*) FROM TEST WHERE NAME = ?", "testobject");
        Assert.assertEquals(0, count);
    }

    private void createDatabase(
            final String dbName) {
        final InstanceSet<DAOFactory> database = mPersistencePluginHelper.createDatabase(dbName, "");
        mMiniMiserDaoFactory = database.getInstanceOf(MiniMiserDAOFactory.class);
        mVersionsDao = mMiniMiserDaoFactory.getVersionDao();
        mSqlAccess = mMiniMiserDaoFactory.getSQLAccess();
        mSimpleJdbcTemplate = mSqlAccess.getSimpleJdbcTemplate();
        mTransactionTemplate = mSqlAccess.createTransactionTemplate();
    }
}
