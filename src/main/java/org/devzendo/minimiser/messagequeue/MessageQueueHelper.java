package org.devzendo.minimiser.messagequeue;

import org.devzendo.minimiser.springloader.SpringLoader;

/**
 * Toolkit for easily adding Messages to the MessageQueue..
 * 
 * @author matt
 *
 */
public final class MessageQueueHelper {
    private static MessageQueue messageQueue;

    /**
     * 
     */
    private MessageQueueHelper() {
        // no instances
    }
    
    /**
     * Initialise the toolkit by obtaining the things it needs to add messages
     * to the Message Queue.
     * 
     * @param springLoader the spring loader
     */
    public static void initialise(final SpringLoader springLoader) {
        messageQueue = springLoader.getBean("messageQueue", MessageQueue.class);
    }

    /**
     * @see MessageQueue.addMessage(Message)
     * @param message the message to add.
     */
    public static void addMessage(final Message message) {
        messageQueue.addMessage(message);
    }
}
