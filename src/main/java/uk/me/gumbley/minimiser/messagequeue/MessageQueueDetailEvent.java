package uk.me.gumbley.minimiser.messagequeue;


/**
 * A message has been added or removed from the MessageQueue. This is the
 * notification you receive when a message is added/removed.
 * 
 * @author matt
 *
 */
public abstract class MessageQueueDetailEvent extends MessageQueueEvent {
    private final Message message;

    /**
     * Construct the event relating to a particular message
     * @param msg the message relating to this event
     */
    public MessageQueueDetailEvent(final Message msg) {
        this.message = msg;
    }

    /**
     * Which Message does this event relate to?
     * @return the message
     */
    public final Message getMessage() {
        return message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final MessageQueueDetailEvent other = (MessageQueueDetailEvent) obj;
        if (message == null) {
            if (other.message != null) {
                return false;
            }
        } else if (!message.equals(other.message)) {
            return false;
        }
        return true;
    }
}
