package org.devzendo.minimiser.upgradedetector;

import org.devzendo.commoncode.patterns.observer.ObservableEvent;

/**
 * A version change has been detected, either due to a fresh installation or
 * proper upgrade, this is the notification
 * you receive from the UpgradeEventListenerManager, if you're an observer.
 * 
 * @author matt
 *
 */
public abstract class UpgradeEvent implements ObservableEvent {
    private final String runningVersion;

    /**
     * Create an UpgradeEvent
     * @param runningVer the currently running verson of the software
     */
    public UpgradeEvent(final String runningVer) {
        this.runningVersion = runningVer;
    }

    /**
     * Get the version of the running software
     * @return the running software version number
     */
    public final String getRunningVersion() {
        return runningVersion;
    }
}
