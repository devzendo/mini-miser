package uk.me.gumbley.minimiser.pluginmanager;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import uk.me.gumbley.minimiser.springloader.ApplicationContext;
import uk.me.gumbley.minimiser.springloader.SpringLoadedBean;
import uk.me.gumbley.minimiser.springloader.SpringLoader;
import uk.me.gumbley.minimiser.springloader.SpringLoaderUnittestCase;

/**
 * Test that the plugin manager loads plugins and binds their
 * application contexts into the SpringLoader
 * @author matt
 *
 */
@ApplicationContext("uk/me/gumbley/minimiser/pluginmanager/MainAppContext.xml")
public class TestPluginManagerAddsToSpringLoaderApplicationContext extends SpringLoaderUnittestCase {
    
    /**
     * @throws PluginException nope
     */
    @Test
    public void pluginsExtendSpringLoaderAppcontexts() throws PluginException {
        final DefaultPluginManager defaultPluginManager = new DefaultPluginManager();
        defaultPluginManager.loadPlugins("uk/me/gumbley/minimiser/pluginmanager/goodplugin.properties");
        final List<Plugin> plugins = defaultPluginManager.getPlugins();
        Assert.assertEquals(2, plugins.size());
        
        // this bean is defined in the AppPlugin's declared
        // application context
        final SpringLoader sl = getSpringLoader();
        final SpringLoadedBean o1 = sl.getBean("testBean",
            SpringLoadedBean.class);
        Assert.assertEquals(31415, o1.getTheAnswer());
    }
}
