package uk.me.gumbley.minimiser.pluginmanager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;

import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.commoncode.patterns.observer.ObserverList;
import uk.me.gumbley.minimiser.springloader.SpringLoader;

import com.mycila.plugin.api.PluginBinding;

/**
 * The Default Plugin manager obtains plugin customisation data
 * by scanning the classpath for plugin descriptions. It loads the
 * plugins found there, and passes them on to the PluginInitialiser
 * for initialisation, from where they are published (as
 * PluginDescriptors) in the PluginRegistry.
 * 
 * 
 * @author matt
 *
 */
/**
 * @author matt
 *
 */
public final class DefaultPluginManager implements PluginManager {
    private static final Logger LOGGER = Logger
            .getLogger(DefaultPluginManager.class);
    private final PluginInitialiser mPluginInitialiser;
    private final ObserverList<PluginEvent> mObserverList;
    private final PluginRegistry mPluginRegistry;

    
    /**
     * Construct a PluginManager that will pass the SpringLoader
     * to loaded plugins
     * @param springLoader the SpringLoader
     * @param pluginRegistry the PluginRegistry for storing plugin details
     */
    public DefaultPluginManager(final SpringLoader springLoader,
            final PluginRegistry pluginRegistry) {
        mPluginRegistry = pluginRegistry;
        mObserverList = new ObserverList<PluginEvent>();
        mPluginInitialiser = new PluginInitialiser(springLoader, pluginRegistry);
    }
    
    /**
     * Construct a PluginManager that will not pass any
     * SpringLoader to loaded plugins - only used by tests.
     * @param pluginRegistry the PluginRegistry for storing plugin details
     */
    public DefaultPluginManager(final PluginRegistry pluginRegistry) {
        mPluginRegistry = pluginRegistry;
        mObserverList = new ObserverList<PluginEvent>();
        mPluginInitialiser = new PluginInitialiser(null, pluginRegistry);
    }

    /**
     * {@inheritDoc}
     * @deprecated
     */
    @Deprecated
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
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <F> List<F> getPluginsImplementingFacade(final Class<F> facadeType) {
        LOGGER.debug("Obtaining implementors of " + facadeType.getSimpleName());
        final ArrayList<F> facades = new ArrayList<F>();
        final List<Plugin> plugins = getPlugins();
        for (final Plugin plugin : plugins) {
            if (facadeType.isAssignableFrom(plugin.getClass())) {
                facades.add((F) plugin);
                LOGGER.debug("  Returning " + plugin.getClass().getSimpleName());
            }
        }
        return facades;
    }

    /**
     * Load the plugins, given a resource path to the plugin
     * properties file.
     * @param propertiesResourcePath the path to the properties
     * file, e.g. META-INF/minimiser/plugin.properties
     * @throws PluginException on any load failures.
     */
    public void loadPlugins(final String propertiesResourcePath) throws PluginException {
        LOGGER.info("Loading plugins from " + propertiesResourcePath);
        final List<Plugin> loadedPlugins = loadPluginsFromClasspath(propertiesResourcePath);
        mPluginInitialiser.initialisePlugins(loadedPlugins);

        // The PluginManager enforces the requirement that there
        // must be an application plugin defined. Unit tests
        // that use the PluginInitialiser in their base class are
        // relaxed about this - you may want to test your non-
        // app plugin.
        final ApplicationPlugin applicationPlugin = mPluginInitialiser.getApplicationPlugin();
        if (applicationPlugin == null) {
            final String warning = "No application plugin has been provided";
            LOGGER.warn(warning);
            throw new PluginException(warning);
        }
        
        final PluginDescriptor applicationPluginDescriptor = mPluginRegistry.getApplicationPluginDescriptor();
        LOGGER.info("Notifying observers of application " + applicationPluginDescriptor.getName() + " details");
        final ApplicationPluginLoadedEvent appLoadedEvent = new ApplicationPluginLoadedEvent(applicationPluginDescriptor);
        LOGGER.debug("notification event: " + appLoadedEvent);
        mObserverList.eventOccurred(appLoadedEvent);
    }
    
    private List<Plugin> loadPluginsFromClasspath(final String propertiesResourcePath) throws PluginException {
        LOGGER.info("Loading plugins from the " + propertiesResourcePath + " resources");
        // mycila doesn't care if the properties can't be found,
        // and will load zero plugins - as it should, presumably.
        // However, this indicates a configuration error in our
        // case.
        displayPluginDescriptorResources(propertiesResourcePath);
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

    private void displayPluginDescriptorResources(final String propertiesResourcePath) {
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
            LOGGER.warn("Could not obtain list of plugin deecriptor resources for " + propertiesResourcePath, e);
        }
    }

    /**
     * Add an observer of plugin events.
     * @param observer the observer to add.
     */
    public void addPluginEventObserver(final Observer<PluginEvent> observer) {
        LOGGER.debug("Adding observer " + observer.getClass().getSimpleName());
        mObserverList.addObserver(observer);
    }

    /**
     * Remove an observer of plugin events.
     * @param observer the observer to remove.
     */
    public void removePluginEventObserver(final Observer<PluginEvent> observer) {
        LOGGER.debug("Removing observer " + observer.getClass().getSimpleName());
        mObserverList.removeListener(observer);
    }
}
