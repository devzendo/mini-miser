package uk.me.gumbley.minimiser.pluginmanager;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import uk.me.gumbley.minimiser.springloader.SpringLoader;

import com.mycila.plugin.api.PluginBinding;

/**
 * The Default Plugin manager obtains plugin customisation data
 * by scanning the classpath for plugin descriptions.
 * 
 * @author matt
 *
 */
public final class DefaultPluginManager implements PluginManager {
    private static final Logger LOGGER = Logger
            .getLogger(DefaultPluginManager.class);
    private final PluginInitialiser mPluginInitialiser;
    
    /**
     * Construct a PluginManager that will pass the SpringLoader
     * to loaded plugins
     * @param springLoader the SpringLoader
     * @param appDetails the bean for storing application details
     */
    public DefaultPluginManager(final SpringLoader springLoader,
            final AppDetails appDetails) {
        mPluginInitialiser = new PluginInitialiser(springLoader, appDetails);
    }
    
    /**
     * Construct a PluginManager that will not pass any
     * SpringLoader to loaded plugins - only used by tests.
     * @param appDetails the bean for storing application details
     */
    public DefaultPluginManager(final AppDetails appDetails) {
        mPluginInitialiser = new PluginInitialiser(null, appDetails);
    }

    /**
     * {@inheritDoc}
     */
    public String getUpdateSiteBaseURL() {
        // TODO Auto-generated method stub
        return "http://localhost:9876";
    }

    /**
     * {@inheritDoc}
     */
    public ApplicationPlugin getApplicationPlugin() {
        return mPluginInitialiser.getApplicationPlugin();
    }

    /**
     * {@inheritDoc}
     */
    public List<Plugin> getPlugins() {
        return mPluginInitialiser.getPlugins();
    }

    /**
     * Load the plugins, given a resource path to the plugin
     * properties file.
     * @param propertiesResourcePath the path to the properties
     * file, e.g. META-INF/minimiser/plugin.properties
     * @throws PluginException on any load failures.
     */
    public void loadPlugins(final String propertiesResourcePath) throws PluginException {
       
        final List<Plugin> loadedPlugins = loadPluginsFromClasspath(propertiesResourcePath);
        mPluginInitialiser.initialisePlugins(loadedPlugins);

        // The PluginManager enforces the requirement that there
        // must be an application plugin defined. Unit tests
        // that use the PluginInitialiser in their base class are
        // relaxed about this - you may want to test your non-
        // app plugin.
        if (mPluginInitialiser.getApplicationPlugin() == null) {
            final String warning = "No application plugin has been provided";
            LOGGER.warn(warning);
            throw new PluginException(warning);
        }
    }
    
    
    private List<Plugin> loadPluginsFromClasspath(final String propertiesResourcePath) throws PluginException {
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
