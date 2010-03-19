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

package org.devzendo.minimiser.persistence.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.logging.LoggingTestCase;
import org.devzendo.minimiser.persistence.MiniMiserDAOFactory;
import org.devzendo.minimiser.persistence.PersistencePluginHelper;
import org.devzendo.minimiser.persistence.domain.Version;
import org.devzendo.minimiser.persistence.domain.VersionableEntity;
import org.devzendo.minimiser.plugin.ApplicationPlugin;
import org.devzendo.minimiser.plugin.Plugin;
import org.devzendo.minimiser.pluginmanager.PluginHelper;
import org.devzendo.minimiser.pluginmanager.PluginHelperFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



/**
 * Tests the Version DAO
 * 
 * @author matt
 *
 */
public final class TestVersionDao extends LoggingTestCase {
    private static final Logger LOGGER = Logger.getLogger(TestVersionDao.class);
    
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

    private Plugin getNormalPlugin() {
        final List<Plugin> plugins = mPluginHelper.getPluginManager().getPlugins();
        for (Plugin plugin : plugins) {
            if (!(plugin instanceof ApplicationPlugin)) {
                return plugin;
            }
        }
        return null;
    }
    
    private Plugin getAppPlugin() {
        return mPluginHelper.getPluginManager().getApplicationPlugin();
    }

    private void checkVersionForPlugin(
            final Plugin plugin,
            final VersionsDao versionsDao,
            final boolean isApplication) {
        
        final Version dbVersion = versionsDao.findVersion(plugin.getName(), VersionableEntity.SCHEMA_VERSION);
        LOGGER.info(String.format("... schema version returned from db should not be null - it is %s", dbVersion));
        Assert.assertNotNull(dbVersion);
        Assert.assertEquals(plugin.getName(), dbVersion.getPluginName());
        Assert.assertEquals(VersionableEntity.SCHEMA_VERSION, dbVersion.getEntity());
        Assert.assertEquals(plugin.getSchemaVersion(), dbVersion.getVersion());
        Assert.assertEquals(isApplication, dbVersion.isApplication());
        //
        final Version appVersion = versionsDao.findVersion(plugin.getName(), VersionableEntity.PLUGIN_CODE_VERSION);
        LOGGER.info(String.format("... application version returned from db should not be null - it is %s", appVersion));
        Assert.assertNotNull(dbVersion);
        Assert.assertEquals(plugin.getName(), appVersion.getPluginName());
        Assert.assertEquals(VersionableEntity.PLUGIN_CODE_VERSION, appVersion.getEntity());
        Assert.assertEquals(plugin.getVersion(), appVersion.getVersion());
        Assert.assertEquals(isApplication, appVersion.isApplication());
    }

    /**
     * Is the VERSIONS table populated correctly?
     */
    @Test
    public void checkVersionPopulation() { 
        final String dbName = "checkversionpopulation";
        final VersionsDao versionsDao = mPersistencePluginHelper.
            createDatabase(dbName, "").
            getInstanceOf(MiniMiserDAOFactory.class).
            getVersionDao();

        final Plugin appPlugin = getAppPlugin();
        Assert.assertNotNull(appPlugin);
        checkVersionForPlugin(appPlugin, versionsDao, true);
        
        final Plugin normalPlugin = getNormalPlugin();
        Assert.assertNotNull(normalPlugin);
        checkVersionForPlugin(normalPlugin, versionsDao, false);
    }
    
    /**
     * You can insert a Version, but only update the version
     * field, not the isApplication field.
     */
    @Test
    public void pluginsCannotChangeTheirApplicationStatus() {
        final String dbName = "noapplicationstatuschange";
        final VersionsDao versionsDao = mPersistencePluginHelper.
            createDatabase(dbName, "").
            getInstanceOf(MiniMiserDAOFactory.class).
            getVersionDao();
        final Plugin appPlugin = getAppPlugin();
        final Version appVersion = versionsDao.findVersion(appPlugin.getName(), VersionableEntity.PLUGIN_CODE_VERSION);
        Assert.assertTrue(appVersion.isApplication());
        Assert.assertEquals("1.0.0", appVersion.getVersion());
        appVersion.setIsApplication(false);
        appVersion.setVersion("1.0.1");
        Assert.assertFalse(appVersion.isApplication()); // the bean takes the change...
        Assert.assertEquals("1.0.1", appVersion.getVersion());
        versionsDao.persistVersion(appVersion);

        final Version newAppVersion = versionsDao.findVersion(appPlugin.getName(), VersionableEntity.PLUGIN_CODE_VERSION);
        Assert.assertTrue(newAppVersion.isApplication()); // but the change in application status cannot be persisted
        Assert.assertEquals("1.0.1", newAppVersion.getVersion()); // version can be updated though
    }
}
