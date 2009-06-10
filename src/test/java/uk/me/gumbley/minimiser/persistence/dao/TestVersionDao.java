package uk.me.gumbley.minimiser.persistence.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.me.gumbley.minimiser.persistence.MiniMiserDatabase;
import uk.me.gumbley.minimiser.persistence.PersistenceUnittestCase;
import uk.me.gumbley.minimiser.persistence.domain.Version;
import uk.me.gumbley.minimiser.persistence.domain.VersionableEntity;
import uk.me.gumbley.minimiser.pluginmanager.ApplicationPlugin;
import uk.me.gumbley.minimiser.pluginmanager.Plugin;
import uk.me.gumbley.minimiser.pluginmanager.PluginManager;


/**
 * Tests the Version DAO
 * 
 * @author matt
 *
 */
public final class TestVersionDao extends PersistenceUnittestCase {
    private static final Logger LOGGER = Logger.getLogger(TestVersionDao.class);
    
    private PluginManager mPluginManager;
    
    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        mPluginManager = getSpringLoader().getBean("pluginManager", PluginManager.class);
    }
    
    /**
     * Is the VERSIONS table populated correctly?
     */
    @Test
    public void checkVersionPopulation() { 
        final String dbName = "checkversionpopulation";
        doSimpleCreateDatabaseBoilerPlate(getAccessFactory(), dbName, "", new RunOnMiniMiserDatabase() {
            
            public void runOnMiniMiserDatabase(final MiniMiserDatabase openedDatabase) {
                final Plugin appPlugin = getAppPlugin();
                Assert.assertNotNull(appPlugin);
                checkVersionForPlugin(appPlugin, openedDatabase.getVersionDao());
                
                final Plugin normalPlugin = getNormalPlugin();
                Assert.assertNotNull(normalPlugin);
                checkVersionForPlugin(normalPlugin, openedDatabase.getVersionDao());
            }

            private Plugin getNormalPlugin() {
                final List<Plugin> plugins = TestVersionDao.this.mPluginManager.getPlugins();
                for (Plugin plugin : plugins) {
                    if (!(plugin instanceof ApplicationPlugin)) {
                        return plugin;
                    }
                }
                return null;
            }
            
            private Plugin getAppPlugin() {
                final List<Plugin> plugins = TestVersionDao.this.mPluginManager.getPlugins();
                for (Plugin plugin : plugins) {
                    if (plugin instanceof ApplicationPlugin) {
                        return plugin;
                    }
                }
                return null;
            }

            private void checkVersionForPlugin(
                    final Plugin plugin,
                    final VersionDao versionDao) {
                
                final Version dbVersion = versionDao.findVersion(plugin.getName(), VersionableEntity.SCHEMA_VERSION);
                LOGGER.info(String.format("... schema version returned from db should not be null - it is %s", dbVersion));
                Assert.assertNotNull(dbVersion);
                Assert.assertEquals(plugin.getName(), dbVersion.getPluginName());
                Assert.assertEquals(VersionableEntity.SCHEMA_VERSION, dbVersion.getEntity());
                Assert.assertEquals(plugin.getSchemaVersion(), dbVersion.getVersion());
                //
                final Version appVersion = versionDao.findVersion(plugin.getName(), VersionableEntity.APPLICATION_VERSION);
                LOGGER.info(String.format("... application version returned from db should not be null - it is %s", appVersion));
                Assert.assertNotNull(dbVersion);
                Assert.assertEquals(plugin.getName(), appVersion.getPluginName());
                Assert.assertEquals(VersionableEntity.APPLICATION_VERSION, appVersion.getEntity());
                Assert.assertEquals(plugin.getVersion(), appVersion.getVersion());
            }
            
        });
    }
}
