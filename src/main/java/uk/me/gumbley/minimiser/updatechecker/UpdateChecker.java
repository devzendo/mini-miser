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
     * The name of the remote file that contains just the
     * latest version number.
     */
    String VERSION_NUMBER_FILE = "version.txt";
    
    /**
     * The name of the remote file that contains the change log.
     */
    String CHANGE_LOG_FILE = "changelog.txt";

    /**
     * Trigger an update check, providing feedback using the progress adapter
     * @param progressAdapter the UpdateProgressAdapter to use for feedback
     */
    void triggerUpdateCheck(UpdateProgressAdapter progressAdapter);
}
