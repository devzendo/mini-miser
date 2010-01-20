package org.devzendo.minimiser.gui.messagequeueviewer;

import org.devzendo.minimiser.messagequeue.BooleanFlagSettingMessage;
import org.devzendo.minimiser.messagequeue.Message;
import org.devzendo.minimiser.messagequeue.SimpleDSTAMessage;
import org.devzendo.minimiser.messagequeue.SimpleMessage;

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
        } else if (message instanceof BooleanFlagSettingMessage) {
            return new BooleanFlagSettingMessageRenderer((BooleanFlagSettingMessage) message);
        }
        return null;
    }
}
