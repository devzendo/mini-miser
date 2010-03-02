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

import java.util.List;

import org.devzendo.minimiser.plugin.AppPlugin;
import org.devzendo.minimiser.plugin.Plugin;
import org.devzendo.minimiser.plugin.SpringContractPlugin;
import org.devzendo.minimiser.springloader.ApplicationContext;
import org.devzendo.minimiser.springloader.SpringLoadedBean;
import org.devzendo.minimiser.springloader.SpringLoader;
import org.devzendo.minimiser.springloader.SpringLoaderUnittestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Test that the plugin manager loads plugins and binds their
 * application contexts into the SpringLoader
 * @author matt
 *
 */
@ApplicationContext("org/devzendo/minimiser/pluginmanager/MainAppContext.xml")
public final class TestPluginManagerAddsToSpringLoaderApplicationContext extends SpringLoaderUnittestCase {

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
    public void pluginsExtendSpringLoaderAppContexts() throws PluginException {
        mDefaultPluginManager.loadPlugins("org/devzendo/minimiser/plugin/goodplugin.properties");
        final List<Plugin> plugins = mDefaultPluginManager.getPlugins();
        Assert.assertEquals(2, plugins.size());

        // this bean is defined in the AppPlugin's declared
        // application context
        final SpringLoader sl = getSpringLoader();
        final SpringLoadedBean o1 = sl.getBean("testBean",
            SpringLoadedBean.class);
        Assert.assertEquals(31415, o1.getTheAnswer());
    }

    /**
     * @throws PluginException nope
     */
    @Test
    public void pluginsAreGivenSpringLoader() throws PluginException {
        mDefaultPluginManager.loadPlugins("org/devzendo/minimiser/plugin/goodplugin.properties");
        final List<Plugin> plugins = mDefaultPluginManager.getPlugins();
        Assert.assertEquals(2, plugins.size());

        final AppPlugin appPlugin = (AppPlugin) mDefaultPluginManager.getApplicationPlugin();

        Assert.assertNotNull(getSpringLoader());
        Assert.assertSame(getSpringLoader(), appPlugin.getSpringLoader());
    }

    /**
     * @throws PluginException nope
     */
    @Test
    public void pluginsNameAndVersionNotRequestedUntilSpringInitialised() throws PluginException {
        mDefaultPluginManager.loadPlugins("org/devzendo/minimiser/plugin/springcontractplugin.properties");
        final List<Plugin> plugins = mDefaultPluginManager.getPlugins();
        Assert.assertEquals(1, plugins.size());

        final SpringContractPlugin appPlugin = (SpringContractPlugin) mDefaultPluginManager.getApplicationPlugin();
        Assert.assertTrue(appPlugin.nameAndVersionNotCalledUntilSpringInitialised());
    }
}
