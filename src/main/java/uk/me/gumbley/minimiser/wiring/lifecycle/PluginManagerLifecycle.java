package uk.me.gumbley.minimiser.wiring.lifecycle;

import uk.me.gumbley.minimiser.gui.dialog.problem.ProblemReporter;
import uk.me.gumbley.minimiser.lifecycle.Lifecycle;
import uk.me.gumbley.minimiser.pluginmanager.PluginException;
import uk.me.gumbley.minimiser.pluginmanager.PluginManager;

/**
 * A Lifecycle that starts up, and shuts down the Plugin
 * Manager, and thereby, the plugins.
 * 
 * @author matt
 *
 */
public final class PluginManagerLifecycle implements Lifecycle {
    private final PluginManager mPluginManager;
    private final String mPluginResourcePath;
    private final ProblemReporter mProblemReporter;

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
