package uk.me.gumbley.minimiser.gui;

/**
 * A status bar at the bottom of the main frame, for presenting announcements,
 * displaying a progress bar for lengthy operations, and displaying various
 * indicators.
 * 
 * @author matt
 *
 */
public interface StatusBar {

    /**
     * Show a message in the status area permanently, until cleared by
     * clearMessage. Any temporary message will override this, for the time
     * stated, but once done, the message will revert to this one.
     * @param message the message
     */
    void displayMessage(String message);
    
    /**
     * Clear the permanent message. Has no effect on any temporary message,
     * which will disappear shortly (to be replaced by nothing due to this call
     * to clearMessage). 
     */
    void clearMessage();
    
    /**
     * Display a message temporarily, for several seconds, overriding any
     * permanent message.
     * @param message the message.
     * @param seconds how long to show it for.
     */
    void displayTemporaryMessage(String message, int seconds);
    
    /**
     * Enable the display of the progress bar, with a given maximum number of
     * steps. 
     * @param max the maximum number of steps.
     */
    void setProgressLength(int max);
    
    /**
     * Indicate which step in the progress we are at.
     * @param step the step, [0..max]
     */
    void setProgressStep(int step);
    
    /**
     * Clear the progress indicator
     */
    void clearProgress();
    
    /**
     * Show the 'padlock' indicator indicating that we're working on an
     * encrypted database?
     * @param encrypted true of encrypted, false if plain
     */
    void setEncryptedIndicator(boolean encrypted);
}
