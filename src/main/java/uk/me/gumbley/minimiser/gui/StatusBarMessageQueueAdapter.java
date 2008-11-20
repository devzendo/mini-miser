package uk.me.gumbley.minimiser.gui;

import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.messagequeue.MessageQueue;
import uk.me.gumbley.minimiser.messagequeue.MessageQueueEvent;
import uk.me.gumbley.minimiser.messagequeue.MessageQueueModifiedEvent;

/**
 * Adapts between the status bar and message queue. Specifically:
 * <ul>
 * <li> listens for message queue size modifications and passes this info
 * on to the status bar 
 * </ul>
 * @author matt
 *
 */
public final class StatusBarMessageQueueAdapter {

    private final StatusBar bar;
    private final MessageQueue queue;

    /**
     * Construct the adapter, and wire up.
     * @param statusBar the status bar
     * @param messageQueue the message queue
     */
    public StatusBarMessageQueueAdapter(final StatusBar statusBar, final MessageQueue messageQueue) {
        this.bar = statusBar;
        this.queue = messageQueue;
        wireAdapter();
    }

    private void wireAdapter() {
        queue.addMessageQueueEventObserver(new Observer<MessageQueueEvent>() {
            public void eventOccurred(final MessageQueueEvent observableEvent) {
                if (observableEvent instanceof MessageQueueModifiedEvent) {
                    final MessageQueueModifiedEvent mqme = (MessageQueueModifiedEvent) observableEvent;
                    bar.setNumberOfQueuedMessages(mqme.getNewQueueSize());
                }
            }
        });
    }
}
