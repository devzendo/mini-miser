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

package org.devzendo.minimiser.migrator;

import org.devzendo.minimiser.persistence.DAOFactory;
import org.devzendo.minimiser.persistence.MiniMiserDAOFactory;
import org.devzendo.minimiser.persistence.PersistencePluginHelper;
import org.devzendo.minimiser.persistence.dao.VersionsDao;
import org.devzendo.minimiser.persistence.domain.Version;
import org.devzendo.minimiser.persistence.domain.VersionableEntity;
import org.devzendo.minimiser.plugin.ApplicationPlugin;
import org.devzendo.minimiser.pluginmanager.PluginException;
import org.devzendo.minimiser.pluginmanager.PluginHelper;
import org.devzendo.minimiser.pluginmanager.PluginHelperFactory;
import org.devzendo.minimiser.util.InstanceSet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



/**
 * Can the helper create old and new databases?
 *
 * @author matt
 *
 */
public final class TestPersistenceMigratorHelper {
    private static final String MIGRATIONDB = "persistencemigratorhelper";
    private PluginHelper mPluginHelper;
    private PersistencePluginHelper mPersistencePluginHelper;
    private PersistenceMigratorHelper mPersistenceMigratorHelper;

    /**
     *
     */
    @Before
    public void getPrerequisites() {
        mPluginHelper = PluginHelperFactory.createPluginHelper();
        mPersistencePluginHelper = new PersistencePluginHelper(false, mPluginHelper);
        mPersistencePluginHelper.validateTestDatabaseDirectory();

        mPersistenceMigratorHelper = new PersistenceMigratorHelper(mPluginHelper, mPersistencePluginHelper);
    }

    /**
     *
     */
    @After
    public void removeTestDatabases() {
        mPersistencePluginHelper.tidyTestDatabasesDirectory();
    }

    /**
     * @throws PluginException never
     */
    @Test
    public void oldDatabaseCanBeCreated() throws PluginException {
        // setup
        mPersistenceMigratorHelper.createOldDatabase(MIGRATIONDB);

        mPluginHelper.loadPlugins("org/devzendo/minimiser/migrator/persistencemigrationoldplugin.properties");
        // run
        final InstanceSet<DAOFactory> daoFactories = mPersistencePluginHelper.openDatabase(MIGRATIONDB, "");
        // test
        try {
            final MiniMiserDAOFactory miniMiserDAOFactory = daoFactories.getInstanceOf(MiniMiserDAOFactory.class);
            final VersionsDao versionsDao = miniMiserDAOFactory.getVersionDao();
            final ApplicationPlugin appPlugin = mPluginHelper.getApplicationPlugin();
            final Version version = versionsDao.findVersion(appPlugin.getName(), VersionableEntity.SCHEMA_VERSION);
            Assert.assertEquals("1.0", version.getVersion());
        } finally {
            // teardown
            daoFactories.getInstanceOf(MiniMiserDAOFactory.class).close();
        }
    }

    /**
     * @throws PluginException never
     */
    @Test
    public void newDatabaseCanBeCreated() throws PluginException {
        // setup
        mPersistenceMigratorHelper.createNewDatabase(MIGRATIONDB);

        mPluginHelper.loadPlugins("org/devzendo/minimiser/migrator/persistencemigrationnewplugin.properties");
        // run
        final InstanceSet<DAOFactory> daoFactories = mPersistencePluginHelper.openDatabase(MIGRATIONDB, "");
        // test
        try {
            final MiniMiserDAOFactory miniMiserDAOFactory = daoFactories.getInstanceOf(MiniMiserDAOFactory.class);
            final VersionsDao versionsDao = miniMiserDAOFactory.getVersionDao();
            final ApplicationPlugin appPlugin = mPluginHelper.getApplicationPlugin();
            final Version version = versionsDao.findVersion(appPlugin.getName(), VersionableEntity.SCHEMA_VERSION);
            Assert.assertEquals("2.0", version.getVersion());
        } finally {
            // teardown
            daoFactories.getInstanceOf(MiniMiserDAOFactory.class).close();
        }
    }
}
