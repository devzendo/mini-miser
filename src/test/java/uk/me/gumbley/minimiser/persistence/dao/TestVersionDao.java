package uk.me.gumbley.minimiser.persistence.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.persistence.MiniMiserDAOFactory;
import uk.me.gumbley.minimiser.persistence.PersistencePluginHelper;
import uk.me.gumbley.minimiser.persistence.domain.Version;
import uk.me.gumbley.minimiser.persistence.domain.VersionableEntity;
import uk.me.gumbley.minimiser.pluginmanager.ApplicationPlugin;
import uk.me.gumbley.minimiser.pluginmanager.Plugin;


/**
 * Tests the Version DAO
 * 
 * @author matt
 *
 */
public final class TestVersionDao extends LoggingTestCase {
    private static final Logger LOGGER = Logger.getLogger(TestVersionDao.class);
    
    private PersistencePluginHelper mPersistencePluginHelper;
    
    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        mPersistencePluginHelper = new PersistencePluginHelper(false, true);
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
        final List<Plugin> plugins = mPersistencePluginHelper.getPluginManager().getPlugins();
        for (Plugin plugin : plugins) {
            if (!(plugin instanceof ApplicationPlugin)) {
                return plugin;
            }
        }
        return null;
    }
    
    private Plugin getAppPlugin() {
        return mPersistencePluginHelper.getPluginManager().getApplicationPlugin();
    }

    private void checkVersionForPlugin(
            final Plugin plugin,
            final VersionDao versionDao,
            final boolean isApplication) {
        
        final Version dbVersion = versionDao.findVersion(plugin.getName(), VersionableEntity.SCHEMA_VERSION);
        LOGGER.info(String.format("... schema version returned from db should not be null - it is %s", dbVersion));
        Assert.assertNotNull(dbVersion);
        Assert.assertEquals(plugin.getName(), dbVersion.getPluginName());
        Assert.assertEquals(VersionableEntity.SCHEMA_VERSION, dbVersion.getEntity());
        Assert.assertEquals(plugin.getSchemaVersion(), dbVersion.getVersion());
        Assert.assertEquals(isApplication, dbVersion.isApplication());
        //
        final Version appVersion = versionDao.findVersion(plugin.getName(), VersionableEntity.APPLICATION_VERSION);
        LOGGER.info(String.format("... application version returned from db should not be null - it is %s", appVersion));
        Assert.assertNotNull(dbVersion);
        Assert.assertEquals(plugin.getName(), appVersion.getPluginName());
        Assert.assertEquals(VersionableEntity.APPLICATION_VERSION, appVersion.getEntity());
        Assert.assertEquals(plugin.getVersion(), appVersion.getVersion());
        Assert.assertEquals(isApplication, appVersion.isApplication());
    }

    /**
     * Is the VERSIONS table populated correctly?
     */
    @Test
    public void checkVersionPopulation() { 
        final String dbName = "checkversionpopulation";
        final VersionDao versionDao = mPersistencePluginHelper.
            createDatabase(dbName, "").
            getInstanceOf(MiniMiserDAOFactory.class).
            getVersionDao();

        final Plugin appPlugin = getAppPlugin();
        Assert.assertNotNull(appPlugin);
        checkVersionForPlugin(appPlugin, versionDao, true);
        
        final Plugin normalPlugin = getNormalPlugin();
        Assert.assertNotNull(normalPlugin);
        checkVersionForPlugin(normalPlugin, versionDao, false);
    }
    
    /**
     * TODO you can insert a Version, but only update the version
     * field, not the isApplication field.
     */
    @Test
    public void pluginsCannotChangeTheirApplicationStatus() {
        Assert.fail("unwritten test");
    }
}
