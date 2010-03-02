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

import org.devzendo.minimiser.lifecycle.Lifecycle;
import org.devzendo.minimiser.pluginmanager.PluginRegistry;
import org.devzendo.minimiser.upgradedetector.UpgradeEventListenerManager;

/**
 * A Lifecycle that wires and unwires the UpgradeEventListenerManager on
 * startup.
 * 
 * @author matt
 *
 */
public final class UpgradeEventListenerLifecycle implements Lifecycle {
    
    private final UpgradeEventListenerManager mUpgradeEventListenerManager;
    private final PluginRegistry mPluginRegistry;

    /**
     * Create the UpgradeEventListenerLifecycle, given the
     * UpgradeEventListenerManager to process the upgrade detection.
     * 
     * @param manager the UpgradeEventListenerManager
     * @param pluginRegistry the plugin registry
     */
    public UpgradeEventListenerLifecycle(
            final UpgradeEventListenerManager manager,
            final PluginRegistry pluginRegistry) {
        mUpgradeEventListenerManager = manager;
        mPluginRegistry = pluginRegistry;
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
        mUpgradeEventListenerManager.checkForUpgrade(mPluginRegistry.getApplicationVersion());
    }
}
