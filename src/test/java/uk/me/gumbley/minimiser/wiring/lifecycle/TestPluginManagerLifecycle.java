package uk.me.gumbley.minimiser.wiring.lifecycle;

import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.me.gumbley.minimiser.gui.dialog.problem.ProblemReporter;
import uk.me.gumbley.minimiser.pluginmanager.AppPlugin;
import uk.me.gumbley.minimiser.pluginmanager.DefaultPluginManager;
import uk.me.gumbley.minimiser.pluginmanager.Plugin;
import uk.me.gumbley.minimiser.pluginmanager.PluginException;
import uk.me.gumbley.minimiser.pluginmanager.PluginManager;
import uk.me.gumbley.minimiser.springloader.ApplicationContext;
import uk.me.gumbley.minimiser.springloader.SpringLoadedBean;
import uk.me.gumbley.minimiser.springloader.SpringLoaderUnittestCase;

/**
 * Tests the wiring of the plugin manager
 * @author matt
 *
 */
@ApplicationContext("uk/me/gumbley/minimiser/pluginmanager/MainAppContext.xml")
public final class TestPluginManagerLifecycle extends SpringLoaderUnittestCase {
    private PluginManager pluginManager;
    private PluginManagerLifecycle lifecycle;
    
    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        pluginManager = new DefaultPluginManager(getSpringLoader());
    }
    
    /**
     * 
     */
    @Test
    public void pluginGetsSpringLoader() {
        final ProblemReporter problemReporter = EasyMock.createMock(ProblemReporter.class);
        EasyMock.replay(problemReporter);
        
        lifecycle = new PluginManagerLifecycle(pluginManager,
            "uk/me/gumbley/minimiser/pluginmanager/goodplugin.properties",
            problemReporter);
        lifecycle.startup();
        final List<Plugin> plugins = pluginManager.getPlugins();
        Assert.assertEquals(2, plugins.size());
        
        final AppPlugin appPlugin = (AppPlugin) pluginManager.getApplicationPlugin();
        
        Assert.assertSame(getSpringLoader(), appPlugin.getSpringLoader());
        
        // this bean is defined in the AppPlugin's declared
        // application context
        final SpringLoadedBean o1 = getSpringLoader().getBean("testBean",
            SpringLoadedBean.class);
        Assert.assertEquals(31415, o1.getTheAnswer());
        
        EasyMock.verify(problemReporter);
    }
    
    /**
     * 
     */
    @Test
    public void pluginLoadFailureCausesProblemReporting() {
        final ProblemReporter problemReporter = EasyMock.createStrictMock(ProblemReporter.class);
        problemReporter.reportProblem(EasyMock.isA(String.class), EasyMock.isA(PluginException.class));
        EasyMock.replay(problemReporter);
        
        lifecycle = new PluginManagerLifecycle(pluginManager,
            "uk/me/gumbley/minimiser/pluginmanager/noappplugin.properties",
            problemReporter);
        lifecycle.startup();

        EasyMock.verify(problemReporter);
    }
}
