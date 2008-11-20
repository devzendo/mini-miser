package uk.me.gumbley.minimiser.gui.messagequeueviewer;

/**
 * A Swing-based MessageQueueViewer.
 * 
 * @author matt
 *
 */
public final class DefaultMessageQueueViewer extends AbstractMessageQueueViewer {

    /**
     * Create the DefaultMessageQueueViewer given its factory.
     * @param factory this viewer's factory
     */
    public DefaultMessageQueueViewer(final MessageQueueViewerFactory factory) {
        super(factory);
    }
    
    // TODO don't forget to getMessageQueueViewerFactory().messageViewerClosed();
    // on window close!
}
