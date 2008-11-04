package uk.me.gumbley.minimiser.wiring.lifecycle;

import uk.me.gumbley.minimiser.lifecycle.Lifecycle;
import uk.me.gumbley.minimiser.upgradedetector.UpgradeEventListenerManager;
import uk.me.gumbley.minimiser.version.AppVersion;

/**
 * A Lifecycle that wires and unwires the UpgradeEventListenerManager on
 * startup.
 * 
 * @author matt
 *
 */
public final class UpgradeEventListenerLifecycle implements Lifecycle {
    
    private final UpgradeEventListenerManager upgradeEventListenerManager;

    /**
     * Create the UpgradeEventListenerLifecycle, given the
     * UpgradeEventListenerManager to process the upgrade detection.
     * 
     * @param manager the UpgradeEventListenerManager
     */
    public UpgradeEventListenerLifecycle(final UpgradeEventListenerManager manager) {
        this.upgradeEventListenerManager = manager;
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
        upgradeEventListenerManager.checkForUpgrade(AppVersion.getVersion());
    }
}
