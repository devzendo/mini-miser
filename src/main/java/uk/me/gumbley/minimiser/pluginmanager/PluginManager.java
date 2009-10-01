package uk.me.gumbley.minimiser.pluginmanager;

import java.util.List;

import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.plugin.ApplicationPlugin;
import uk.me.gumbley.minimiser.plugin.Plugin;


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
    void loadPlugins(String propertiesResourcePath) throws PluginException;
    
    /**
     * @return the set of loaded Plugins, including the single
     * ApplicationPlugin.
     */
    List<Plugin> getPlugins();
    
    /**
     * @return the single ApplicationPlugin
     */
    ApplicationPlugin getApplicationPlugin();

    /**
     * @param <F> the type of facade-implementor to return 
     * @param facadeType the type of the facade-implementor to return
     * @return a list of plugins implementing the given type
     */
    <F> List<F> getPluginsImplementingFacade(Class<F> facadeType);

    /**
     * Add an observer of PluginEvents
     * @param observer the observer
     */
    void addPluginEventObserver(Observer<PluginEvent> observer);

    /**
     * Remove an observer of PluginEvents.
     * @param observer the observer to remove.
     */
    void removePluginEventObserver(Observer<PluginEvent> observer);
}
