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

/**
 * The PluginRegistry holds all the PluginDescriptors formed
 * when the PluginManager successfully loads the Plugins.
 *  
 * @author matt
 *
 */
public interface PluginRegistry {
    /**
     * Name text returned when there is no application plugin
     */
    String UNKNOWN_APPLICATION = "<Unknown Application>";

    /**
     * Version Text returned when there is no application plugin
     */
    String UNKNOWN_VERSION = "<Unknown Version>";
    
    /**
     * @return the application plugin's descriptor, or null
     * if none has been loaded.
     */
    ApplicationPluginDescriptor getApplicationPluginDescriptor();
    
    /**
     * @return the list of plugin descriptors, which could be
     * empty, but never null. This would also contain the
     * application plugin, if there is one.
     */
    List<PluginDescriptor> getPluginDescriptors();
    
    /**
     * Add a plugin descriptor to the registry.
     * @param pluginDescriptor the descriptor to add
     */
    void addPluginDescriptor(PluginDescriptor pluginDescriptor);
    
    /**
     * @return the application's name, or <Unknown Application>
     * if no application plugin has been added. 
     */
    String getApplicationName();

    /**
     * @return the application's version, or <Unknown Version>
     * if no application plugin has been added. 
     */
    String getApplicationVersion();
}
