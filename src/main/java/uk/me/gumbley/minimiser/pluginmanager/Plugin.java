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
 * The following calls are then made to obtain metadata about the
 * plugin for binding into the pluginregistry - they can also
 * be called at any time during normal operation:
 * <ul>
 * <li> getSchemaVersion() is called when this plugin's database
 *      schema is being checked or stored in the database.
 * <li> getUpdateSiteURL() is called whenever update availability
 *      checks are being performed, if this facility has been
 *      enabled
 * <li> getDevelopersContactDetails() is called whenever this info
 *      is displayed to the user, e.g. in the About or Problem
 *      dialogs.
 * <li> getLicenseDetails() is called whenever the plugin is being
 *      described, e.g. in the About dialog.
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
     * Where is the update site for this plugin?
     * 
     * The files 'version.txt' and 'changes'txt' should be
     * available at this URL.
     * 
     * @return the Base URL of the update site for this plugin 
     */
    String getUpdateSiteBaseURL();

    /**
     * How can users of this plugin contact the developers?
     * 
     * This may be a personal address, or mailing list. It is
     * displayed in the About box, and if this is the Application
     * Plugin, shown in the Problem Reporter.
     * 
     * @return an email address, or could be a URL.
     */
    String getDevelopersContactDetails();

    /**
     * Obtain a short description of the copyright/license details
     * of this plugin. Used by the About dialog.
     * 
     * e.g. (C) 2009 Algebraic, Inc.
     * or GPL2 2009 The FrooBar project
     * 
     * @return short copyright/license text.
     */
    String getLicenseDetails();
    
    /**
     * Shut down the plugin, freeing any resources. Called by the
     * framework upon system shutdown.
     */
    void shutdown();
}
