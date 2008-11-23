package uk.me.gumbley.minimiser.gui.messagequeueviewer;

import uk.me.gumbley.minimiser.gui.StatusBar;

/**
 * A MessageQueueViewerFactory for use in unit tests.
 * @author matt
 *
 */
public final class HeadlessMessageQueueViewerFactory extends
        AbstractMessageQueueViewerFactory {

    /**
     * Pass the status bar to the abstract base class
     * @param bar the status bar
     */
    public HeadlessMessageQueueViewerFactory(final StatusBar bar) {
        super(bar, null, null);
    }

    /**
     * {@inheritDoc}
     */
    public MessageQueueViewer createMessageQueueViewer() {
        messageViewerCreated();
        return new HeadlessMessageQueueViewer(this);
    }
}
