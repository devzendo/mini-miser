package uk.me.gumbley.minimiser.wiring.lifecycle;

import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.lifecycle.Lifecycle;
import uk.me.gumbley.minimiser.prefs.CoreBooleanFlags;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.prefs.PrefsEvent;
import uk.me.gumbley.minimiser.updatechecker.UpdateChecker;
import uk.me.gumbley.minimiser.updatechecker.UpdateProgressAdapterFactory;

/**
 * Adapts between the 'update allowed' prefs change events and the triggering
 * of update availability checks.
 * @author matt
 *
 */
public final class UpdateAllowedPrefsEventObserverLifecycle implements Lifecycle, Observer<PrefsEvent> {
    private static final Logger LOGGER = Logger
            .getLogger(UpdateAllowedPrefsEventObserverLifecycle.class);

    private final Prefs prefs;
    private final UpdateChecker updateChecker;
    private final UpdateProgressAdapterFactory updateProgressAdapterFactory;

    /**
     * Construct the adapter given other system objects for interaction.
     * @param leUpdateChecker the update checker
     * @param lePrefs the prefs
     * @param leAdapterFactory the update progress adapter factory
     */
    public UpdateAllowedPrefsEventObserverLifecycle(
            final UpdateChecker leUpdateChecker,
            final Prefs lePrefs,
            final UpdateProgressAdapterFactory leAdapterFactory) {
        this.updateChecker = leUpdateChecker;
        this.prefs = lePrefs;
        this.updateProgressAdapterFactory = leAdapterFactory;
    }
    
    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final PrefsEvent observableEvent) {
        LOGGER.debug("Prefs event: " + observableEvent.getPrefsSection());
        if (observableEvent.getPrefsSection() != Prefs.PrefsSection.BOOLEAN_FLAGS) {
            return;
        }
        if (!prefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED)) {
            LOGGER.info("Prefs have changed, but update checks are disallowed");
            return;
        }
        LOGGER.info("Triggering update check");
        updateChecker.triggerUpdateCheck(updateProgressAdapterFactory.createUpdateProgressAdapter());
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        prefs.addChangeListener(this);
    }
}
