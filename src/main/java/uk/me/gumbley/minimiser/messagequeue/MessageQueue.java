package uk.me.gumbley.minimiser.messagequeue;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.commoncode.patterns.observer.ObserverList;

/**
 * The MessageQueue is used to hold non-persistent, asynchronously-delivered
 * messages to the user. It allows subscribers to be notified of additions and
 * removals from the queue, and allows them to retrieve items by index.
 * @author matt
 *
 */
public final class MessageQueue {
    private static final Logger LOGGER = Logger.getLogger(MessageQueue.class);
    private final ObserverList<MessageQueueEvent> observerList;
    private final ArrayList<Message> messages;
    
    /**
     * Construct the MessageQueue.
     */
    public MessageQueue() {
        observerList = new ObserverList<MessageQueueEvent>();
        messages = new ArrayList<Message>();
    }

    /**
     * How many messages are there in the MessageQueue?
     * @return the number of messages.
     */
    public int size() {
        return messages.size();
    }

    /**
     * Return the currently selected message, or null, if no message has been
     * selected.
     * @return the current message or null
     */
    public Message getCurrentMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Add an observer of MessageQueueEvents
     * @param obs the observer
     */
    public void addMessageQueueEventObserver(final Observer<MessageQueueEvent> obs) {
        observerList.addObserver(obs);
    }

    /**
     * Add a message to the queue, notify observers of the new message and
     * queue size
     * @param message the new message
     */
    public void addMessage(final Message message) {
        if (message == null) {
            final String warning = "Cannot add a null message";
            LOGGER.warn(warning);
            throw new IllegalArgumentException(warning);
        }
        messages.add(message);
        observerList.eventOccurred(new MessageAddedEvent(message));
        observerList.eventOccurred(new MessageQueueModifiedEvent(messages.size()));
    }

    /**
     * Remove a message from the queue, notify observers of the new message and
     * queue size
     * @param message the message removed
     */
    public void removeMessage(final Message message) {
        if (message == null) {
            final String warning = "Cannot remove a null message";
            LOGGER.warn(warning);
            throw new IllegalArgumentException(warning);
        }
        if (!messages.contains(message)) {
            final String warning = "Cannot remove message " + message + " since it is not in the message queue";
            LOGGER.warn(warning);
            throw new IllegalStateException(warning);
        }
        messages.remove(message);
        observerList.eventOccurred(new MessageRemovedEvent(message));
        observerList.eventOccurred(new MessageQueueModifiedEvent(messages.size()));
    }

    /**
     * Obtain a message given its index
     * @param index the index into the message list
     * @return the message
     */
    public Message getMessage(final int index) {
        return messages.get(index);
    }
    
}
