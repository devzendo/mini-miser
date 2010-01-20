package org.devzendo.minimiser.messagequeue;


/**
 * A message has been added or removed from the MessageQueue. This is the
 * notification you receive.
 * 
 * @author matt
 *
 */
public final class MessageQueueModifiedEvent extends MessageQueueEvent {
    private final int newQueueSize;

    /**
     * The message queue has been modified
     * @param qs the new size of the queue 
     */
    public MessageQueueModifiedEvent(final int qs) {
        this.newQueueSize = qs;
        
    }

    /**
     * What is the new queue size?
     * @return the new queue size
     */
    public int getNewQueueSize() {
        return newQueueSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + newQueueSize;
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
        final MessageQueueModifiedEvent other = (MessageQueueModifiedEvent) obj;
        if (newQueueSize != other.newQueueSize) {
            return false;
        }
        return true;
    }
}
