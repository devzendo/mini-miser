package uk.me.gumbley.minimiser.updatechecker;

import java.io.IOException;

import uk.me.gumbley.minimiser.gui.StatusBar;

/**
 * An UpdateProgressAdapter that informs the user of the progress
 * of the update availability check via the status bar.
 * 
 * @author matt
 *
 */
public final class DefaultUpdateProgressAdapter implements UpdateProgressAdapter {
    private final StatusBar mStatusBar;

    /**
     * Construct given the status bar
     * @param statusBar the status bar
     */
    public DefaultUpdateProgressAdapter(final StatusBar statusBar) {
        this.mStatusBar = statusBar;
    }
    
    /**
     * {@inheritDoc}
     */
    public void alreadyCheckedToday() {
        mStatusBar.displayTemporaryMessage("Already checked today", 5);
    }

    /**
     * {@inheritDoc}
     */
    public void checkStarted() {
        mStatusBar.displayTemporaryMessage("Checking for updates...", 10);
    }

    /**
     * {@inheritDoc}
     */
    public void noApplicationVersionDeclared() {
        mStatusBar.displayTemporaryMessage("Application does not have a version number", 10);
    }

    /**
     * {@inheritDoc}
     */
    public void noUpdateURLDeclared() {
        mStatusBar.displayTemporaryMessage("Application does not have an update site", 10);
    }

    /**
     * {@inheritDoc}
     */
    public void commsFailure(final IOException exception) {
        mStatusBar.displayTemporaryMessage("Update check failed: " + exception.getMessage(), 10);
    }

    /**
     * {@inheritDoc}
     */
    public void finished() {
    }

    /**
     * {@inheritDoc}
     */
    public void noUpdateAvailable() {
        mStatusBar.displayTemporaryMessage("No updates available", 10);
    }

    /**
     * {@inheritDoc}
     */
    public void transformFailure(final IOException exception) {
        mStatusBar.displayTemporaryMessage("Problem storing change log: " + exception.getMessage(), 10);
    }

    /**
     * {@inheritDoc}
     */
    public void transformFailure(final ParseException exception) {
        mStatusBar.displayTemporaryMessage("Problem processing change log: " + exception.getMessage(), 10);
    }

    /**
     * {@inheritDoc}
     */
    public void updateAvailable() {
        mStatusBar.displayTemporaryMessage("Updates available!", 10);
    }

    /**
     * {@inheritDoc}
     */
    public void updateCheckDisallowed() {
        mStatusBar.displayTemporaryMessage("Update checks disallowed", 10);
    }
}
