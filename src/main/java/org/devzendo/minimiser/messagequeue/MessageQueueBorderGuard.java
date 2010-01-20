package org.devzendo.minimiser.messagequeue;

/**
 * MessageQueueBorderGuards vet incoming messages for suitability to be added to
 * the MessageQueue. They also process messages that have been on the
 * MessageQueue and that have been removed.
 * <p>
 * This is a GoF Strategy.
 * 
 * @author matt
 *
 */
public interface MessageQueueBorderGuard {

    /**
     * Is the given message allowed to be placed on the MessageQueue?
     * @param message the Message to investigate
     * @return true iff allowed to be placed on the MessageQueue
     */
    boolean isAllowed(Message message);

    /**
     * Messages that are allowed onto the queue are prepared - i.e. any state
     * stored persistently is moved into the message.
     * 
     * @param message the message being prepared
     */
    void prepareMessage(Message message);

    /**
     * When this message is being removed from the MessageQueue, process any
     * type-specific behaviour for this message, e.g. persisting the DSTA
     * flag for the message.
     * @param message the Message to process during removal
     */
    void processMessageRemoval(Message message);
}
