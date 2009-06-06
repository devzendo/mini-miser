package uk.me.gumbley.minimiser.pluginmanager;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import uk.me.gumbley.minimiser.springloader.SpringLoader;

/**
 * A helper class that initialises the plugins that have already
 * been loaded by some external mechanism - either using mycila,
 * or via annotations in the plugin unit test case.
 * 
 * @author matt
 * 
 */
public final class PluginInitialiser {
    private static final Logger LOGGER = Logger
            .getLogger(PluginInitialiser.class);

    private final List<Plugin> mPlugins = new ArrayList<Plugin>();
    private ApplicationPlugin mApplicationPlugin;
    private final SpringLoader mSpringLoader;
    private final PluginRegistry mPluginRegistry;

    /**
     * Construct a PluginInitialiser that will pass the
     * SpringLoader to loaded plugins
     * 
     * @param springLoader the SpringLoader
     * @param pluginRegistry the plugin registry 
     */
    public PluginInitialiser(final SpringLoader springLoader,
            final PluginRegistry pluginRegistry) {
        mSpringLoader = springLoader;
        mPluginRegistry = pluginRegistry;
    }
    

    /**
     * Initialise a set of loaded plugins
     * @param loadedPlugins the plugins to initialise
     * @throws PluginException on initialisation failure
     */
    public void initialisePlugins(final List<Plugin> loadedPlugins) throws PluginException {
        // TODO improve this diag
        LOGGER.info("List of plugins: " + loadedPlugins);
        if (loadedPlugins.size() == 0) {
            final String warning = "No plugins have been found";
            LOGGER.warn(warning);
            throw new PluginException(warning);
        }
        for (Plugin plugin : loadedPlugins) {
            addLoadedPlugin(plugin);
            if (mSpringLoader != null) {
                addPluginApplicationContextsToSpringLoader(plugin);
            }
        }
        // Now all app contexts have been loaded, let the
        // plugins have the SpringLoader
        if (mSpringLoader != null) {
            for (Plugin plugin : loadedPlugins) {
                giveSpringLoaderToPlugin(plugin);
            }
        }

        if (mApplicationPlugin == null) {
            final String warning = "No application plugin has been provided";
            LOGGER.warn(warning);
            throw new PluginException(warning);
        }

        // Store the plugins' details in the
        // PluginRegistry, from where the rest of the system
        // can obtain it.
        for (Plugin plugin : mPlugins) {
            final PluginDescriptor pluginDescriptor = getDescriptorFromPlugin(plugin);
            mPluginRegistry.addPluginDescriptor(pluginDescriptor);
        }
    }

    private PluginDescriptor getDescriptorFromPlugin(final Plugin plugin) {
        return new PluginDescriptor(
            plugin instanceof ApplicationPlugin, 
            plugin.getName(),
            plugin.getVersion(),
            "1", /*plugin.getSchemaVersion(),*/ // TODO plugins need a schema version
            plugin.getUpdateSiteBaseURL(),
            "bob@aol.com"); //plugin.getDevelopersEmailAddress(), // TODO
    }

    /**
     * @return the loaded, initialised plugins, if
     * initialisePlugins succeeded without Exception
     */
    public List<Plugin> getPlugins() {
        return mPlugins;
    }


    /**
     * @return the application plugin, if
     * initialisePlugins succeeded without Exception
     */
    public ApplicationPlugin getApplicationPlugin() {
        return mApplicationPlugin;
    }


    /**
     * Give the SpringLoader to a plugin.
     * <p>
     * Precondition: only called if we actually have a SpringLoader
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
