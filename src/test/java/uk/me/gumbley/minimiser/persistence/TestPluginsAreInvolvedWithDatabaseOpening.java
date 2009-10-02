package uk.me.gumbley.minimiser.persistence;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.me.gumbley.minimiser.plugin.Plugin;
import uk.me.gumbley.minimiser.plugin.facade.opendatabase.DatabaseOpening;
import uk.me.gumbley.minimiser.pluginmanager.PluginException;
import uk.me.gumbley.minimiser.pluginmanager.PluginManager;
import uk.me.gumbley.minimiser.util.InstanceSet;


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
        mPluginManager.loadPlugins("uk/me/gumbley/minimiser/persistence/persistenceopeningplugin.properties");
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
            createDatabase();
            
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

    private void createDatabase() {
        mAccessFactory.createDatabase(mDbDirPlusDbName, "").getInstanceOf(MiniMiserDAOFactory.class).close();        
    }
}
