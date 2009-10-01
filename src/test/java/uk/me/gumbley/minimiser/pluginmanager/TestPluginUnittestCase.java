package uk.me.gumbley.minimiser.pluginmanager;

import org.junit.Assert;
import org.junit.Test;

import uk.me.gumbley.minimiser.plugin.AppPlugin;
import uk.me.gumbley.minimiser.plugin.ApplicationPlugin;
import uk.me.gumbley.minimiser.springloader.SpringLoadedBean;
import uk.me.gumbley.minimiser.springloader.SpringLoader;

/**
 * Do PluginUnitTestCases receive the environment they expect?
 * @author matt
 *
 */
@PluginUnderTest("uk.me.gumbley.minimiser.plugin.AppPlugin")
public final class TestPluginUnittestCase extends PluginUnittestCase {
    /**
     * 
     */
    @Test
    public void pluginEnvironmentIsValid() {
        final ApplicationPlugin applicationPlugin = getApplicationPlugin();
        Assert.assertNotNull(applicationPlugin);
        final AppPlugin appPlugin = (AppPlugin) applicationPlugin;
        final SpringLoader springLoader = appPlugin.getSpringLoader();
        Assert.assertNotNull(springLoader);
        final SpringLoadedBean springLoadedBean = springLoader.getBean("testBean", SpringLoadedBean.class);
        Assert.assertNotNull(springLoadedBean);
        Assert.assertEquals(31415, springLoadedBean.getTheAnswer());
    }
}
