package uk.me.gumbley.minimiser.messagequeue;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.commoncode.patterns.observer.ObserverList;
import uk.me.gumbley.minimiser.gui.dialog.dstamessage.DSTAMessageId;
import uk.me.gumbley.minimiser.prefs.Prefs;

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
    private int currentMessageIndex;
    private final MessageQueueBorderGuardFactory messageQueueBorderGuardFactory;
    
    /**
     * Construct the MessageQueue.
     * @param borderGuardFactory the MessageQueueBorderGuardFactory
     */
    public MessageQueue(final MessageQueueBorderGuardFactory borderGuardFactory) {
        this.messageQueueBorderGuardFactory = borderGuardFactory;
        observerList = new ObserverList<MessageQueueEvent>();
        messages = new ArrayList<Message>();
        currentMessageIndex = -1;
    }

    /**
     * How many messages are there in the MessageQueue?
     * @return the number of messages.
     */
    public int size() {
        return messages.size();
    }

    /**
     * Return the index of the currently selected message, or -1, if no message
     * has been selected.
     * @return the current message index or -1
     */
    public int getCurrentMessageIndex() {
        return currentMessageIndex;
    }

    /**
     * Set the index of the current message
     * @param newIndex the new index value
     */
    public void setCurrentMessageIndex(final int newIndex) {
        if (newIndex < 0) {
            final String warning = "Cannot set the current message index to a negative value (" + newIndex + ")";
            LOGGER.warn(warning);
            throw new IllegalArgumentException(warning);
        } else if (newIndex > size() - 1) {
            final String warning = "Cannot set the current message index past the size of the list (size is "
                + size() + "; attempting to set current index to " + newIndex + ")";
            LOGGER.warn(warning);
            throw new IllegalArgumentException(warning);
        }
        LOGGER.info("Current message index is now " + newIndex);
        currentMessageIndex = newIndex;
    }
    
    /**
     * Convenience method for attempting to set the current message index to
     * the next one. Can fail with IllegalArgumentException if you attempt
     * to go off the end of the list.
     */
    public void next() {
        setCurrentMessageIndex(currentMessageIndex + 1);
    }

    /**
     * Convenience method for attempting to set the current message index to
     * the previous one. Can fail with IllegalArgumentException if you attempt
     * to go off the end of the list.
     */
    public void previous() {
        setCurrentMessageIndex(currentMessageIndex - 1);
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
     * queue size. If the message is a SimpleDSTAMessaage and user has indicated
     * that they don't want to see the message again, it will NOT be added to
     * the queue. 
     * @param message the new message
     */
    public void addMessage(final Message message) {
        if (message == null) {
            final String warning = "Cannot add a null message";
            LOGGER.warn(warning);
            throw new IllegalArgumentException(warning);
        }
        
        final MessageQueueBorderGuard borderGuard = getBorderGuardForMessage(message);
        if (!borderGuard.isAllowed(message)) {
            LOGGER.debug("Not adding message '" + message.getSubject() + "' since it has been vetoed");
            return;
        }
        
        borderGuard.prepareMessage(message);
        
        LOGGER.info("Message Added: " + message.getSubject());
        if (currentMessageIndex == -1) {
            currentMessageIndex = 0;
            LOGGER.info("Current message index is 0");
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
        LOGGER.info("Message Removed: " + message.getSubject());
        if (messages.size() == 1) {
            currentMessageIndex = -1;
            LOGGER.info("Current message index is <undefined>");
        } else if (currentMessageIndex == messages.size() - 1) {
            setCurrentMessageIndex(currentMessageIndex - 1);
        }

        final MessageQueueBorderGuard borderGuard = getBorderGuardForMessage(message);
        borderGuard.processMessageRemoval(message);
        
        messages.remove(message);
        observerList.eventOccurred(new MessageRemovedEvent(message));
        observerList.eventOccurred(new MessageQueueModifiedEvent(messages.size()));
    }

    private MessageQueueBorderGuard getBorderGuardForMessage(final Message message) {
        final MessageQueueBorderGuard borderGuard = messageQueueBorderGuardFactory.createBorderGuard(message);
        return borderGuard;
    }
    
    /**
     * Obtain a message given its index
     * @param index the index into the message list
     * @return the message
     */
    public Message getMessageByIndex(final int index) {
        return messages.get(index);
    }
}
