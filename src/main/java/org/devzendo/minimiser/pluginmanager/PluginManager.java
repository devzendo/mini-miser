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

import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.plugin.ApplicationPlugin;
import org.devzendo.minimiser.plugin.Plugin;



/**
 * The Plugin Manager presents customisation data provided by the
 * current set of plugins.
 * 
 * @author matt
 *
 */
public interface PluginManager {
    
    /**
     * Load the plugins, given a resource path to the plugin
     * properties file.
     * @param propertiesResourcePath the path to the properties
     * file, e.g. META-INF/minimiser/plugin.properties
     * @throws PluginException on any load failures.
     */
    void loadPlugins(String propertiesResourcePath) throws PluginException;
    
    /**
     * @return the set of loaded Plugins, including the single
     * ApplicationPlugin.
     */
    List<Plugin> getPlugins();
    
    /**
     * @return the single ApplicationPlugin
     */
    ApplicationPlugin getApplicationPlugin();

    /**
     * @param <F> the type of facade-implementor to return 
     * @param facadeType the type of the facade-implementor to return
     * @return a list of plugins implementing the given type
     */
    <F> List<F> getPluginsImplementingFacade(Class<F> facadeType);

    /**
     * Add an observer of PluginEvents
     * @param observer the observer
     */
    void addPluginEventObserver(Observer<PluginEvent> observer);

    /**
     * Remove an observer of PluginEvents.
     * @param observer the observer to remove.
     */
    void removePluginEventObserver(Observer<PluginEvent> observer);
}
