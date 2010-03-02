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

import org.devzendo.minimiser.plugin.Plugin;


/**
 * Given a resource path to a properties file, find all
 * occurrences of these on the classpath, then use them to load
 * a list of Plugin classes.
 *  
 * @author matt
 *
 */
public interface PluginLoader {

    /**
     * Discover all occurrences of the named properties file from
     * the classpath, and log their actual paths. 
     * 
     * @param propertiesResourcePath a resource path to a
     * properties file, e.g. META-INF/minimiser/plugin.properties
     */
    void displayPluginDescriptorResources(final String propertiesResourcePath);

    /**
     * Load all occurrences of the named properties files from
     * the classpath, and then dynamically load each class
     * referred to therein.
     *  
     * @param propertiesResourcePath a resource path to a
     * properties file, e.g. META-INF/minimiser/plugin.properties
     * @return a list of Plugins.
     * @throws PluginException upon plugin load failure
     */
    List<Plugin> loadPluginsFromClasspath(final String propertiesResourcePath) throws PluginException;
}
