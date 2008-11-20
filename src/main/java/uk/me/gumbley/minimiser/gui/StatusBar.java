package uk.me.gumbley.minimiser.gui;

import java.awt.event.ActionListener;

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
    
    /**
     * Set the number of queued messages.
     * @param queueSize the number of queued messages
     */
    void setNumberOfQueuedMessages(int queueSize);
    
    /**
     * Obtain the number of queued messages
     * @return the number of queued messages
     */
    int getNumberOfQueuedMessages();

    /**
     * Set and cleared when the message queue viewer window is shown and closed,
     * this is used to prevent the message queue indicator from displaying
     * when the viewer is present. 
     * @param showing true iff the viewer is showing.
     */
    void setMessageQueueViewerShowing(final boolean showing);

    /**
     * Add a listener to the message queue indicator button
     * @param listener the listener
     */
    void addLaunchMessageQueueActionListener(final ActionListener listener);
}
