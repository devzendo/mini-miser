package uk.me.gumbley.minimiser.gui.messagequeueviewer;

import java.awt.Frame;
import uk.me.gumbley.minimiser.messagequeue.MessageQueue;

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

    /**
     * Called by the MessageQueueViewer when it is creating its dialog, so that
     * the dialog can be made a child of the main application frame.
     * 
     * @return the application main frame
     */
    Frame getMainFrame();
    
    /**
     * Called by the MessageQueueViewer so it can interact with the MessageQueue
     * @return the MessageQueue
     */
    MessageQueue getMessageQueue();
}
