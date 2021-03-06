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

package org.devzendo.minimiser.opener;

import java.io.File;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.logging.LoggingUnittestHelper;
import org.devzendo.minimiser.opener.OpenerAdapter.ProgressStage;
import org.devzendo.minimiser.persistence.DAOFactory;
import org.devzendo.minimiser.persistence.MiniMiserDAOFactory;
import org.devzendo.minimiser.persistence.PersistencePluginHelper;
import org.devzendo.minimiser.persistence.PersistencePluginOpenerHelper;
import org.devzendo.minimiser.pluginmanager.PluginHelper;
import org.devzendo.minimiser.pluginmanager.PluginHelperFactory;
import org.devzendo.minimiser.util.FileUnittestHelper;
import org.devzendo.minimiser.util.InstanceSet;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.dao.DataAccessException;


/**
 * Tests the simpler cases of Opener workflow. e.g. opening, password entry,
 * etc. Note that migration is not covered here, see
 * TestPluginsAreInvolvedWithDatabaseMigration, also, this uses dummy
 * plugins - see TestOpenerWorkflowWithNoPlugins for the case
 * where an open is attempted when no application plugin is
 * loaded.
 * 
 * @author matt
 * 
 */
public final class TestOpenerWorkflow {
    private static final Logger LOGGER = Logger
            .getLogger(TestOpenerWorkflow.class);
    private PluginHelper mPluginHelper;
    private PersistencePluginHelper mPersistencePluginHelper;
    private PersistencePluginOpenerHelper mPersistencePluginOpenerHelper;

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
        mPluginHelper = PluginHelperFactory.createPluginHelperWithDummyPluginManager();
        mPersistencePluginHelper = new PersistencePluginHelper(false, mPluginHelper);
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

    /**
     * 
     */
    @Test(timeout = 8000)
    public void progressNotificationsAndCorrectPasswordEnteredSecondTimeOnEncryptedOpen() {
        // setup
        final String dbName = "progressencabandon";
        final String[] dbPasswordAttempts = new String[] {"Squeamish Ossifrage", "Tantric Obstacles"};
        mPersistencePluginHelper.createDatabase(dbName, dbPasswordAttempts[0]).getInstanceOf(MiniMiserDAOFactory.class).close();
        
        final DatabaseOpenObserver obs = new DatabaseOpenObserver();
        mPersistencePluginOpenerHelper.addDatabaseOpenObserver(obs);

        final OpenerAdapter openerAdapter = EasyMock.createNiceMock(OpenerAdapter.class);
        EasyMock.checkOrder(openerAdapter, true);
        openerAdapter.startOpening();
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.STARTING), EasyMock.isA(String.class));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.OPENING), EasyMock.eq("Opening database '" + dbName + "'"));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.PASSWORD_REQUIRED), EasyMock.eq("Password required for '" + dbName + "'"));
        openerAdapter.requestPassword();
        EasyMock.expectLastCall().andReturn(dbPasswordAttempts[1]);
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.OPENING), EasyMock.eq("Trying to open database '" + dbName + "'"));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.PASSWORD_REQUIRED), EasyMock.eq("Password required for '" + dbName + "'"));
        openerAdapter.requestPassword();
        EasyMock.expectLastCall().andReturn(dbPasswordAttempts[0]);
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
    public void progressNotificationsExceptionOnOpenOfNonExistant() {
        // setup
        final String dbName = "wah";
        
        final DatabaseOpenObserver obs = new DatabaseOpenObserver();
        mPersistencePluginOpenerHelper.addDatabaseOpenObserver(obs);

        final OpenerAdapter openerAdapter = EasyMock.createNiceMock(OpenerAdapter.class);
        EasyMock.checkOrder(openerAdapter, true);
        openerAdapter.startOpening();
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.STARTING), EasyMock.isA(String.class));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.OPENING), EasyMock.eq("Opening database '" + dbName + "'"));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.NOT_PRESENT), EasyMock.isA(String.class));
        openerAdapter.stopOpening();
        EasyMock.replay(openerAdapter);

        // run
        // Note that the password isn't passed into the opener, since
        // it's prompted for (and provided by the OpenerAdapter). It's
        // used by createDatabaseWithPluggableBehaviourBeforeDeletion
        // since we may be creating an encrypted database for tests.
        LOGGER.info("test is opening " + dbName);
        final InstanceSet<DAOFactory> database = mPersistencePluginOpenerHelper.openNonExistantDatabase(dbName, openerAdapter);
        LOGGER.info("db open");
        // test
        Assert.assertNull(database);
        obs.assertDatabaseNotOpen();
        EasyMock.verify(openerAdapter);
    }
    
    /**
     * 
     */
    @Test(timeout = 8000)
    public void progressNotificationsExceptionOnOpenOfCorrupt() {
        // setup
        final String dbName = "corrupt";
        mPersistencePluginHelper.createDatabase(dbName, "").getInstanceOf(MiniMiserDAOFactory.class).close();
        // Corrupt the database
        final File dbFile = new File(mPersistencePluginHelper.getAbsoluteDatabaseDirectory(dbName) + ".data.db");
        LOGGER.info("data file is " + dbFile.getAbsolutePath());
        Assert.assertTrue(dbFile.exists());
        FileUnittestHelper.corruptFile(dbFile);
        
        final DatabaseOpenObserver obs = new DatabaseOpenObserver();
        mPersistencePluginOpenerHelper.addDatabaseOpenObserver(obs);

        final OpenerAdapter openerAdapter = EasyMock.createNiceMock(OpenerAdapter.class);
        EasyMock.checkOrder(openerAdapter, true);
        openerAdapter.startOpening();
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.STARTING), EasyMock.isA(String.class));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.OPENING), EasyMock.eq("Opening database '" + dbName + "'"));
        openerAdapter.reportProgress(EasyMock.eq(ProgressStage.OPEN_FAILED), EasyMock.isA(String.class));
        openerAdapter.seriousProblemOccurred(EasyMock.isA(DataAccessException.class));
        openerAdapter.stopOpening();
        EasyMock.replay(openerAdapter);

        // run
        // Note that the password isn't passed into the opener, since
        // it's prompted for (and provided by the OpenerAdapter). It's
        // used by createDatabaseWithPluggableBehaviourBeforeDeletion
        // since we may be creating an encrypted database for tests.
        LOGGER.info("test is opening " + dbName);
        final InstanceSet<DAOFactory> database = mPersistencePluginOpenerHelper.openNonExistantDatabase(dbName, openerAdapter);
        LOGGER.info("db open");
        // test
        Assert.assertNull(database);
        obs.assertDatabaseNotOpen();
        EasyMock.verify(openerAdapter);
    }
}
