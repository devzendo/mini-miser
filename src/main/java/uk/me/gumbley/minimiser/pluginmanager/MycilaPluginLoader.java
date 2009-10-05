package uk.me.gumbley.minimiser.pluginmanager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;

import uk.me.gumbley.minimiser.plugin.Plugin;

import com.mycila.plugin.api.PluginBinding;

/**
 * A PluginLoader that uses Mathieu Carbou's mycila plugin loader.
 * Currently only supports Java plugins, and forces our Plugin
 * interface to extend his (which gives us plugin ordering).
 * 
 * @author matt
 *
 */
public final class MycilaPluginLoader implements PluginLoader {
    private static final Logger LOGGER = Logger
            .getLogger(MycilaPluginLoader.class);
    
    /**
     * {@inheritDoc}
     */
    public void displayPluginDescriptorResources(final String propertiesResourcePath) {
        LOGGER.debug("List of plugin descriptor resources: " + propertiesResourcePath);
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            final Enumeration<URL> resources =
                contextClassLoader.getResources(propertiesResourcePath);
            while (resources.hasMoreElements()) {
                final URL resource = resources.nextElement();
                LOGGER.debug("Plugin descriptor resource is " + resource);
            }
            LOGGER.debug("End of plugin descriptor list");
        } catch (final IOException e) {
            LOGGER.warn("Could not obtain list of plugin descriptor resources for " + propertiesResourcePath, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<Plugin> loadPluginsFromClasspath(final String propertiesResourcePath)
            throws PluginException {
        LOGGER.info("Loading plugins from the " + propertiesResourcePath + " resources");
        // mycila doesn't care if the properties can't be found,
        // and will load zero plugins - as it should, presumably.
        // However, this indicates a configuration error in our
        // case.
        try {
            final com.mycila.plugin.spi.PluginManager<Plugin> manager =
                new com.mycila.plugin.spi.PluginManager<Plugin>(Plugin.class,
                        propertiesResourcePath);
            LOGGER.info("Loading plugins");
            final List<PluginBinding<Plugin>> resolvedPlugins = manager.getResolver().getResolvedPlugins();
            final List<Plugin> loadedPlugins = new ArrayList<Plugin>(resolvedPlugins.size());
            for (PluginBinding<Plugin> binding : resolvedPlugins) {
                final Plugin plugin = binding.getPlugin();
                loadedPlugins.add(plugin);
            }
            return loadedPlugins;
        } catch (final com.mycila.plugin.api.PluginException e) {
            final String warning = "Failure loading plugins: " + e.getMessage();
            LOGGER.warn(warning);
            LOGGER.debug(warning, e);
            throw new PluginException(warning); 
        }
    }
}
