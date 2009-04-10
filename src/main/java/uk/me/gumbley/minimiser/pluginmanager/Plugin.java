package uk.me.gumbley.minimiser.pluginmanager;

import java.util.List;

import uk.me.gumbley.minimiser.springloader.SpringLoader;

/**
 * The interface that all plugins must implement to tell the
 * framework about themselves.
 * <p/>
 * After instantiating each plugin via its no-arg constructor,
 * the framework also makes several lifecycle method calls into
 * the plugin, in order:
 * <ul>
 * <li> getName and getVersion are called</li>
 * <li> getApplicationContexts() allows the plugin to register
 *      any custom XML application context files provided as
 *      resources</li>
 * <li> ...all plugins' application contexts are then loaded...</li>
 * <li> setSpringLoader() is called after all plugins' application
 *      contexts have been bound to the SpringLoader.</li>
 * </ul>
 * <p/>
 * The following calls can be called at any time during normal
 * operation:
 * <ul>
 * <li> getUpdateSiteURL() is called whenever update availability
 *      checks are being performed, if this facility has been
 *      enabled
 * </ul>
 * <p/>
 * Upon system shutdown, the following calls will be made, in
 * order:
 * <ul>
 * <li> shutdown() when the framework is closing down.
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
     * Give the SpringLoader to the plugin, after the
     * application contexts for all plugins have been loaded
     * @param springLoader the SpringLoader
     */
    void setSpringLoader(final SpringLoader springLoader);
    
    /**
     * Obtain the SpringLoader for plugin use
     * @return the SpringLoader
     */
    SpringLoader getSpringLoader();
    
    
    /**
     * Where is the update site for this app?
     * The files 'version.txt' and 'changes'txt' should be
     * available at this URL.
     * @return the Base URL of the update site for this app. 
     */
    String getUpdateSiteBaseURL();
        
    
    
    /**
     * Shut down the plugin, freeing any resources. Called by the
     * framework upon system shutdown.
     */
    void shutdown();
}
