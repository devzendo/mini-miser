package uk.me.gumbley.minimiser.pluginmanager;

import java.util.List;

import uk.me.gumbley.minimiser.plugin.Plugin;

/**
 * Given a resource path to a properties file, find all
 * occurrences of these on the classpath, then use them to load
 * a list of Plugin classes.
 *  
 * @author matt
 *
 */
public interface PluginLoader {

    /**
     * Discover all occurrences of the named properties file from
     * the classpath, and log their actual paths. 
     * 
     * @param propertiesResourcePath a resource path to a
     * properties file, e.g. META-INF/minimiser/plugin.properties
     */
    void displayPluginDescriptorResources(final String propertiesResourcePath);

    /**
     * Load all occurrences of the named properties files from
     * the classpath, and then dynamically load each class
     * referred to therein.
     *  
     * @param propertiesResourcePath a resource path to a
     * properties file, e.g. META-INF/minimiser/plugin.properties
     * @return a list of Plugins.
     * @throws PluginException upon plugin load failure
     */
    List<Plugin> loadPluginsFromClasspath(final String propertiesResourcePath) throws PluginException;
}
