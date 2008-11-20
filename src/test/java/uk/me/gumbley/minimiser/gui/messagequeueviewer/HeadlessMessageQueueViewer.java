package uk.me.gumbley.minimiser.gui.messagequeueviewer;

/**
 * A headless MessageQueueViewer for tests.
 * @author matt
 *
 */
public final class HeadlessMessageQueueViewer extends AbstractMessageQueueViewer {

    /**
     * Construct the viewer given its fcatory.
     * @param factory this viewer's fcatory
     */
    public HeadlessMessageQueueViewer(final MessageQueueViewerFactory factory) {
        super(factory);
    }
    
    /**
     * For use by unit tests, indicate that "the user has closed the viewer
     * dialog". 
     */
    public void closeViewer() {
        getMessageQueueViewerFactory().messageViewerClosed();
    }
}
