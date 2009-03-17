package uk.me.gumbley.minimiser.pluginmanager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import uk.me.gumbley.minimiser.logging.LoggingTestCase;


/**
 * @author matt
 *
 */
public final class TestPluginManager extends LoggingTestCase {
    /**
     * @throws PluginException never
     */
    @Test
    public void loadGoodPlugin() throws PluginException {
        final DefaultPluginManager defaultPluginManager = new DefaultPluginManager();
        defaultPluginManager.loadPlugins("uk/me/gumbley/minimiser/pluginmanager/goodplugin.properties");
        final List<Plugin> plugins = defaultPluginManager.getPlugins();
        Assert.assertEquals(2, plugins.size());
        final Map<String, Plugin> pluginMap = new HashMap<String, Plugin>();
        for (Plugin plugin : plugins) {
            pluginMap.put(plugin.getName(), plugin);
        }
        final ApplicationPlugin appPlugin = (ApplicationPlugin) pluginMap.get("Application");
        Assert.assertNotNull(appPlugin);
        final Plugin normalPlugin = pluginMap.get("Normal");
        Assert.assertNotNull(normalPlugin);
        
        Assert.assertEquals("1.0.1", normalPlugin.getVersion());
        Assert.assertEquals("Normal", normalPlugin.getName());
        Assert.assertEquals("1.0.0", appPlugin.getVersion());
        Assert.assertEquals("Application", appPlugin.getName());
        Assert.assertNotNull(defaultPluginManager.getApplicationPlugin());
        Assert.assertSame(defaultPluginManager.getApplicationPlugin(), appPlugin);
    }
    
    /**
     * @throws PluginException yes
     */
    @Test(expected = PluginException.class)
    public void thereMustBeAnApplicationPlugin() throws PluginException {
        final DefaultPluginManager defaultPluginManager = new DefaultPluginManager();
        defaultPluginManager.loadPlugins("uk/me/gumbley/minimiser/pluginmanager/noappplugin.properties");
    }
    
    /**
     * @throws PluginException yes
     */
    @Test(expected = PluginException.class)
    public void thereMustBeOnlyOneApplicationPlugin() throws PluginException {
        final DefaultPluginManager defaultPluginManager = new DefaultPluginManager();
        defaultPluginManager.loadPlugins("uk/me/gumbley/minimiser/pluginmanager/twoappplugin.properties");
    }
    
    // TODO should provide some tests to show that mycila
    // exceptions are caught and rethrown....
    
    /**
     * @throws PluginException yes
     */
    @Test(expected = PluginException.class)
    public void nonexistantPluginPropertiesMustThrow() throws PluginException {
        final DefaultPluginManager defaultPluginManager = new DefaultPluginManager();
        defaultPluginManager.loadPlugins("uk/me/gumbley/minimiser/pluginmanager/doesnotexist.properties");
    }
}
