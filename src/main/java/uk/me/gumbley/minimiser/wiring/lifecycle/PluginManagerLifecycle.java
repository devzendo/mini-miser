package uk.me.gumbley.minimiser.wiring.lifecycle;

import uk.me.gumbley.minimiser.gui.dialog.problem.ProblemReporter;
import uk.me.gumbley.minimiser.lifecycle.Lifecycle;
import uk.me.gumbley.minimiser.pluginmanager.PluginException;
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
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        try {
            mPluginManager.loadPlugins(mPluginResourcePath);
        } catch (final PluginException e) {
            mProblemReporter.reportProblem("loading plugins", e);
        }
    }
}
