package org.devzendo.minimiser.wiring.lifecycle;

import java.util.List;

import org.devzendo.minimiser.gui.dialog.problem.ProblemReporter;
import org.devzendo.minimiser.plugin.AppPlugin;
import org.devzendo.minimiser.plugin.BadShutdownPlugin;
import org.devzendo.minimiser.plugin.Plugin;
import org.devzendo.minimiser.pluginmanager.DefaultPluginManager;
import org.devzendo.minimiser.pluginmanager.DefaultPluginRegistry;
import org.devzendo.minimiser.pluginmanager.PluginException;
import org.devzendo.minimiser.pluginmanager.PluginManager;
import org.devzendo.minimiser.springloader.ApplicationContext;
import org.devzendo.minimiser.springloader.SpringLoadedBean;
import org.devzendo.minimiser.springloader.SpringLoaderUnittestCase;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests the wiring of the plugin manager
 * @author matt
 *
 */
@ApplicationContext("org/devzendo/minimiser/pluginmanager/MainAppContext.xml")
public final class TestPluginManagerLifecycle extends SpringLoaderUnittestCase {
    private PluginManager pluginManager;
    private PluginManagerLifecycle lifecycle;

    /**
     *
     */
    @Before
    public void getPrerequisites() {
        pluginManager = new DefaultPluginManager(getSpringLoader(), new DefaultPluginRegistry());
    }

    /**
     *
     */
    @Test
    public void pluginGetsSpringLoader() {
        final ProblemReporter problemReporter = EasyMock.createMock(ProblemReporter.class);
        EasyMock.replay(problemReporter);

        lifecycle = new PluginManagerLifecycle(pluginManager,
            "org/devzendo/minimiser/plugin/goodplugin.properties",
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
            "org/devzendo/minimiser/pluginmanager/noappplugin.properties",
            problemReporter);
        lifecycle.startup();

        EasyMock.verify(problemReporter);
    }

    /**
     *
     */
    @Test
    public void lifecycleShutdownCausesPluginShutdown() {
        final ProblemReporter problemReporter = EasyMock.createMock(ProblemReporter.class);
        EasyMock.replay(problemReporter);

        lifecycle = new PluginManagerLifecycle(pluginManager,
            "org/devzendo/minimiser/plugin/goodplugin.properties",
            problemReporter);
        lifecycle.startup();

        final AppPlugin appPlugin = (AppPlugin) pluginManager.getApplicationPlugin();
        Assert.assertFalse(appPlugin.hasShutdownBeenCalled());

        lifecycle.shutdown();

        Assert.assertTrue(appPlugin.hasShutdownBeenCalled());

        EasyMock.verify(problemReporter);
    }

    /**
     *
     */
    @Test
    public void pluginShutdownFailureCausesProblemReporting() {
        final ProblemReporter problemReporter = EasyMock.createStrictMock(ProblemReporter.class);
        problemReporter.reportProblem(EasyMock.isA(String.class), EasyMock.isA(Exception.class));
        EasyMock.replay(problemReporter);

        lifecycle = new PluginManagerLifecycle(pluginManager,
            "org/devzendo/minimiser/pluginmanager/badshutdownplugin.properties",
            problemReporter);
        lifecycle.startup();
        lifecycle.shutdown();

        EasyMock.verify(problemReporter);
    }

    /**
     *
     */
    @Test
    public void allPluginsAreShutdownDesipiteFailureOfOne() {
        final ProblemReporter problemReporter = EasyMock.createStrictMock(ProblemReporter.class);

        lifecycle = new PluginManagerLifecycle(pluginManager,
            "org/devzendo/minimiser/pluginmanager/badshutdownplugin.properties",
            problemReporter);
        lifecycle.startup();
        final List<Plugin> plugins = pluginManager.getPlugins();
        assertAllPluginsShutdown(plugins, false);
        lifecycle.shutdown();
        assertAllPluginsShutdown(plugins, true);
    }

    private void assertAllPluginsShutdown(final List<Plugin> plugins, final boolean hasShutdown) {
        for (final Plugin plugin : plugins) {
            if (plugin instanceof BadShutdownPlugin) {
                final BadShutdownPlugin bsp = (BadShutdownPlugin) plugin;
                Assert.assertEquals(hasShutdown, bsp.hasShutdownBeenCalled());
            }
            if (plugin instanceof AppPlugin) {
                final AppPlugin ap = (AppPlugin) plugin;
                Assert.assertEquals(hasShutdown, ap.hasShutdownBeenCalled());
            }
        }
    }
}
