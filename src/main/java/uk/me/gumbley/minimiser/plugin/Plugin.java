package uk.me.gumbley.minimiser.plugin;

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
 * <li> getApplicationContexts() allows the plugin to register
 *      any custom XML application context files provided as
 *      resources</li>
 * <li> ...all plugins' application contexts are then loaded...</li>
 * <li> setSpringLoader() is called after all plugins' application
 *      contexts have been bound to the SpringLoader.</li>
 * <li> getName() and getVersion() are called</li>
 * </ul>
 * <p/>
 * The following calls are then made to obtain metadata about the
 * plugin for binding into the pluginregistry - they can also
 * be called at any time during normal operation:
 * <ul>
 * <li> getSchemaVersion() is called when this plugin's database
 *      schema is being checked or stored in the database.
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
     * What is the name of this plugin?
     * 
     * The framework will use this name when storing the versions
     * (code version and schema version) in the database, and, if
     * this is the Application Plugin, in the help menu, main frame
     * title bar, and other displays wherever the application name
     * is shown.
     * 
     * The column used to store this in the database is 40
     * characters wide.
     *  
     * @return the name of this plugin
     */
    String getName();
    
    /**
     * What is the version number of this plugin's code?
     * 
     * The framework will use this name when storing the versions
     * in the database.
     * 
     * @return the code version of this plugin
     */
    String getVersion();
    
    /**
     * Allow a plugin to provide any plugin-specific application
     * context files.
     * 
     * Can return null or an empty list if there are none.
     * The elements of the list are paths to resources inside the
     * plugin's jar, e.g. uk/me/gumbley/myapp/app.xml
     * 
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
     * What is the database schema version of this plugin?
     * 
     * The framework will use this name when storing the versions
     * in the database.
     * 
     * @return the database schema version of this plugin
     */
    String getSchemaVersion();
    
    /**
     * Shut down the plugin, freeing any resources. Called by the
     * framework upon system shutdown.
     */
    void shutdown();
}
