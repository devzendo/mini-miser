package uk.me.gumbley.minimiser.pluginmanager;

import java.util.List;

import uk.me.gumbley.minimiser.springloader.SpringLoader;

/**
 * The interface that all plugins must implement to tell the
 * framework about themselves.
 * <p>
 * After instantiating each plugin via its no-arg constructor,
 * the framework also makes several lifecycle method calls into
 * the plugin:
 * <ul>
 * <li> getApplicationContexts() allows the plugin to register
 *      any custom XML application context files provided as
 *      resources</li>
 * <li> setSpringLoader() is called after all plugins' application
 *      contexts have been bound to the SpringLoader.</li>
 * </ul>
 *  
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
    
    /**
     * Provide any plugin-specific application context files.
     * Can return null or an empty list if there are none.
     * The elements of the list are paths to resources inside the
     * plugin's jar, e.g. uk/me/gumbley/myapp/app.xml
     * @return a list of any application contexts that the plugin
     * needs to add to the SpringLoader, or null, or empty list.
     */
    List<String> getApplicationContextResourcePaths();
    
    /**
     * Obtain the SpringLoader for plugin use
     * @return the SpringLoader
     */
    SpringLoader getSpringLoader();

    /**
     * Give the SpringLoader to the plugin, after the
     * application contexts for all plugins have been loaded
     * @param springLoader the SpringLoader
     */
    void setSpringLoader(final SpringLoader springLoader);
}
