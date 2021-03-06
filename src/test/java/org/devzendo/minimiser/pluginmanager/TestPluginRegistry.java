/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devzendo.minimiser.pluginmanager;

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
    private DefaultPluginRegistry mPluginRegistry;
    private ApplicationPluginDescriptor mAppPluginDescriptor;
    private PluginDescriptor mNormalPluginDescriptor;
    private ApplicationPluginDescriptor mBadAppPluginDescriptor;
    private ApplicationPluginDescriptor mNullDetailsAppPluginDescriptor;

    /**
     *
     */
    @Before
    public void getPrerequisites() {
        mPluginRegistry = new DefaultPluginRegistry();
        mAppPluginDescriptor = new ApplicationPluginDescriptor(true,
            "App Name", "1.0.0", "3.4", "http://localhost",
            "developers@appplugin.com", "ASLv2", "License", "About",
            "changelog.txt", null);
        mNormalPluginDescriptor = new PluginDescriptor(false,
            "Plugin Name", "0.1.0", "4.5");
        mBadAppPluginDescriptor = new ApplicationPluginDescriptor(true,
            "", "", "3.4", "http://localhost",
            "developers@appplugin.com", "Apache", "License", "About",
            "changelog.txt", null); // no name or version
        mNullDetailsAppPluginDescriptor = new ApplicationPluginDescriptor(true,
            null, null, "3.4", "http://localhost",
            "developers@appplugin.com", "(C) 2009 MJG", "License", "About",
            "changelog.txt", null); // no name or version
    }

    /**
     *
     */
    @Test
    public void validatePluginDescriptors() {
        Assert.assertTrue(mAppPluginDescriptor.isApplication());
        Assert.assertTrue(mBadAppPluginDescriptor.isApplication());
        Assert.assertTrue(mNullDetailsAppPluginDescriptor.isApplication());
        Assert.assertFalse(mNormalPluginDescriptor.isApplication());
    }

    /**
     *
     */
    @Test
    public void emptiness() {
        Assert.assertNull(mPluginRegistry.getApplicationPluginDescriptor());
        Assert.assertEquals(Collections.EMPTY_LIST, mPluginRegistry.getPluginDescriptors());
        Assert.assertEquals("<Unknown Application>", mPluginRegistry.getApplicationName());
        Assert.assertEquals("<Unknown Version>", mPluginRegistry.getApplicationVersion());
    }

    /**
     *
     */
    @Test
    public void addNoNameOrVersionAppPluginYieldsUnknown() {
        mPluginRegistry.addPluginDescriptor(mBadAppPluginDescriptor);
        Assert.assertEquals("<Unknown Application>", mPluginRegistry.getApplicationName());
        Assert.assertEquals("<Unknown Version>", mPluginRegistry.getApplicationVersion());
    }

    /**
     *
     */
    @Test
    public void addNullNameOrVersionAppPluginYieldsUnknown() {
        mPluginRegistry.addPluginDescriptor(mNullDetailsAppPluginDescriptor);
        Assert.assertEquals("<Unknown Application>", mPluginRegistry.getApplicationName());
        Assert.assertEquals("<Unknown Version>", mPluginRegistry.getApplicationVersion());
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
    @Test(expected = IllegalStateException.class)
    public void multipleApplicationsAreBadForYou() {
        mPluginRegistry.addPluginDescriptor(mAppPluginDescriptor);
        mPluginRegistry.addPluginDescriptor(mBadAppPluginDescriptor);
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
