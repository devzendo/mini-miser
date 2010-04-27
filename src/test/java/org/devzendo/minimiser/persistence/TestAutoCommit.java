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

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.logging.LoggingTestCase;
import org.devzendo.minimiser.persistence.dao.VersionsDao;
import org.devzendo.minimiser.persistence.domain.Version;
import org.devzendo.minimiser.persistence.domain.VersionableEntity;
import org.devzendo.minimiser.persistence.sql.SQLAccess;
import org.devzendo.minimiser.util.InstanceSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;


/**
 * Tests the setting and manipulation of H2's AUTOCOMMIT feature.
 * @author matt
 *
 */
public final class TestAutoCommit extends LoggingTestCase {
    private static final Logger LOGGER = Logger.getLogger(TestAutoCommit.class);
    private PersistencePluginHelper mPersistencePluginHelper;
    private SimpleJdbcTemplate mSimpleJdbcTemplate;
    private TransactionTemplate mTransactionTemplate;
    private VersionsDao mVersionsDao;
    private static final String TESTPLUGIN = "testplugin";

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
    
    /**
     * 
     */
    @Test
    public void autoCommitIsDisabledWhenTransactionActive() {
        getSimpleJdbcTemplateForCreatedDatabase("autocommitdisabledintransaction");
        LOGGER.info("Starting transaction template");
        final boolean[] autoCommitActive = {true, true};
        mTransactionTemplate.execute(new TransactionCallback<Object>() {
            public Object doInTransaction(final TransactionStatus ts) {
                autoCommitActive[0] = isAutoCommitEnabled(mSimpleJdbcTemplate);
                // create something for the transaction to be useful for
                final Version version = new Version(TESTPLUGIN, VersionableEntity.PLUGIN_CODE_VERSION, false, "1.0");
                mVersionsDao.persistVersion(version);
                autoCommitActive[1] = isAutoCommitEnabled(mSimpleJdbcTemplate);
                return null;
            }
        });
        // test
        LOGGER.info("End of transaction template");
        Assert.assertFalse("Autocommit should have been turned off at the start of the transaction", autoCommitActive[0]);
        Assert.assertFalse("Autocommit should have been turned off by the time data has been written", autoCommitActive[1]);
    }

    /**
     * 
     */
    @Test
    public void autoCommitIsReEnabledWhenTransactionFinished() {
        getSimpleJdbcTemplateForCreatedDatabase("autocommitenabledaftercommit");
        LOGGER.info("Starting transaction template");
        Assert.assertTrue("Didn't start out with autocommit enabled", isAutoCommitEnabled(mSimpleJdbcTemplate));
        mTransactionTemplate.execute(new TransactionCallback<Object>() {
            public Object doInTransaction(final TransactionStatus ts) {
                // create something for the transaction to be useful for
                final Version version = new Version(TESTPLUGIN, VersionableEntity.PLUGIN_CODE_VERSION, false, "1.0");
                mVersionsDao.persistVersion(version);
                return null;
            }
        });
        // test
        LOGGER.info("End of transaction template");
        Assert.assertTrue("Autocommit was not re-enabled after a transaction", isAutoCommitEnabled(mSimpleJdbcTemplate));
    }

    /**
     * 
     */
    @Test
    public void autoCommitReEnabledAfterRollback() {
        // setup
        getSimpleJdbcTemplateForCreatedDatabase("autocommitenabledafterrollback");
        LOGGER.info("Starting transaction template");
        Assert.assertTrue("Didn't start out with autocommit enabled", isAutoCommitEnabled(mSimpleJdbcTemplate));
        LOGGER.info("Starting transaction template");
        try {
            mTransactionTemplate.execute(new TransactionCallback<Object>() {
                public Object doInTransaction(final TransactionStatus ts) {
                    final Version version = new Version(TESTPLUGIN, VersionableEntity.PLUGIN_CODE_VERSION, false, "1.0");
                    mVersionsDao.persistVersion(version);
                    throw new DataIntegrityViolationException("A simulated access failure");
                }
            });
        } catch (final DataAccessException dae) {
            // nothing - will come in here, but this is tested for elsewhere
        }
        // test
        LOGGER.info("End of transaction template");
        Assert.assertTrue("Autocommit was not re-enabled after a transaction", isAutoCommitEnabled(mSimpleJdbcTemplate));
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
        mTransactionTemplate = sqlAccess.createTransactionTemplate();
        mVersionsDao = miniMiserDaoFactory.getVersionDao();
    }
}
