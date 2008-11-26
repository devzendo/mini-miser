package uk.me.gumbley.minimiser.gui.messagequeueviewer;

import uk.me.gumbley.minimiser.messagequeue.Message;
import uk.me.gumbley.minimiser.messagequeue.SimpleDSTAMessage;
import uk.me.gumbley.minimiser.messagequeue.SimpleMessage;

/**
 * A MessageRendererFactory is a factory for MessageRenderers. Given a message
 * type, create the correct MessageRenderer used to display it in the
 * MessageQueueViewer.
 * <p>
 * Messages just contain content. MessageRenderers provide graphical components
 * that display that content.
 * 
 * @author matt
 *
 */
public final class MessageRendererFactory {

    /**
     * Given a Message, create a MessageRenderer that can render this message.
     * @param message some kind of Message
     * @return a MessageRenderer for this Message
     */
    public MessageRenderer createRenderer(final Message message) {
        if (message instanceof SimpleMessage) {
            return new SimpleMessageRenderer((SimpleMessage) message);
        } else if (message instanceof SimpleDSTAMessage) {
            return new SimpleDSTAMessageRenderer((SimpleDSTAMessage) message);
        }
        return null;
    }
}
