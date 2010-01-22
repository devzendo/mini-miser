package org.devzendo.minimiser.upgradedetector;

import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.prefs.Prefs;
import org.devzendo.minimiser.prefs.PrefsInitialiser;


/**
 * Performs initialisation of prefs, upon detection of a fresh installation.
 * 
 * @author matt
 *
 */
public final class PrefsInitialisingFreshInstallListener implements Observer<UpgradeEvent> {

    private final Prefs prefs;
    
    /**
     * Store the prefs for use if a fresh installation is detected.
     * @param preferences the prefs
     */
    public PrefsInitialisingFreshInstallListener(final Prefs preferences) {
        this.prefs = preferences;
    }

    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final UpgradeEvent observableEvent) {
        if (observableEvent instanceof FreshInstallEvent) {
            PrefsInitialiser.initialisePrefs(prefs);
        }
    }
}
