package uk.me.gumbley.minimiser.updatechecker;


/**
 * An UpdateChecker that retrieves test data.
 * 
 * @author matt
 *
 */
public final class StubUpdateChecker implements UpdateChecker {
    private boolean updateTriggered = false;
    /**
     * For tests, was an update check triggered?
     * @return true iff triggered
     */
    public boolean updateTriggered() {
        return updateTriggered;
    }

    /**
     * {@inheritDoc}
     */
    public void triggerUpdateCheck(final UpdateProgressAdapter progressAdapter) {
        updateTriggered = true;
    }
}
