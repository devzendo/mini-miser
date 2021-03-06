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
import java.util.Map;

import org.devzendo.commoncode.logging.LoggingUnittestHelper;
import org.devzendo.minimiser.plugin.ApplicationPlugin;
import org.devzendo.minimiser.plugin.Plugin;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;



/**
 * Tests that plugin classes can be instantiated by the
 * PluginLoader.
 *
 * @author matt
 *
 */
public final class TestPluginLoader {
    private PluginLoader mPluginLoader;
    /**
     * 
     */
    @BeforeClass
    public static void setupLogging() {
        LoggingUnittestHelper.setupLogging();
    }

    /**
     *
     */
    @Before
    public void getPrerequisites() {
        mPluginLoader = new DefaultPluginLoader();
    }

    /**
     * @throws PluginException never
     */
    @Test
    public void loadsJavaPluginsFromProperties() throws PluginException {
        final List<Plugin> plugins = mPluginLoader.loadPluginsFromClasspath("org/devzendo/minimiser/plugin/goodplugin.properties");
        checkCorrectPluginDetails(plugins);
    }

    /**
     * This test is ignored until the switch to Spring 3.0, and
     * a more modern JRuby.
     * @throws PluginException never
     */
    @Ignore
    @Test
    public void loadsJRubyPluginsFromProperties() throws PluginException {
        final List<Plugin> plugins = mPluginLoader.loadPluginsFromClasspath("org/devzendo/minimiser/plugin/goodrubyplugin.properties");
        checkCorrectPluginDetails(plugins);
    }

    private void checkCorrectPluginDetails(final List<Plugin> plugins) {
        Assert.assertEquals(2, plugins.size());
        final Map<String, Plugin> pluginMap = PluginHelper.pluginsToMap(plugins);

        final ApplicationPlugin appPlugin = (ApplicationPlugin) pluginMap.get("Application");
        Assert.assertNotNull(appPlugin);
        Assert.assertTrue(appPlugin instanceof ApplicationPlugin);
        Assert.assertEquals("Application", appPlugin.getName());
        Assert.assertEquals("1.0.0", appPlugin.getVersion());
        Assert.assertEquals("1.0", appPlugin.getSchemaVersion());

        final Plugin normalPlugin = pluginMap.get("Normal");
        Assert.assertNotNull(normalPlugin);
        Assert.assertFalse(normalPlugin instanceof ApplicationPlugin);
        Assert.assertEquals("Normal", normalPlugin.getName());
        Assert.assertEquals("1.0.1", normalPlugin.getVersion());
        Assert.assertNull(normalPlugin.getSchemaVersion());
    }
}
