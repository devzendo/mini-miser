package org.devzendo.minimiser.pluginmanager;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.devzendo.minimiser.persistence.domain.CurrentSchemaVersion;
import org.devzendo.minimiser.springloader.ApplicationContext;
import org.devzendo.minimiser.springloader.SpringLoaderUnittestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Test that the plugin manager loads the internal plugins and that
 * their name and versions are reasonable.
 *
 * @author matt
 *
 */
@ApplicationContext("org/devzendo/minimiser/pluginmanager/InternalPluginAppContext.xml")
public final class TestPluginManagerAndInternalPlugin extends SpringLoaderUnittestCase {

    private PluginRegistry mPluginRegistry;
    private DefaultPluginManager mDefaultPluginManager;

    /**
     *
     */
    @Before
    public void getPrerequisites() {
        mPluginRegistry = new DefaultPluginRegistry();
        mDefaultPluginManager = new DefaultPluginManager(getSpringLoader(), mPluginRegistry);
    }

    /**
     * @throws PluginException nope
     */
    @Test
    public void loadingStandardPluginDescriptorsCausesInternalPluginToBeLoaded() throws PluginException {
        // Note that this test can't load the plugins from the
        // standard resource, "META-INF/minimiser/plugins.properties"
        // as we're testing the framework's internal plugin, and
        // it's a normal, not an application plugin. Loading just
        // the normal internal plugin without an application
        // plugin is forbidden - so for this test, add in a dummy
        // app.
        mDefaultPluginManager.loadPlugins("org/devzendo/minimiser/plugin/internalplugin.properties");

        final List<PluginDescriptor> pluginDescriptors = mPluginRegistry.getPluginDescriptors();
        Assert.assertEquals(2, pluginDescriptors.size());
        // find the internal plugin
        boolean foundInternalPlugin = false;
        for (final PluginDescriptor pluginDescriptor : pluginDescriptors) {
            if (!pluginDescriptor.isApplication()) {
                foundInternalPlugin = true;
                // these should come from the pom, but no idea what
                // they should be, so just check for something
                Assert.assertFalse(StringUtils.isBlank(pluginDescriptor.getName()));
                Assert.assertFalse(StringUtils.isBlank(pluginDescriptor.getVersion()));

                Assert.assertEquals(CurrentSchemaVersion.getCurrentSchemaVersion(), pluginDescriptor.getSchemaVersion());
            }
        }
        Assert.assertTrue(foundInternalPlugin);
    }
}