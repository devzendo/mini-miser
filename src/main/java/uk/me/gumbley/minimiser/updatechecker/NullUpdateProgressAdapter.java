package uk.me.gumbley.minimiser.updatechecker;

import java.io.IOException;

/**
 * An UpdateProgressAdapter with no feedback at all.
 * @author matt
 *
 */
public final class NullUpdateProgressAdapter implements UpdateProgressAdapter {
    /**
     * {@inheritDoc}
     */
    public void alreadyCheckedToday() {
    }

    /**
     * {@inheritDoc}
     */
    public void checkStarted() {
    }

    /**
     * {@inheritDoc}
     */
    public void noApplicationVersionDeclared() {
    }
    
    /**
     * {@inheritDoc}
     */
    public void noUpdateURLDeclared() {
    }

    /**
     * {@inheritDoc}
     */
    public void commsFailure(final IOException exception) {
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
    }

    /**
     * {@inheritDoc}
     */
    public void transformFailure(final IOException exception) {
    }

    /**
     * {@inheritDoc}
     */
    public void transformFailure(final ParseException exception) {
    }

    /**
     * {@inheritDoc}
     */
    public void updateAvailable() {
    }

    /**
     * {@inheritDoc}
     */
    public void updateCheckDisallowed() {
    }
}
