package uk.me.gumbley.minimiser.pluginmanager;

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
    PluginDescriptor getApplicationPluginDescriptor();
    
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
