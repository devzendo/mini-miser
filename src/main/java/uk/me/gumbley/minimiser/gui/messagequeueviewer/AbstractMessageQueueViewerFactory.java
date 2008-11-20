package uk.me.gumbley.minimiser.gui.messagequeueviewer;

import uk.me.gumbley.minimiser.gui.StatusBar;

/**
 * Provide framework for MessageQueueViewerFactories.
 * @author matt
 *
 */
public abstract class AbstractMessageQueueViewerFactory implements MessageQueueViewerFactory {

    private final StatusBar statusBar;

    /**
     * Encapsulate the status bar so that viewers only need to tell their
     * factory that they have opened or closed.
     * @param bar the status bar
     */
    public AbstractMessageQueueViewerFactory(final StatusBar bar) {
        this.statusBar = bar;
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
    
}
