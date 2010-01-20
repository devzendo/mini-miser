package org.devzendo.minimiser.gui.messagequeueviewer;

/**
 * Provide framework for MessageQueueViewers.
 * 
 * @author matt
 *
 */
public abstract class AbstractMessageQueueViewer implements MessageQueueViewer {
    private final MessageQueueViewerFactory messageQueueViewerFactory;
    
    /**
     * Create an AbstractMessageQueueViewer given its factory. The Viewer knows
     * of its factory, so it can inform the factory that it has opened or
     * closed. The factory encapsulates the informing of the other objects
     * that need to know, notably the status bar.
     * @param factory the factory that created this viewer
     */
    public AbstractMessageQueueViewer(final MessageQueueViewerFactory factory) {
        this.messageQueueViewerFactory = factory;
    }
    
    /**
     * @return this viewer's factory
     */
    protected final MessageQueueViewerFactory getMessageQueueViewerFactory() {
        return messageQueueViewerFactory;
    }
}
