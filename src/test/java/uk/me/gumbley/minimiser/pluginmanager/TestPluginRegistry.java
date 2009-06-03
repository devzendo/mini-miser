package uk.me.gumbley.minimiser.pluginmanager;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the plugin registry.
 * 
 * @author matt
 *
 */
public final class TestPluginRegistry {
    private DefaultPluginRegistryImpl mPluginRegistry;
    private PluginDescriptor mAppPluginDescriptor;
    private PluginDescriptor mNormalPluginDescriptor;
    
    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        mPluginRegistry = new DefaultPluginRegistryImpl();
        mAppPluginDescriptor = PluginDescriptor.createApplicationPluginDescriptor(
            "App Name", "1.0.0", "3.4", "http://localhost", "developers@appplugin.com");
        mNormalPluginDescriptor = PluginDescriptor.createPluginDescriptor(
            "Plugin Name", "0.1.0", "4.5", "http://theirhost", "developers@plugin.co.uk");
    }

    /**
     * 
     */
    @Test
    public void validatePluginDescriptors() {
        Assert.assertTrue(mAppPluginDescriptor.isApplication());
        Assert.assertFalse(mNormalPluginDescriptor.isApplication());
    }

    /**
     * 
     */
    @Test
    public void emptiness() {
        Assert.assertNull(mPluginRegistry.getApplicationPluginDescriptor());
        Assert.assertEquals(Collections.EMPTY_LIST, mPluginRegistry.getPluginDescriptors());
    }

    /**
     * 
     */
    @Test
    public void applicationAddedToRegistryCanBeFound() {
        mPluginRegistry.addPluginDescriptor(mAppPluginDescriptor);
        Assert.assertSame(mAppPluginDescriptor, mPluginRegistry.getApplicationPluginDescriptor());
        final List<PluginDescriptor> pluginDescriptors = mPluginRegistry.getPluginDescriptors();
        Assert.assertEquals(1, pluginDescriptors.size());
        Assert.assertSame(mAppPluginDescriptor, pluginDescriptors.get(0));
    }
    
    /**
     * 
     */
    @Test
    public void pluginAddedMultipleTimesIsOnlyReturnedOnce() {
        mPluginRegistry.addPluginDescriptor(mAppPluginDescriptor);
        mPluginRegistry.addPluginDescriptor(mAppPluginDescriptor);
        mPluginRegistry.addPluginDescriptor(mNormalPluginDescriptor);
        mPluginRegistry.addPluginDescriptor(mNormalPluginDescriptor);
        Assert.assertSame(mAppPluginDescriptor, mPluginRegistry.getApplicationPluginDescriptor());
        final List<PluginDescriptor> pluginDescriptors = mPluginRegistry.getPluginDescriptors();
        int appIndex = -1;
        int normalIndex = -1;
        for (int i = 0; i < pluginDescriptors.size(); i++) {
            final PluginDescriptor pluginDescriptor = pluginDescriptors.get(i);
            if (pluginDescriptor.isApplication()) {
                appIndex = i;
            } else {
                normalIndex = i;
            }
        }
        Assert.assertTrue(appIndex != -1);
        Assert.assertTrue(normalIndex != -1);
        Assert.assertEquals(2, pluginDescriptors.size());
        Assert.assertSame(mAppPluginDescriptor, pluginDescriptors.get(appIndex));
        Assert.assertSame(mNormalPluginDescriptor, pluginDescriptors.get(normalIndex));
    }
}
