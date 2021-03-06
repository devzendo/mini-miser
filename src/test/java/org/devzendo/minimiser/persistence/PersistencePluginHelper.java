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

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.commoncode.string.StringUtils;
import org.devzendo.minimiser.config.UnittestingConfig;
import org.devzendo.minimiser.persistence.impl.JdbcTemplateAccessFactoryImpl;
import org.devzendo.minimiser.pluginmanager.PluginHelper;
import org.devzendo.minimiser.pluginmanager.PluginHelperFactory;
import org.devzendo.minimiser.util.InstanceSet;


/**
 * A helper class for building tests involving plugins and
 * persistence.
 *
 * @author matt
 *
 */
public final class PersistencePluginHelper {
    private static final Logger LOGGER = Logger
            .getLogger(PersistencePluginHelper.class);
    private final AccessFactory mAccessFactory;
    private final File mTestDatabaseDirectory;
    private final boolean mSuppressEmptinessCheck;
    private final PersistencePluginHelperTidier mTidier;
    private final PluginHelper mPluginHelper;

    /**
     * Create a helper that will check that the test database
     * directory is empty, and use the real plugin manager.
     */
    public PersistencePluginHelper() {
        this(false, PluginHelperFactory.createPluginHelper());
    }

    /**
     * Create a helper that may or may not check that the test
     * database directory is empty, and may use the real or dummy
     * plugin manager
     * @param suppressEmptinessCheck true to suppress the empty
     * check, false to check it
     * @param pluginHelper the PluginHelper, whose PluginManager
     * will be used by the access factory when creating / opening
     * databases
     */
    public PersistencePluginHelper(final boolean suppressEmptinessCheck,
            final PluginHelper pluginHelper) {
        mSuppressEmptinessCheck = suppressEmptinessCheck;
        mPluginHelper = pluginHelper;
        mTestDatabaseDirectory = new UnittestingConfig().getTestDatabaseDirectory();
        mAccessFactory = new JdbcTemplateAccessFactoryImpl(mPluginHelper.getPluginManager());
        mTidier = new PersistencePluginHelperTidier(mTestDatabaseDirectory);
    }

    /**
     * Add a database to the set that are to be deleted by
     * deleteCreatedDatabases. For instance if you're using two
     * instances of the helper, one to create a database with an
     * old schema, using an "old" plugin, and one to verify
     * migration to a new schema, using a "new plugin".
     * @param dbName the name of the database to add to the
     * deletion set.
     * @param miniMiserDAOFactory a MiniMiserDAOFactory used to
     * close the database if open, prior to deletion
     */
    public void addDatabaseToDelete(final String dbName, final MiniMiserDAOFactory miniMiserDAOFactory) {
        mTidier.addDatabaseToDelete(dbName, miniMiserDAOFactory);
    }

    /**
     * Add a database to the set that are to be deleted by
     * deleteCreatedDatabases. For instance if you're using two
     * instances of the helper, one to create a database with an
     * old schema, using an "old" plugin, and one to verify
     * migration to a new schema, using a "new plugin".
     * @param dbName the name of the database to add to the
     * deletion set.
     */
    public void addDatabaseToDelete(final String dbName) {
        mTidier.addDatabaseToDelete(dbName);
    }

    /**
     * Check for an empty test database directory
     */
    public void validateTestDatabaseDirectory() {
        if (mTestDatabaseDirectory == null
                || mTestDatabaseDirectory.getAbsolutePath().length() == 0) {
            final String err = "No database directory defined";
            LOGGER.error(err);
            throw new IllegalStateException(err);
        }
        if (!mTestDatabaseDirectory.exists() || !mTestDatabaseDirectory.isDirectory()) {
            final String err = String.format(
                            "Database dir %s does not exist or is not a directory",
                            mTestDatabaseDirectory.getAbsolutePath());
            LOGGER.error(err);
            throw new IllegalStateException(err);
        }
        if (mSuppressEmptinessCheck) {
            return;
        }

        checkForEmptiness(mTestDatabaseDirectory);
    }

    private void checkForEmptiness(final File databaseDirectory) {
        if (databaseDirectory != null && databaseDirectory.exists()
                && databaseDirectory.isDirectory()) {
            final List<String> files = Arrays.asList(databaseDirectory.list());
            if (files == null || files.size() == 0) {
                return;
            }
            final String err = String.format(
                            "Database directory %s contains %d files not cleared up after test: %s",
                            databaseDirectory.getAbsoluteFile(),
                            files.size(), StringUtils.join(files, ", "));
            LOGGER.error(err);
            if (files.size() != 0) {
                throw new IllegalStateException(err);
            }
        }
    }

    /**
     * Obtain the full path to a named directory
     * @param dbname the name of the database directory e.g. foo
     * @return the directory, prefixed with the test database directory, e.g.
     * /home/matt/testdb/foo
     */
    public String getAbsoluteDatabaseDirectory(final String dbname) {
        final StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.slashTerminate(mTestDatabaseDirectory.getAbsolutePath()));
        sb.append(dbname);
        return sb.toString();
    }

    /**
     * Create a database given its name and optional password.
     * It is left open. Typically called from a @Before method.
     * @param dbName the name of the database
     * @param dbPassword the password, or empty string if not
     * encrypted.
     * @return an InstanceSet<DAOFactory> via which data access
     * objects can be obtained.
     */
    public InstanceSet<DAOFactory> createDatabase(final String dbName, final String dbPassword) {
        final Observer<PersistenceObservableEvent> ignoringObserver = new Observer<PersistenceObservableEvent>() {
            public void eventOccurred(final PersistenceObservableEvent observableEvent) {
                // do nothing
            }
        };
        return createDatabase(dbName, dbPassword, ignoringObserver);
    }

    /**
     * Create a database given its name and optional password.
     * It is left open. Typically called from a @Before method.
     * @param dbName the name of the database
     * @param dbPassword the password, or empty string if not
     * encrypted.
     * @param observer an observer of persistence events that
     * will be notified of the progress of the creation.
     * @return an InstanceSet<DAOFactory> via which data access
     * objects can be obtained.
     */
    public InstanceSet<DAOFactory> createDatabase(final String dbName, final String dbPassword, final Observer<PersistenceObservableEvent> observer) {
        // in case the open fails, we still need to delete the database on exit
        mTidier.addDatabaseToDelete(dbName);

        final String dbDirPlusDbName = getAbsoluteDatabaseDirectory(dbName);
        LOGGER.info(String.format("Creating database dbName = %s, dbDirPlusDbName = %s, dbPassword = '%s'", dbName, dbDirPlusDbName, dbPassword));
        final InstanceSet<DAOFactory> daoFactorySet = mAccessFactory.createDatabase(dbDirPlusDbName, dbPassword, observer, null);
        mTidier.addDatabaseToDelete(dbName, daoFactorySet.getInstanceOf(MiniMiserDAOFactory.class));
        return daoFactorySet;
    }

    /**
     * Open a database given its name and optional password.
     * Typically called from a @Before method.
     * @param dbName the name of the database
     * @param dbPassword the password, or empty string if not
     * encrypted.
     * @return an InstabceSet<DAOFactory> via which data access
     * objects can be obtained.
     */
    public InstanceSet<DAOFactory> openDatabase(final String dbName, final String dbPassword) {
        // in case the open fails, we still need to delete the database on exit
        mTidier.addDatabaseToDelete(dbName);

        final String dbDirPlusDbName = getAbsoluteDatabaseDirectory(dbName);
        LOGGER.info(String.format("Opening database dbName = %s, dbDirPlusDbName = %s, dbPassword = '%s'", dbName, dbDirPlusDbName, dbPassword));
        final InstanceSet<DAOFactory> daoFactorySet = mAccessFactory.openDatabase(dbDirPlusDbName, dbPassword);
        mTidier.addDatabaseToDelete(dbName, daoFactorySet.getInstanceOf(MiniMiserDAOFactory.class));
        return daoFactorySet;
    }

    /**
     * Typically run in an @After block, tidy up after the
     * databases have been created.
     */
    public void tidyTestDatabasesDirectory() {
        mTidier.tidy();
    }

    /**
     * Obtain the access factory
     * @return the access factory
     */
    public AccessFactory getAccessFactory() {
        return mAccessFactory;
    }
}
