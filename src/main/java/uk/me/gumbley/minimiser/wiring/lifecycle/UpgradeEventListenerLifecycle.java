package uk.me.gumbley.minimiser.wiring.lifecycle;

import uk.me.gumbley.minimiser.lifecycle.Lifecycle;
import uk.me.gumbley.minimiser.pluginmanager.PluginRegistry;
import uk.me.gumbley.minimiser.upgradedetector.UpgradeEventListenerManager;

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
