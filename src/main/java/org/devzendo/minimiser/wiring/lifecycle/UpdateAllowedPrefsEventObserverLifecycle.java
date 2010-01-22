package org.devzendo.minimiser.wiring.lifecycle;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.gui.menu.Menu;
import org.devzendo.minimiser.lifecycle.Lifecycle;
import org.devzendo.minimiser.prefs.CoreBooleanFlags;
import org.devzendo.minimiser.prefs.Prefs;
import org.devzendo.minimiser.prefs.PrefsEvent;
import org.devzendo.minimiser.updatechecker.UpdateChecker;
import org.devzendo.minimiser.updatechecker.UpdateProgressAdapterFactory;


/**
 * Adapts between the 'update allowed' prefs change events and the
 * triggering of update availability checks.
 * Also enables the relevant menu item. This latter functionality
 * should probably be broken out into another prefs event observer.
 * @author matt
 *
 */
public final class UpdateAllowedPrefsEventObserverLifecycle implements Lifecycle, Observer<PrefsEvent> {
    private static final Logger LOGGER = Logger
            .getLogger(UpdateAllowedPrefsEventObserverLifecycle.class);

    private final Prefs prefs;
    private final UpdateChecker updateChecker;
    private final UpdateProgressAdapterFactory updateProgressAdapterFactory;
    private final Menu menu;

    /**
     * Construct the adapter given other system objects for interaction.
     * @param leUpdateChecker the update checker
     * @param lePrefs the prefs
     * @param leAdapterFactory the update progress adapter factory
     * @param leMenu the menu
     */
    public UpdateAllowedPrefsEventObserverLifecycle(
            final UpdateChecker leUpdateChecker,
            final Prefs lePrefs,
            final UpdateProgressAdapterFactory leAdapterFactory,
            final Menu leMenu) {
        this.updateChecker = leUpdateChecker;
        this.prefs = lePrefs;
        this.updateProgressAdapterFactory = leAdapterFactory;
        this.menu = leMenu;
    }
    
    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final PrefsEvent observableEvent) {
        LOGGER.debug("Prefs event: " + observableEvent.getPrefsSection());
        if (observableEvent.getPrefsSection() != Prefs.PrefsSection.BOOLEAN_FLAGS) {
            return;
        }
        final boolean updatesAllowed = prefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED);
        menu.setHelpCheckForUpdatesEnabled(updatesAllowed);
        if (!updatesAllowed) {
            LOGGER.info("Prefs have changed, but update checks are disallowed");
            return;
        }
        LOGGER.info("Triggering update check");
        updateChecker.triggerUpdateCheck(updateProgressAdapterFactory.createVisibleUpdateProgressAdapter());
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
