package uk.me.gumbley.minimiser.pluginmanager;

import java.util.ArrayList;
import java.util.List;

import uk.me.gumbley.minimiser.springloader.SpringLoader;

/**
 * A Plugin Manager for tests that returns a single App.
 * 
 * @author matt
 *
 */
public final class DummyAppPluginManager implements PluginManager {

    /**
     * {@inheritDoc}
     */
    public ApplicationPlugin getApplicationPlugin() {
        return new ApplicationPlugin() {

            public List<String> getApplicationContextResourcePaths() {
                return null;
            }

            public String getName() {
                return "Dummy App";
            }

            public SpringLoader getSpringLoader() {
                return null;
            }

            public String getUpdateSiteBaseURL() {
                return null;
            }

            public String getVersion() {
                return "1.0.0";
            }

            public void setSpringLoader(
                    final SpringLoader springLoader) {
            }

            public void shutdown() {
            }

            public List<String> getAfter() {
                return null;
            }

            public List<String> getBefore() {
                return null;
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    public List<Plugin> getPlugins() {
        final ArrayList<Plugin> pluginList = new ArrayList<Plugin>();
        final Plugin dummyPlugin = new Plugin() {

            public List<String> getApplicationContextResourcePaths() {
                return null;
            }

            public String getName() {
                return "Dummy Plugin";
            }

            public SpringLoader getSpringLoader() {
                return null;
            }

            public String getUpdateSiteBaseURL() {
                return null;
            }

            public String getVersion() {
                return "3.2.1";
            }

            public void setSpringLoader(final SpringLoader springLoader) {
            }

            public void shutdown() {
            }

            public List<String> getAfter() {
                return null;
            }

            public List<String> getBefore() {
                return null;
            }
        };
        pluginList.add(dummyPlugin);
        return pluginList;
    }

    /**
     * {@inheritDoc}
     */
    public String getUpdateSiteBaseURL() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void loadPlugins(final String propertiesResourcePath)
            throws PluginException {
    }
}
