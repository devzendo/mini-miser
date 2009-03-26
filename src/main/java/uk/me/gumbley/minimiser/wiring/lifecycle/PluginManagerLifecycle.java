package uk.me.gumbley.minimiser.wiring.lifecycle;

import org.apache.log4j.Logger;

import uk.me.gumbley.minimiser.gui.dialog.problem.ProblemReporter;
import uk.me.gumbley.minimiser.lifecycle.Lifecycle;
import uk.me.gumbley.minimiser.pluginmanager.Plugin;
import uk.me.gumbley.minimiser.pluginmanager.PluginManager;

/**
 * A Lifecycle that starts up, and shuts down the Plugin
 * Manager, and thereby, the plugins.
 * <p>
 * Reports any problems with plugin initialisation via the
 * Problem Reporter.
 * @author matt
 *
 */
public final class PluginManagerLifecycle implements Lifecycle {
    private static final Logger LOGGER = Logger
            .getLogger(PluginManagerLifecycle.class);
    private final PluginManager mPluginManager;
    private final String mPluginResourcePath;
    private final ProblemReporter mProblemReporter;

    /**
     * Start the lifecycle
     * @param pluginManager the plugin manager
     * @param pluginResourcePath the path into the resources
     * where we'll find plugin descriptors
     * @param problemReporter the problem reporter with which to
     * report any initialisation problems
     */
    public PluginManagerLifecycle(final PluginManager pluginManager,
            final String pluginResourcePath,
            final ProblemReporter problemReporter) {
        mPluginManager = pluginManager;
        mPluginResourcePath = pluginResourcePath;
        mProblemReporter = problemReporter;
    }
    
    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        LOGGER.info("Shutting down plugins");
        for (Plugin plugin : mPluginManager.getPlugins()) {
            try {
                plugin.shutdown();
                // This throws PluginException, but plugins
                // might be developed by non-TDDers, and might
                // throw any old rubbish, so let's not take
                // any chances... and catch all.
            } catch (final Exception e) {
                mProblemReporter.reportProblem("shutting down plugins", e);
            }
        }
        LOGGER.info("Plugins shut down");
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        LOGGER.info("Starting plugins");
        try {
            mPluginManager.loadPlugins(mPluginResourcePath);
            // This throws PluginException, but plugins
            // might be developed by non-TDDers, and might
            // throw any old rubbish, so let's not take
            // any chances... and catch all.
        } catch (final Exception e) {
            mProblemReporter.reportProblem("loading plugins", e);
        }
        LOGGER.info("Plugins started");
    }
}
