package uk.me.gumbley.minimiser.upgradedetector;

import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.prefs.PrefsInitialiser;

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
