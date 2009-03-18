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
    private final List<Plugin> mPlugins = new ArrayList<Plugin>();
    private ApplicationPlugin mApplicationPlugin;
    private final SpringLoader mSpringLoader;
    
    /**
     * Construct a PluginManager that will pass the SpringLoader
     * to loaded plugins
     * @param springLoader the SpringLoader
     */
    public DefaultPluginManager(final SpringLoader springLoader) {
        this.mSpringLoader = springLoader;
    }
    
    /**
     * Construct a PluginManager that will not pass any
     * SpringLoader to loaded plugins
     */
    public DefaultPluginManager() {
        this.mSpringLoader = null;
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
        return mApplicationPlugin;
    }

    /**
     * {@inheritDoc}
     */
    public List<Plugin> getPlugins() {
        return mPlugins;
    }

    /**
     * Load the plugins, given a resource path to the plugin
     * properties file.
     * @param propertiesResourcePath the path to the properties
     * file, e.g. META-INF/minimiser/plugin.properties
     * @throws PluginException on any load failures.
     */
    public void loadPlugins(final String propertiesResourcePath) throws PluginException {
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
            LOGGER.info("List of bindings: " + resolvedPlugins);
            if (resolvedPlugins.size() == 0) {
                final String warning = "No plugins have been found";
                LOGGER.warn(warning);
                throw new PluginException(warning);
            }
            for (PluginBinding<Plugin> binding : resolvedPlugins) {
                final Plugin plugin = binding.getPlugin();
                addLoadedPlugin(plugin);
                if (mSpringLoader != null) {
                    addPluginApplicationContextsToSpringLoader(plugin);
                }
            }
            // Now all app contexts have been loaded, let the
            // plugins have the SpringLoader
            for (PluginBinding<Plugin> binding : resolvedPlugins) {
                final Plugin plugin = binding.getPlugin();
                if (mSpringLoader != null) {
                    giveSpringLoaderToPlugin(plugin);
                }
            }
        } catch (final com.mycila.plugin.api.PluginException e) {
            final String warning = "Failure loading plugins: " + e.getMessage();
            LOGGER.warn(warning);
            LOGGER.debug(warning, e);
            throw new PluginException(warning); 
        }
        
        if (mApplicationPlugin == null) {
            final String warning = "No application plugin has been provided";
            LOGGER.warn(warning);
            throw new PluginException(warning);
        }
    }

    /**
     * Give the SpringLoader to a plugin.
     * <p>
     * Precondition: only called if weactually have a SpringLoader
     * @param plugin
     */
    private void giveSpringLoaderToPlugin(final Plugin plugin) {
        LOGGER.info("Giving SpringLoader to plugin " + plugin.getName());
        plugin.setSpringLoader(mSpringLoader);
    }

    /**
     * If the plugin defines any additional application context
     * files, add them to the SpringLoader.
     * <p>
     * Precondition: only called if we actually have a SpringLoader
     * @param plugin the plugin whose application contexts are
     * to be added.
     */
    private void addPluginApplicationContextsToSpringLoader(final Plugin plugin) throws PluginException {
        LOGGER.info("Requesting application contexts from plugin " + plugin.getName());
        final List<String> applicationContextResourcePaths = plugin.getApplicationContextResourcePaths();
        if (applicationContextResourcePaths == null || applicationContextResourcePaths.size() == 0) {
            LOGGER.info("No application contexts provided");
            return;
        }
        LOGGER.info("Plugin-defined application contexts:");
        for (String appContextResourcePath : applicationContextResourcePaths) {
            LOGGER.info("  " + appContextResourcePath);
        }
        try {
            mSpringLoader.addApplicationContext(applicationContextResourcePaths.toArray(new String[0]));
        } catch (final Exception e) {
            final String loadMessage = "Cannot add plugin application contexts: " + e.getMessage();
            LOGGER.warn(loadMessage);
            LOGGER.debug(loadMessage, e);
            throw new PluginException(loadMessage);
        }
    }

    private void addLoadedPlugin(final Plugin plugin) throws PluginException {
        LOGGER.info("Loaded plugin " + plugin.getName() + " version " + plugin.getVersion());
        LOGGER.debug("of class " + plugin.getClass().getName());
        if (plugin instanceof ApplicationPlugin) {
            if (mApplicationPlugin != null) {
                final String dupMessage = "Trying to load a second ApplicationPlugin ("
                    + plugin.getName() + " v" + plugin.getVersion() + ", class " + plugin.getClass().getName() + ") "
                    + "when there is already one loaded ("
                    + mApplicationPlugin.getName()
                    + " v" + mApplicationPlugin.getVersion() + ", class "
                    + mApplicationPlugin.getClass().getName() + ")";
                LOGGER.warn(dupMessage);
                throw new PluginException(dupMessage); 
            }
            LOGGER.info(plugin.getName() + " is the Application Plugin");
            mApplicationPlugin = (ApplicationPlugin) plugin;
        }
        mPlugins.add(plugin);
    }
}
