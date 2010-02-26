package org.devzendo.minimiser.upgradedetector;

import org.devzendo.commoncode.patterns.observer.Observer;

/**
 * Allows tests to check UpgradeEvents
 * 
 * @author matt
 *
 */
public final class StubRecordingUpgradeListener implements Observer<UpgradeEvent> {

    private UpgradeEvent observedEvent = null;

    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final UpgradeEvent observableEvent) {
        this.observedEvent = observableEvent;
    }

    /**
     * @return the observed event, or null if nothing was observed
     */
    public UpgradeEvent getObservedEvent() {
        return observedEvent;
    }
}