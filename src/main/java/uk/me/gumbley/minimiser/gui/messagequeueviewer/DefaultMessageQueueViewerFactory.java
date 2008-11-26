package uk.me.gumbley.minimiser.gui.messagequeueviewer;

import java.awt.Frame;
import javax.swing.SwingUtilities;
import uk.me.gumbley.minimiser.gui.StatusBar;
import uk.me.gumbley.minimiser.messagequeue.MessageQueue;

/**
 * The factory for Swing-based MessageQueueViewers
 * @author matt
 *
 */
public final class DefaultMessageQueueViewerFactory extends 
        AbstractMessageQueueViewerFactory {
    /**
     * Pass the status bar on to the abstract base class for factories
     * @param bar the status bar
     * @param main the main application frame
     * @param queue the message queue
     */
    public DefaultMessageQueueViewerFactory(final StatusBar bar, final Frame main, final MessageQueue queue) {
        super(bar, main, queue);
    }

    /**
     * {@inheritDoc}
     */
    public MessageQueueViewer createMessageQueueViewer() {
        assert SwingUtilities.isEventDispatchThread();
        
        messageViewerCreated();
        // TODO inject MessageRendererFactory
        return new DefaultMessageQueueViewer(this, new MessageRendererFactory());
    }
}
