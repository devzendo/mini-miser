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

import java.util.List;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.plugin.Plugin;
import org.devzendo.minimiser.plugin.facade.closedatabase.DatabaseClosing;
import org.devzendo.minimiser.pluginmanager.PluginException;
import org.devzendo.minimiser.pluginmanager.PluginManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



/**
 * Plugins that declare an interest will be given the opportunity
 * to close any database resources prior to final closing..
 *
 * @author matt
 *
 */
public final class TestPluginsAreInvolvedWithDatabaseClosing extends DefaultPluginManagerPersistenceUnittestCase {
    private static final Logger LOGGER = Logger
            .getLogger(TestPluginsAreInvolvedWithDatabaseClosing.class);
    private static final String PLUGINDBNAME = "pluginclosingdb";
    private PluginManager mPluginManager;
    private DatabaseClosingAppPlugin mDatabaseClosingAppPlugin;
    private AccessFactory mAccessFactory;
    private String mDbDirPlusDbName;

    /**
     * @throws PluginException never
     */
    @Before
    public void getPrerequisites() throws PluginException {
        mPluginManager = getPluginManager();
        mPluginManager.loadPlugins("org/devzendo/minimiser/persistence/persistenceclosingplugin.properties");
        final List<Plugin> plugins = mPluginManager.getPlugins();
        Assert.assertEquals(1, plugins.size());
        mDatabaseClosingAppPlugin = (DatabaseClosingAppPlugin) plugins.get(0);
        Assert.assertNotNull(mDatabaseClosingAppPlugin);
        final List<DatabaseClosing> databaseClosingPlugins = mPluginManager.getPluginsImplementingFacade(DatabaseClosing.class);
        Assert.assertEquals(1, databaseClosingPlugins.size());
        mAccessFactory = getAccessFactory();
        Assert.assertNotNull(mAccessFactory);
        mDbDirPlusDbName = getAbsoluteDatabaseDirectory(PLUGINDBNAME);
    }

    /**
     *
     */
    @Test
    public void pluginsCalledToCloseDatabase() {
        MiniMiserDAOFactory miniMiserDAOFactory = null;
        try {
            miniMiserDAOFactory = createDatabaseLeaveOpen();
            Assert.assertFalse(miniMiserDAOFactory.isClosed());
            Assert.assertFalse(mDatabaseClosingAppPlugin.closeMethodCalled());

            miniMiserDAOFactory.close();
            Assert.assertTrue(mDatabaseClosingAppPlugin.closeMethodCalled());
        } finally {
            try {
                if (miniMiserDAOFactory != null) {
                    if (!miniMiserDAOFactory.isClosed()) {
                        LOGGER.info("db is not already closed in finally");
                        miniMiserDAOFactory.close();
                    }
                }
            } finally {
                deleteDatabaseFiles(PLUGINDBNAME);
            }
        }
    }

    private MiniMiserDAOFactory createDatabaseLeaveOpen() {
        return mAccessFactory.createDatabase(mDbDirPlusDbName, "").getInstanceOf(MiniMiserDAOFactory.class);
    }
}
