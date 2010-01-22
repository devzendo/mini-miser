package org.devzendo.minimiser.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.plugin.Plugin;
import org.devzendo.minimiser.plugin.facade.newdatabase.NewDatabaseCreation;
import org.devzendo.minimiser.pluginmanager.PluginException;
import org.devzendo.minimiser.pluginmanager.PluginManager;
import org.devzendo.minimiser.util.InstanceSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



/**
 * Plugins that declare an interest will be given the File|New
 * wizard's results data, i.e. the user's input prior to
 * database creation.
 *
 * @author matt
 *
 */
public final class TestPluginsAreInvolvedWithDatabaseCreation extends DefaultPluginManagerPersistenceUnittestCase {
    private static final Logger LOGGER = Logger
            .getLogger(TestPluginsAreInvolvedWithDatabaseCreation.class);
    private static final String PLUGINDBNAME = "plugincreationdb";
    private PluginManager mPluginManager;
    private DatabaseCreationAppPlugin mDatabaseCreationAppPlugin;
    private AccessFactory mAccessFactory;
    private PluginObserver mPluginObserver;
    private String mDbDirPlusDbName;

    /**
     * @throws PluginException never
     */
    @Before
    public void getPrerequisites() throws PluginException {
        mPluginManager = getPluginManager();
        mPluginManager.loadPlugins("org/devzendo/minimiser/persistence/persistencecreationplugin.properties");
        final List<Plugin> plugins = mPluginManager.getPlugins();
        Assert.assertEquals(1, plugins.size());
        mDatabaseCreationAppPlugin = (DatabaseCreationAppPlugin) plugins.get(0);
        Assert.assertNotNull(mDatabaseCreationAppPlugin);
        final List<NewDatabaseCreation> databaseCreationPlugins = mPluginManager.getPluginsImplementingFacade(NewDatabaseCreation.class);
        Assert.assertEquals(1, databaseCreationPlugins.size());
        mAccessFactory = getAccessFactory();
        Assert.assertNotNull(mAccessFactory);
        mPluginObserver = new PluginObserver();
        mDbDirPlusDbName = getAbsoluteDatabaseDirectory(PLUGINDBNAME);
    }

    private class PluginObserver implements Observer<PersistenceObservableEvent> {
        private int seenPluginEvents = 0;

        public void eventOccurred(final PersistenceObservableEvent observableEvent) {
            LOGGER.info(observableEvent.getDescription());
            if (observableEvent.getDescription().matches("^(Creating|Populating) DatabaseCreationAppPlugin data$")) {
                seenPluginEvents++;
            }
        }

        /**
         * @return the seenPluginEvent
         */
        public boolean haveSeenPluginEvent() {
            return seenPluginEvents == 2;
        }
    }

    /**
     *
     */
    @Test
    public void pluginsReceiveWizardResultsCreateAndOpenDatabase() {
        final Map<String, Object> pluginProperties = createPluginProperties();
        MiniMiserDAOFactory miniMiserDaoFactory = null;
        try {
            final InstanceSet<DAOFactory> databases = mAccessFactory.
            createDatabase(mDbDirPlusDbName, "", mPluginObserver, pluginProperties);
            miniMiserDaoFactory = databases.getInstanceOf(MiniMiserDAOFactory.class);
            Assert.assertTrue(mDatabaseCreationAppPlugin.correctPluginPropertiesPassed());
            Assert.assertTrue(mDatabaseCreationAppPlugin.allCreationMethodsCalled());
            Assert.assertTrue(mDatabaseCreationAppPlugin.allOpeningMethodsCalled());
            Assert.assertNotNull(databases.getInstanceOf(DatabaseOpeningDAOFactory.class));
            Assert.assertTrue(mPluginObserver.haveSeenPluginEvent());
        } finally {
            try {
                if (miniMiserDaoFactory != null) {
                    miniMiserDaoFactory.close();
                }
            } finally {
                deleteDatabaseFiles(PLUGINDBNAME);
            }
        }
    }

    private HashMap<String, Object> createPluginProperties() {
        final HashMap<String, Object> results = new HashMap<String, Object>();
        results.put(DatabaseCreationAppPlugin.WIZARDKEY, DatabaseCreationAppPlugin.WIZARDVALUE);
        return results;
    }
}
