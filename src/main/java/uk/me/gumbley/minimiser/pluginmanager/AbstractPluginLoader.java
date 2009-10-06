package uk.me.gumbley.minimiser.pluginmanager;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import org.apache.log4j.Logger;

/**
 * Implements functionality common to PluginLoaders.
 * @author matt
 *
 */
public abstract class AbstractPluginLoader implements PluginLoader {
    private static final Logger LOGGER = Logger
            .getLogger(AbstractPluginLoader.class);

    /**
     * Given a resource path, return all URLs pointing to this on
     * the current classpath
     * @param resourcePath the resource path
     * @return an Enumeration<URL> of instances
     * @throws IOException on classpath scanning failure
     */
    protected Enumeration<URL> getPluginDescriptorURLs(final String resourcePath) throws IOException {
        return Thread.currentThread().getContextClassLoader().getResources(resourcePath);
    }

    /**
     * {@inheritDoc}
     */
    public final void displayPluginDescriptorResources(final String propertiesResourcePath) {
        LOGGER.debug("List of plugin descriptor resources: " + propertiesResourcePath);
        try {
            final Enumeration<URL> resources =
                getPluginDescriptorURLs(propertiesResourcePath);
            while (resources.hasMoreElements()) {
                final URL resource = resources.nextElement();
                LOGGER.debug("Plugin descriptor resource is " + resource);
            }
            LOGGER.debug("End of plugin descriptor list");
        } catch (final IOException e) {
            LOGGER.warn("Could not obtain list of plugin descriptor resources for " + propertiesResourcePath, e);
        }
    }
}
