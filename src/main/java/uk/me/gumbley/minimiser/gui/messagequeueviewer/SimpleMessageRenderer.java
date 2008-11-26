package uk.me.gumbley.minimiser.gui.messagequeueviewer;

import java.awt.Component;
import uk.me.gumbley.minimiser.messagequeue.SimpleMessage;

/**
 * A renderer for SimpleMessages.
 * 
 * @author matt
 *
 */
public final class SimpleMessageRenderer extends AbstractSimpleMessageRenderer {

    /**
     * Construct the renderer
     * @param message the message to render
     */
    public SimpleMessageRenderer(final SimpleMessage message) {
        super(message);
    }

    /**
     * SimpleMessages have no controls
     * {@inheritDoc}
     */
    public Component renderControls() {
        return null;
    }
}
