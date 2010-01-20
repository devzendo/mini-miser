package org.devzendo.minimiser.gui.menu.actionlisteners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.prefs.CoreBooleanFlags;
import org.devzendo.minimiser.prefs.Prefs;
import org.devzendo.minimiser.updatechecker.UpdateChecker;
import org.devzendo.minimiser.updatechecker.UpdateProgressAdapterFactory;


/**
 * Triggers a check to see if any updates are available, with
 * feedback to the user.
 * 
 * @author matt
 *
 */
public final class HelpCheckForUpdatesActionListener implements ActionListener {
    private static final Logger LOGGER = Logger
            .getLogger(HelpCheckForUpdatesActionListener.class);
    private final Prefs prefs;
    private final UpdateChecker updateChecker;
    private final UpdateProgressAdapterFactory updateProgressAdapterFactory;

    /**
     * @param leUpdateChecker the update checker
     * @param lePrefs the prefs
     * @param leAdapterFactory the update progress adapter factory
     */
    public HelpCheckForUpdatesActionListener(
            final UpdateChecker leUpdateChecker,
            final Prefs lePrefs,
            final UpdateProgressAdapterFactory leAdapterFactory) {
        this.updateChecker = leUpdateChecker;
        this.prefs = lePrefs;
        this.updateProgressAdapterFactory = leAdapterFactory;
    }

    /**
     * @param e an event
     */
    public void actionPerformed(final ActionEvent e) {
        if (!prefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED)) {
            // This shouldn't happen, since the menu should be disabled
            LOGGER.error("Manual update availability check requested, but update checks are disallowed");
            return;
        }
        LOGGER.info("Requesting update check");
        updateChecker.triggerUpdateCheck(updateProgressAdapterFactory.createVisibleUpdateProgressAdapter());
    }
}
