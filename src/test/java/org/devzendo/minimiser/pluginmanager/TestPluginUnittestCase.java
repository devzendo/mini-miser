package org.devzendo.minimiser.pluginmanager;

import org.devzendo.minimiser.plugin.AppPlugin;
import org.devzendo.minimiser.plugin.ApplicationPlugin;
import org.devzendo.minimiser.springloader.SpringLoadedBean;
import org.devzendo.minimiser.springloader.SpringLoader;
import org.junit.Assert;
import org.junit.Test;


/**
 * Do PluginUnitTestCases receive the environment they expect?
 * @author matt
 *
 */
@PluginUnderTest("org.devzendo.minimiser.plugin.AppPlugin")
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
