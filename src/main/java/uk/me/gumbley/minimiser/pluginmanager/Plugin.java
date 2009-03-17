package uk.me.gumbley.minimiser.pluginmanager;

/**
 * The interface that all plugins must implement to tell the
 * framework about themselves.
 * 
 * @author matt
 *
 */
public interface Plugin extends com.mycila.plugin.api.Plugin {
    /**
     * @return the name of this plugin
     */
    String getName();
    
    /**
     * @return the version of this plugin
     */
    String getVersion();
}
