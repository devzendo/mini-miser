package uk.me.gumbley.minimiser.updatechecker;

import java.io.IOException;

/**
 * Provides feedback to the user on the progress of the update check.
 * @author matt
 *
 */
public interface UpdateProgressAdapter {
    /**
     * Called if the check for updates is disallowed via prefs. No more
     * methods will be called on the adapter after this. This callback
     * indicates an error since update checks should never be triggered
     * if disallowed.
     */
    void updateCheckDisallowed();

    /**
     * Called if a check has already been made today. No more methods
     * will be called on the adapter after this. This is not an error.
     */
    void alreadyCheckedToday();

    /**
     * Called when the conditions for an update check to proceed have been met.
     * You will have subsequent calls after receiving this.
     */
    void checkStarted();

    /**
     * A communications exception has occurred attempting to retrieve remote
     * data. There will be no more method calls after receiving this.
     * @param exception details of the exception
     */
    void commsFailure(IOException exception);

    /**
     * The remote version is the same as that installed locally. No more method
     * calls after this.
     */
    void noUpdateAvailable();

    /**
     * There is a different remote version than that installed. There may be
     * more method calls after this.
     */
    void updateAvailable();

    /**
     * The retrieved change log could not be transformed. There will be no more
     * method calls after this.
     * @param exception details of the exception
     */
    void transformFailure(IOException exception);
}
