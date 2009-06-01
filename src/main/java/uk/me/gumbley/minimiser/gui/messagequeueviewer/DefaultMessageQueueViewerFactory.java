package uk.me.gumbley.minimiser.gui.messagequeueviewer;

import java.awt.Frame;

import javax.swing.SwingUtilities;

import uk.me.gumbley.minimiser.gui.CursorManager;
import uk.me.gumbley.minimiser.gui.StatusBar;
import uk.me.gumbley.minimiser.messagequeue.MessageQueue;
import uk.me.gumbley.minimiser.pluginmanager.AppDetails;

/**
 * The factory for Swing-based MessageQueueViewers
 * @author matt
 *
 */
public final class DefaultMessageQueueViewerFactory extends 
        AbstractMessageQueueViewerFactory {
    private final CursorManager mCursorManager;
    private final AppDetails mAppDetails;

    /**
     * Pass the status bar on to the abstract base class for factories
     * @param bar the status bar
     * @param main the main application frame
     * @param queue the message queue
     * @param cursor the cursor manager
     * @param appDetails the application name and version
     */
    public DefaultMessageQueueViewerFactory(final StatusBar bar,
            final Frame main,
            final MessageQueue queue,
            final CursorManager cursor,
            final AppDetails appDetails) {
        super(bar, main, queue);
        mCursorManager = cursor;
        mAppDetails = appDetails;
    }

    /**
     * {@inheritDoc}
     */
    public MessageQueueViewer createMessageQueueViewer() {
        assert SwingUtilities.isEventDispatchThread();
        
        messageViewerCreated();
        mCursorManager.hourglass(this.getClass().getSimpleName());
        // TODO inject MessageRendererFactory
        final DefaultMessageQueueViewer messageQueueViewer =
            new DefaultMessageQueueViewer(this, 
                new MessageRendererFactory(),
                mAppDetails);
        mCursorManager.normal(this.getClass().getSimpleName());
        return messageQueueViewer;
    }
}
