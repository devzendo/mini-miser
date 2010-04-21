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

import java.util.List;

import org.devzendo.minimiser.migrator.DefaultMigrator;
import org.devzendo.minimiser.migrator.Migrator;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.persistence.AccessFactory;
import org.devzendo.minimiser.persistence.DAOFactory;
import org.devzendo.minimiser.persistence.DatabaseOpeningAppPlugin;
import org.devzendo.minimiser.persistence.DatabaseOpeningDAOFactory;
import org.devzendo.minimiser.persistence.DefaultPluginManagerPersistenceUnittestCase;
import org.devzendo.minimiser.persistence.MiniMiserDAOFactory;
import org.devzendo.minimiser.plugin.Plugin;
import org.devzendo.minimiser.plugin.facade.opendatabase.DatabaseOpening;
import org.devzendo.minimiser.pluginmanager.PluginException;
import org.devzendo.minimiser.pluginmanager.PluginManager;
import org.devzendo.minimiser.util.InstanceSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;



/**
 * The Opener should add all DAOFactories (from miniMiser and the
 * plugins) to the DatabaseDescriptor.
 * TODO: rewrite this to use the PersistencePluginHelper
 * @author matt
 *
 */
public final class TestPluginsAddDAOFactoriesToDatabaseDescriptorViaOpener extends DefaultPluginManagerPersistenceUnittestCase {
    private static final String PLUGINDBNAME = "openerplugindaofactorydb";
    private PluginManager mPluginManager;
    private DatabaseOpeningAppPlugin mDatabaseOpeningAppPlugin;
    private AccessFactory mAccessFactory;
    private String mDbDirPlusDbName;
    private Opener mOpener;
    private Migrator mMigrator;

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
        mMigrator = new DefaultMigrator(mPluginManager);
        mOpener = new DefaultOpener(mAccessFactory, mMigrator, mPluginManager);
    }

    private final class NullOpenerAdapter implements OpenerAdapter {
        public void databaseNotFound(
                final DataAccessResourceFailureException exception) {
            throw new IllegalStateException("Wasn't expecting the database to not be found: " + exception.getMessage());
        }

        public void reportProgress(
                final ProgressStage progressStage,
                final String description) {
        }

        public String requestPassword() {
            throw new IllegalStateException("Wasn't expecting to have a password requested");
        }

        public void migrationFailed(final DataAccessException exception) {
            throw new IllegalStateException("Wasn't expecting a migration failure: " + exception.getMessage());
        }

        public void seriousProblemOccurred(final DataAccessException exception) {
            throw new IllegalStateException("Wasn't expecting a problem: " + exception.getMessage());
        }

        public void startOpening() {
        }

        public void stopOpening() {
        }

        public boolean requestMigration() {
            throw new IllegalStateException("Wasn't expecting to have migration requested");
        }

        public void migrationNotPossible() {
        }

        public void createdByOtherApplication() {
        }

        public void noApplicationPluginAvailable() {
        }
    }

    /**
     *
     */
    @Test
    public void openerAddsPluginDAOFactoryToDatabaseDescriptor() {
        final DatabaseOpenObserver openObserver = new DatabaseOpenObserver();
        mOpener.addDatabaseOpenObserver(openObserver);
        InstanceSet<DAOFactory> daoFactories = null;
        try {
            createDatabase();
            daoFactories = mOpener.openDatabase(PLUGINDBNAME, mDbDirPlusDbName, new NullOpenerAdapter());
            Assert.assertNotNull(daoFactories);

            // Now was the plugin's DAOFactory added?
            // Some preliminary checks to make sure the MiniMiserDAOFactory is added also...
            openObserver.assertDatabaseOpen();
            final DatabaseDescriptor databaseDescriptor = openObserver.getDatabaseOpenEvent().getDatabase();
            Assert.assertEquals(PLUGINDBNAME, databaseDescriptor.getDatabaseName());
            Assert.assertNotNull(databaseDescriptor.getDAOFactory(MiniMiserDAOFactory.class));

            // Well, was the plugin's DAOFactory added?
            Assert.assertNotNull(databaseDescriptor.getDAOFactory(DatabaseOpeningDAOFactory.class));
        } finally {
            try {
                if (daoFactories != null) {
                    daoFactories.getInstanceOf(MiniMiserDAOFactory.class).close();
                }
            } finally {
                deleteDatabaseFiles(PLUGINDBNAME);
            }
        }
    }

    private void createDatabase() {
        mAccessFactory.createDatabase(mDbDirPlusDbName, "").getInstanceOf(MiniMiserDAOFactory.class).close();
    }
}
