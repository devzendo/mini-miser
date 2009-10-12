package uk.me.gumbley.minimiser.pluginmanager;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.plugin.ApplicationPlugin;
import uk.me.gumbley.minimiser.plugin.Plugin;


/**
 * Tests that plugin classes can be instantiated by the
 * PluginLoader.
 * 
 * @author matt
 *
 */
public final class TestPluginLoader extends LoggingTestCase {
    private PluginLoader mPluginLoader;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        mPluginLoader = new DefaultPluginLoader();
    }
    
    /**
     * @throws PluginException never
     */
    @Test
    public void loadsJavaPluginsFromProperties() throws PluginException {
        final List<Plugin> plugins = mPluginLoader.loadPluginsFromClasspath("uk/me/gumbley/minimiser/plugin/goodplugin.properties");
        checkCorrectPluginDetails(plugins);
    }
    
    /**
     * This test is ignored until the switch to Spring 3.0, and
     * a more modern JRuby.
     * @throws PluginException never
     */
    @Ignore
    @Test
    public void loadsJRubyPluginsFromProperties() throws PluginException {
        final List<Plugin> plugins = mPluginLoader.loadPluginsFromClasspath("uk/me/gumbley/minimiser/plugin/goodrubyplugin.properties");
        checkCorrectPluginDetails(plugins);
    }

    private void checkCorrectPluginDetails(final List<Plugin> plugins) {
        Assert.assertEquals(2, plugins.size());
        final Map<String, Plugin> pluginMap = PluginHelper.pluginsToMap(plugins);
        
        final ApplicationPlugin appPlugin = (ApplicationPlugin) pluginMap.get("Application");
        Assert.assertNotNull(appPlugin);
        Assert.assertTrue(appPlugin instanceof ApplicationPlugin);
        Assert.assertEquals("Application", appPlugin.getName());
        Assert.assertEquals("1.0.0", appPlugin.getVersion());
        Assert.assertEquals("1.0", appPlugin.getSchemaVersion());
        
        final Plugin normalPlugin = pluginMap.get("Normal");
        Assert.assertNotNull(normalPlugin);
        Assert.assertFalse(normalPlugin instanceof ApplicationPlugin);
        Assert.assertEquals("Normal", normalPlugin.getName());
        Assert.assertEquals("1.0.1", normalPlugin.getVersion());
        Assert.assertNull(normalPlugin.getSchemaVersion());
    }
}
