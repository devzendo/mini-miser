package uk.me.gumbley.minimiser.pluginmanager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.plugin.ApplicationPlugin;
import uk.me.gumbley.minimiser.plugin.Plugin;
import uk.me.gumbley.minimiser.plugin.facade.FacadeA;
import uk.me.gumbley.minimiser.plugin.facade.FacadeB;
import uk.me.gumbley.minimiser.plugin.facade.FacadeNeverImplemented;


/**
 * @author matt
 *
 */
public final class TestPluginManager extends LoggingTestCase {
    private DefaultPluginManager mDefaultPluginManager;
    private PluginRegistry mPluginRegistry;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        mPluginRegistry = new DefaultPluginRegistry();
        mDefaultPluginManager = new DefaultPluginManager(null, mPluginRegistry);
    }
    
    /**
     * @throws PluginException never
     */
    @Test
    public void loadGoodPlugin() throws PluginException {
        mDefaultPluginManager.loadPlugins("uk/me/gumbley/minimiser/plugin/goodplugin.properties");
        final List<Plugin> plugins = mDefaultPluginManager.getPlugins();
        Assert.assertEquals(2, plugins.size());
        final Map<String, Plugin> pluginMap = pluginsToMap(plugins);
        final ApplicationPlugin appPlugin = (ApplicationPlugin) pluginMap.get("Application");
        Assert.assertNotNull(appPlugin);
        final Plugin normalPlugin = pluginMap.get("Normal");
        Assert.assertNotNull(normalPlugin);
        
        Assert.assertEquals("1.0.1", normalPlugin.getVersion());
        Assert.assertEquals("Normal", normalPlugin.getName());
        Assert.assertEquals("1.0.0", appPlugin.getVersion());
        Assert.assertEquals("Application", appPlugin.getName());
        Assert.assertNotNull(mDefaultPluginManager.getApplicationPlugin());
        Assert.assertSame(mDefaultPluginManager.getApplicationPlugin(), appPlugin);
    }
    
    /**
     * @throws PluginException not in this test
     */
    @SuppressWarnings("unchecked")
    @Test
    public void loadingApplicationPluginFiresListeners() throws PluginException {
        final Observer<PluginEvent> obs = EasyMock.createStrictMock(Observer.class);
        obs.eventOccurred(EasyMock.eq(
            new ApplicationPluginLoadedEvent(new ApplicationPluginDescriptor(
                    true,
                    "Application",
                    "1.0.0",
                    "1.0",
                    "http://localhost/",
                    "bob@aol.com",
                    "(C) 2009 J. R. Bob Dobbs",
                    "COPYING.txt",
                    "About.txt",
                    "changelog.txt",
                    null))));
        EasyMock.replay(obs);

        mDefaultPluginManager.addPluginEventObserver(obs);
        mDefaultPluginManager.loadPlugins("uk/me/gumbley/minimiser/plugin/goodplugin.properties");
        
        EasyMock.verify(obs);
    }
    
    /**
     * @throws PluginException never
     */
    @Test
    public void appPluginHasItsDetailsStoredInPluginRegistry() throws PluginException {
        Assert.assertEquals(PluginRegistry.UNKNOWN_APPLICATION, mPluginRegistry.getApplicationName());
        Assert.assertEquals(PluginRegistry.UNKNOWN_VERSION, mPluginRegistry.getApplicationVersion());

        mDefaultPluginManager.loadPlugins("uk/me/gumbley/minimiser/plugin/goodplugin.properties");
        
        Assert.assertEquals("Application", mPluginRegistry.getApplicationName());
        Assert.assertEquals("1.0.0", mPluginRegistry.getApplicationVersion());
    }
    
    /**
     * @throws PluginException yes
     */
    @Test(expected = PluginException.class)
    public void thereMustBeAnApplicationPlugin() throws PluginException {
        final DefaultPluginManager defaultPluginManager = new DefaultPluginManager(null, mPluginRegistry);
        defaultPluginManager.loadPlugins("uk/me/gumbley/minimiser/plugin/noappplugin.properties");
    }
    
    /**
     * @throws PluginException yes
     */
    @Test(expected = PluginException.class)
    public void thereMustBeOnlyOneApplicationPlugin() throws PluginException {
        final DefaultPluginManager defaultPluginManager = new DefaultPluginManager(null, mPluginRegistry);
        defaultPluginManager.loadPlugins("uk/me/gumbley/minimiser/plugin/twoappplugin.properties");
    }
    
    // TODO should provide some tests to show that mycila
    // exceptions are caught and rethrown....
    
    /**
     * @throws PluginException yes
     */
    @Test(expected = PluginException.class)
    public void nonexistantPluginPropertiesMustThrow() throws PluginException {
        final DefaultPluginManager defaultPluginManager = new DefaultPluginManager(null, mPluginRegistry);
        defaultPluginManager.loadPlugins("uk/me/gumbley/minimiser/plugin/doesnotexist.properties");
    }

    /**
     * @throws PluginException never
     */
    @Test
    public void pluginFacadesCanBeObtained() throws PluginException {
        mDefaultPluginManager.loadPlugins("uk/me/gumbley/minimiser/plugin/pluginswithfacades.properties");
        final List<Plugin> plugins = mDefaultPluginManager.getPlugins();
        
        Assert.assertEquals(4, plugins.size());
        final Map<String, Plugin> pluginMap = pluginsToMap(plugins);
        Assert.assertNotNull(pluginMap.get("AppFacadeAPlugin"));
        Assert.assertNotNull(pluginMap.get("NormalFacadeAPlugin"));
        Assert.assertNotNull(pluginMap.get("NormalFacadeBPlugin"));
        Assert.assertNotNull(pluginMap.get("NormalFacadeABPlugin"));
        
        final List<FacadeA> facadeAs = mDefaultPluginManager.getPluginsImplementingFacade(FacadeA.class);
        Assert.assertEquals(3, facadeAs.size());
        Assert.assertTrue(facadeAs.contains(pluginMap.get("AppFacadeAPlugin")));
        Assert.assertTrue(facadeAs.contains(pluginMap.get("NormalFacadeAPlugin")));
        Assert.assertTrue(facadeAs.contains(pluginMap.get("NormalFacadeABPlugin")));
        Assert.assertFalse(facadeAs.contains(pluginMap.get("NormalFacadeBPlugin")));

        final List<FacadeB> facadeBs = mDefaultPluginManager.getPluginsImplementingFacade(FacadeB.class);
        Assert.assertEquals(2, facadeBs.size());
        Assert.assertTrue(facadeBs.contains(pluginMap.get("NormalFacadeABPlugin")));
        Assert.assertTrue(facadeBs.contains(pluginMap.get("NormalFacadeBPlugin")));

        final List<FacadeNeverImplemented> facadeNeverImplementeds = mDefaultPluginManager.getPluginsImplementingFacade(FacadeNeverImplemented.class);
        Assert.assertEquals(0, facadeNeverImplementeds.size());
    }

    private Map<String, Plugin> pluginsToMap(final List<Plugin> plugins) {
        final Map<String, Plugin> pluginMap = new HashMap<String, Plugin>();
        for (Plugin plugin : plugins) {
            pluginMap.put(plugin.getName(), plugin);
        }
        return pluginMap;
    }
}
