package uk.me.gumbley.minimiser.gui.messagequeueviewer;

import java.awt.Component;
import uk.me.gumbley.minimiser.messagequeue.Message;

/**
 * A renderer for Simple Messages - just renders the text. Controls are left
 * up to subclasses.
 * 
 * @author matt
 *
 */
public abstract class AbstractSimpleMessageRenderer implements MessageRenderer {

    private final Message message;

    /**
     * Construct the renderer
     * @param msg the message to render
     */
    public AbstractSimpleMessageRenderer(final Message msg) {
        this.message = msg;
    }

    /**
     * {@inheritDoc}
     */
    public final Component render() {
        return new HTMLPanel(message.getMessageContent().toString());
    }
}
