package uk.me.gumbley.minimiser.wiring.lifecycle;

import uk.me.gumbley.minimiser.lifecycle.Lifecycle;
import uk.me.gumbley.minimiser.pluginmanager.AppDetails;
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
    private final AppDetails mAppDetails;

    /**
     * Create the UpgradeEventListenerLifecycle, given the
     * UpgradeEventListenerManager to process the upgrade detection.
     * 
     * @param manager the UpgradeEventListenerManager
     * @param appDetails the application details
     */
    public UpgradeEventListenerLifecycle(
            final UpgradeEventListenerManager manager,
            final AppDetails appDetails) {
        mUpgradeEventListenerManager = manager;
        mAppDetails = appDetails;
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
        mUpgradeEventListenerManager.checkForUpgrade(mAppDetails.getApplicationVersion());
    }
}
