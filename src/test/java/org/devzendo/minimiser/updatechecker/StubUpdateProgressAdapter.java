package org.devzendo.minimiser.updatechecker;

import java.io.IOException;

/**
 * @author matt
 *
 */
public final class StubUpdateProgressAdapter implements UpdateProgressAdapter {

    /**
     * {@inheritDoc}
     */
    public void updateCheckDisallowed() {
    }

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
    public void noUpdateAvailable() {
    }

    /**
     * {@inheritDoc}
     */
    public void updateAvailable() {
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
    public void finished() {
    }
}
