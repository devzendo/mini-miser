package uk.me.gumbley.minimiser.pluginmanager;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
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
        Assert.assertEquals(2, plugins.size());
        final Map<String, Plugin> pluginMap = PluginHelper.pluginsToMap(plugins);
        final ApplicationPlugin appPlugin = (ApplicationPlugin) pluginMap.get("Application");
        Assert.assertNotNull(appPlugin);
        final Plugin normalPlugin = pluginMap.get("Normal");
        Assert.assertNotNull(normalPlugin);
    }
}
