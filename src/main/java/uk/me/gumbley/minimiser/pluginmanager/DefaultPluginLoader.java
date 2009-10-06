package uk.me.gumbley.minimiser.pluginmanager;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import uk.me.gumbley.minimiser.plugin.Plugin;

/**
 * A PluginLoader that scans for named properties files, and
 * instantiates the Plugin classes described therein.
 * 
 * @author matt
 *
 */
public final class DefaultPluginLoader extends AbstractPluginLoader implements PluginLoader {
    private static final Logger LOGGER = Logger
            .getLogger(DefaultPluginLoader.class);
    /**
     * {@inheritDoc}
     */
    public List<Plugin> loadPluginsFromClasspath(final String propertiesResourcePath)
            throws PluginException {
        LOGGER.info("Loading plugins from properties at " + propertiesResourcePath);
        try {
            final Enumeration<URL> propertiesURLs = getPluginDescriptorURLs(propertiesResourcePath);
            final List<Plugin> plugins = new ArrayList<Plugin>();
            while (propertiesURLs.hasMoreElements()) {
                final URL propertiesURL = propertiesURLs.nextElement();
                final Properties properties = loadProperties(propertiesURL);
                plugins.addAll(loadPlugins(properties));
            }
            LOGGER.info("Returning " + plugins.size() + " plugin(s)");
            return plugins;
        } catch (final IOException e) {
            final String warning = "Failure loading plugins: " + e.getMessage();
            LOGGER.warn(warning);
            LOGGER.debug(warning, e);
            throw new PluginException(warning); 
        }
    }
    
    private List<Plugin> loadPlugins(final Properties properties) throws IOException {
        final List<Plugin> plugins = new ArrayList<Plugin>();
        final Set<Entry<Object, Object>> entrySet = properties.entrySet();
        for (final Entry<Object, Object> entry : entrySet) {
            // we can ignore the lhs
            final String pluginClassName = entry.getValue().toString();
            plugins.add(loadPlugin(pluginClassName));
        }
        return plugins;
    }

    @SuppressWarnings("unchecked")
    private Plugin loadPlugin(final String pluginClassName) throws IOException {
        LOGGER.debug("Loading plugin from class " + pluginClassName);
        try {
            final Class<Plugin> klass = (Class<Plugin>) Class.forName(pluginClassName);
            return klass.newInstance();
        } catch (final Exception e) {
            final String warning = "Cannot load class '" + pluginClassName + ": " + e.getMessage();
            LOGGER.warn(warning);
            throw new IOException(warning);
        }
    }

    private Properties loadProperties(final URL propertiesURL) throws IOException {
        LOGGER.debug("Loading properties file at " + propertiesURL.toString());
        InputStream is = null;
        try {
            is = new BufferedInputStream(propertiesURL.openStream());
            final Properties properties = new Properties();
            properties.load(is);
            return properties;
        } catch (final IOException e) {
            final String warning = "Cannot load plugin descriptor at URL"
                + propertiesURL.toString()
                + ": "
                + e.getMessage();
            LOGGER.warn(warning);
            LOGGER.debug(warning, e);
            throw new IOException(warning);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (final IOException e) {
                }
            }
        }
    }
}
