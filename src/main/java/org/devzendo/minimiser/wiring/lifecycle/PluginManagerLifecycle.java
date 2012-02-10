/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devzendo.minimiser.wiring.lifecycle;

import org.apache.log4j.Logger;
import org.devzendo.commonapp.lifecycle.Lifecycle;
import org.devzendo.minimiser.gui.dialog.problem.ProblemReporter;
import org.devzendo.minimiser.plugin.Plugin;
import org.devzendo.minimiser.pluginmanager.PluginManager;


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
        for (final Plugin plugin : mPluginManager.getPlugins()) {
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
