package uk.me.gumbley.minimiser.pluginmanager;

import java.util.List;


/**
 * The Plugin Manager presents customisation data provided by the
 * current set of plugins.
 * 
 * @author matt
 *
 */
public interface PluginManager {
    /**
     * TODO move into Plugin
     * Where is the update site for this app?
     * @return the Base URL of the update site for this app. 
     * @deprecated
     */
    @Deprecated
    String getUpdateSiteBaseURL();
    
    /**
     * Load the plugins, given a resource path to the plugin
     * properties file.
     * @param propertiesResourcePath the path to the properties
     * file, e.g. META-INF/minimiser/plugin.properties
     * @throws PluginException on any load failures.
     */
    void loadPlugins(final String propertiesResourcePath) throws PluginException;
    
    /**
     * @return the set of loaded Plugins, including the single
     * ApplicationPlugin.
     */
    List<Plugin> getPlugins();
    
    /**
     * @return the single ApplicationPlugin
     */
    ApplicationPlugin getApplicationPlugin();
}