package uk.me.gumbley.minimiser.updatechecker;

/**
 * The UpdateChcker checks for updates to the software, asynchronously when
 * triggered.
 * 
 * @author matt
 *
 */
public interface UpdateChecker {

    /**
     * Trigger an update check, providing feedback using the progress adapter
     * @param progressAdapter the UpdateProgressAdapter to use for feedback
     */
    void triggerUpdateCheck(UpdateProgressAdapter progressAdapter);
}
