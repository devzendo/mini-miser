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
}
