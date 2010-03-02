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

import org.devzendo.minimiser.plugin.Plugin;
import org.devzendo.minimiser.plugin.facade.opendatabase.DatabaseOpening;
import org.devzendo.minimiser.pluginmanager.PluginException;
import org.devzendo.minimiser.pluginmanager.PluginManager;
import org.devzendo.minimiser.util.InstanceSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



/**
 * Plugins that declare an interest will be given the opportunity
 * to provide their DAOFactory to be added to the
 * InstanceSet<DAOFactory> that's bound to the DatabaseDescriptor
 * upon database opening.
 *
 * @author matt
 *
 */
public final class TestPluginsAreInvolvedWithDatabaseOpening extends DefaultPluginManagerPersistenceUnittestCase {
    private static final String PLUGINDBNAME = "pluginopeningdb";
    private PluginManager mPluginManager;
    private DatabaseOpeningAppPlugin mDatabaseOpeningAppPlugin;
    private AccessFactory mAccessFactory;
    private String mDbDirPlusDbName;

    /**
     * @throws PluginException never
     */
    @Before
    public void getPrerequisites() throws PluginException {
        mPluginManager = getPluginManager();
        mPluginManager.loadPlugins("org/devzendo/minimiser/persistence/persistenceopeningplugin.properties");
        final List<Plugin> plugins = mPluginManager.getPlugins();
        Assert.assertEquals(1, plugins.size());
        mDatabaseOpeningAppPlugin = (DatabaseOpeningAppPlugin) plugins.get(0);
        Assert.assertNotNull(mDatabaseOpeningAppPlugin);
        final List<DatabaseOpening> databaseOpeningPlugins = mPluginManager.getPluginsImplementingFacade(DatabaseOpening.class);
        Assert.assertEquals(1, databaseOpeningPlugins.size());
        mAccessFactory = getAccessFactory();
        Assert.assertNotNull(mAccessFactory);
        mDbDirPlusDbName = getAbsoluteDatabaseDirectory(PLUGINDBNAME);
    }

    /**
     *
     */
    @Test
    public void pluginsCreateDAOFactory() {
        InstanceSet<DAOFactory> daoFactories = null;
        MiniMiserDAOFactory miniMiserDAOFactory = null;
        try {
            createDatabaseThenClose();

            daoFactories = mAccessFactory.openDatabase(mDbDirPlusDbName, "");
            miniMiserDAOFactory = daoFactories.getInstanceOf(MiniMiserDAOFactory.class);
            Assert.assertNotNull(miniMiserDAOFactory);
            final DatabaseOpeningDAOFactory databaseOpeningDAOFactory =
                daoFactories.getInstanceOf(DatabaseOpeningDAOFactory.class);
            Assert.assertNotNull(databaseOpeningDAOFactory);
            Assert.assertTrue(mDatabaseOpeningAppPlugin.allOpeningMethodsCalled());
        } finally {
            try {
                if (miniMiserDAOFactory != null) {
                    miniMiserDAOFactory.close();
                }
            } finally {
                deleteDatabaseFiles(PLUGINDBNAME);
            }
        }
    }

    private void createDatabaseThenClose() {
        mAccessFactory.createDatabase(mDbDirPlusDbName, "").getInstanceOf(MiniMiserDAOFactory.class).close();
    }
}
