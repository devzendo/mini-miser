package org.devzendo.minimiser.gui.messagequeueviewer;

import java.awt.Frame;

import org.devzendo.minimiser.gui.StatusBar;
import org.devzendo.minimiser.messagequeue.MessageQueue;

/**
 * Provide framework for MessageQueueViewerFactories.
 * @author matt
 *
 */
public abstract class AbstractMessageQueueViewerFactory implements MessageQueueViewerFactory {

    private final StatusBar statusBar;
    private final Frame mainFrame;
    private final MessageQueue messageQueue;

    /**
     * Encapsulate the status bar so that viewers only need to tell their
     * factory that they have opened or closed.
     * @param bar the status bar
     * @param main the main application frame
     * @param queue the message queue
     */
    public AbstractMessageQueueViewerFactory(final StatusBar bar, final Frame main, final MessageQueue queue) {
        this.statusBar = bar;
        this.mainFrame = main;
        this.messageQueue = queue;
    }

    /**
     * Called by factory create methods when viewers are created, notify the
     * status bar that the viewer is open.
     */
    protected final void messageViewerCreated() {
        statusBar.setMessageQueueViewerShowing(true);
    }
    
    /**
     * {@inheritDoc}
     */
    public final void messageViewerClosed() {
        statusBar.setMessageQueueViewerShowing(false);
    }

    /**
     * Obtain the application's main frame, against which message viewer dialogs
     * are shown.
     * @return the main frame
     */
    public final Frame getMainFrame() {
        return mainFrame;
    }
    
    /**
     * {@inheritDoc}
     */
    public final MessageQueue getMessageQueue() {
        return messageQueue;
    }
}
