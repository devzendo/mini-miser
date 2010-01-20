package org.devzendo.minimiser.upgradedetector;

import java.util.List;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.springbeanlistloader.AbstractSpringBeanListLoaderImpl;
import org.devzendo.minimiser.springloader.SpringLoader;

import uk.me.gumbley.commoncode.patterns.observer.Observer;

/**
 * Manages a list of listeners of UpgradeEvents.
 * 
 * @author matt
 *
 */
public final class UpgradeEventListenerManager extends AbstractSpringBeanListLoaderImpl<Observer<UpgradeEvent>> {
    private static final Logger LOGGER = Logger
            .getLogger(UpgradeEventListenerManager.class);
    private final UpgradeDetector upgradeDetector;

    /**
     * @param springLoader the Spring loader
     * @param detector the Upgrade Detector
     * @param listenerBeanNames the list of upgrade event listener beans to manage.
     */
    public UpgradeEventListenerManager(final SpringLoader springLoader, final UpgradeDetector detector, final List<String> listenerBeanNames) {
        super(springLoader, listenerBeanNames);
        this.upgradeDetector = detector;
    }

    /**
     * Check the version stored, and the current version, and update any
     * listeners with an appropriate event.
     * @param runtimeVersion the currently running software version
     */
    public void checkForUpgrade(final String runtimeVersion) {
        boolean storeRuntimeVersion = false;
        LOGGER.info("Checking for fresh installation");
        if (upgradeDetector.freshInstall()) {
            LOGGER.info("Fresh installation detected");
            fireEvent(new FreshInstallEvent(runtimeVersion));
            storeRuntimeVersion = true;
        } else {
            LOGGER.info("Checking for upgrade to version " + runtimeVersion);
            if (upgradeDetector.upgraded(runtimeVersion)) {
                LOGGER.info("Upgrade detected");
                fireEvent(new SoftwareUpgradedEvent(runtimeVersion, upgradeDetector.getStoredVersion()));
                storeRuntimeVersion = true;
            }
        }
        if (storeRuntimeVersion) {
            upgradeDetector.storeCurrentVersion(runtimeVersion);
        }
    }

    private void fireEvent(final UpgradeEvent upgradeEvent) {
        for (Observer<UpgradeEvent> observer : getBeans()) {
            LOGGER.debug("Notifying '" + observer.getClass().getName() + "'");
            observer.eventOccurred(upgradeEvent);
        }
    }
}
