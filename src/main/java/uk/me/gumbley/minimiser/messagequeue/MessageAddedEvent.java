package uk.me.gumbley.minimiser.messagequeue;


/**
 * A message has been added or removed from the MessageQueue. This is the
 * notification you receive when a message is added.
 * 
 * @author matt
 *
 */
public final class MessageAddedEvent extends MessageQueueDetailEvent {

    /**
     * Construct the event relating to a particular message
     * @param msg the message relating to this event
     */
    public MessageAddedEvent(final Message msg) {
        super(msg);
    }
}
