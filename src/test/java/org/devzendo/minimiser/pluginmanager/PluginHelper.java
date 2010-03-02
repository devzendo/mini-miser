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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.devzendo.minimiser.plugin.ApplicationPlugin;
import org.devzendo.minimiser.plugin.Plugin;


/**
 * A helper class for building tests involving plugins.
 * 
 * @author matt
 *
 */
public final class PluginHelper {
    private final PluginManager mPluginManager;

    /**
     * Package-private constructor; use the PluginHelperFactory
     * to instantiate one.
     * @param pluginManager the PluginManager to use
     */
    PluginHelper(final PluginManager pluginManager) {
        mPluginManager = pluginManager;

    }

    /**
     * Load the plugins using the resource path to the standard
     * plugins.properties that lists plugins to be loaded.
     * Typically called from a @Before method.
     * @return a list of loaded plugins
     * @throws PluginException on plugin load or initialisation
     * problems
     */
    public List<Plugin> loadStandardPlugins() throws PluginException {
        return loadPlugins("META-INF/minimiser/plugins.properties");
    }
    
    /**
     * Load the plugins given a resource path to a properties file
     * that lists the plugins to be loaded. Typically called from
     * a @Before method.
     * @param propertiesResourcePath the path to the properties
     * file
     * @return a list of loaded plugins
     * @throws PluginException on plugin load or initialisation
     * problems
     */
    public List<Plugin> loadPlugins(final String propertiesResourcePath) throws PluginException {
        mPluginManager.loadPlugins(propertiesResourcePath);
        return mPluginManager.getPlugins();
    }
    
    /**
     * Obtain the application plugin.
     * @return the application plugin, if loaded
     */
    public ApplicationPlugin getApplicationPlugin() {
        return mPluginManager.getApplicationPlugin();
    }
    
    /**
     * @param <F> the type of facade-implementor to return 
     * @param facadeType the type of the facade-implementor to return
     * @return a list of plugins implementing the given type
     */
    public <F> List<F> getPluginsImplementingFacade(final Class<F> facadeType) {
        return mPluginManager.getPluginsImplementingFacade(facadeType);
    }

    /**
     * Obtain the plugin manager
     * @return the plugin manager
     */
    public PluginManager getPluginManager() {
        return mPluginManager;
    }

    /**
     * To help with testing that a list of Plugins has been
     * correctly built, transform it into a Map, keyed on the
     * Plugin name
     * @param plugins a List of Plugins
     * @return a Map of Plugin Name => Plugin Instance
     */
    public static Map<String, Plugin> pluginsToMap(final List<Plugin> plugins) {
        final Map<String, Plugin> pluginMap = new HashMap<String, Plugin>();
        for (Plugin plugin : plugins) {
            pluginMap.put(plugin.getName(), plugin);
        }
        return pluginMap;
    }

}
