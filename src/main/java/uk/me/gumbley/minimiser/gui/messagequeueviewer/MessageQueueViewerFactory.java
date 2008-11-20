package uk.me.gumbley.minimiser.gui.messagequeueviewer;

/**
 * MessageQueueViewerFactory objects create, and receive notification of
 * closure of, their MessageQueueViewer.
 * 
 * @author matt
 *
 */
public interface MessageQueueViewerFactory {

    /**
     * Create and show the MessageQueueViewer.
     * @return the MessageQueueViewer.
     */
    MessageQueueViewer createMessageQueueViewer();
    
    /**
     * Called by the MessageQueueViewer when it is closed by the user, so that
     * notification of this can be passed around (e.g. to the status bar, so
     * that it can enable the message queue indicator (if any messages remian))
     */
    void messageViewerClosed();
}
