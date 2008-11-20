package uk.me.gumbley.minimiser.gui.messagequeueviewer;

import uk.me.gumbley.minimiser.gui.StatusBar;

/**
 * The factory for Swing-based MessageQueueViewers
 * @author matt
 *
 */
public final class DefaultMessageQueueViewerFactory extends 
        AbstractMessageQueueViewerFactory {
    /**
     * Pass the status bar on to the abstrcat base class for factories
     * @param bar the status bar
     */
    public DefaultMessageQueueViewerFactory(final StatusBar bar) {
        super(bar);
    }

    /**
     * {@inheritDoc}
     */
    public MessageQueueViewer createMessageQueueViewer() {
        messageViewerCreated();
        return new DefaultMessageQueueViewer(this);
    }
}
