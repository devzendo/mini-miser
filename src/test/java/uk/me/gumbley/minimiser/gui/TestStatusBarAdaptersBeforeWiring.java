package uk.me.gumbley.minimiser.gui;

import org.junit.Assert;
import org.junit.Test;
import uk.me.gumbley.minimiser.messagequeue.MessageQueue;
import uk.me.gumbley.minimiser.messagequeue.SimpleMessage;
import uk.me.gumbley.minimiser.util.DelayedExecutor;


/**
 * Tests the interaction between Status Bar, Message Queue and Message Queue
 * Viewer as handled by the StatusBarMessageQueueAdapter.
 * 
 * Also see TestStatusBarAdapters for the majority of the tests. This class
 * tests the case when messages are added to the message queue before the
 * adapter wires the message queue to the status bar. The status bar should
 * therefore show that there are messages.
 * 
 * @author matt
 *
 */
public final class TestStatusBarAdaptersBeforeWiring {
    private HeadlessStatusBar headlessStatusBar;
    private MessageQueue messageQueue;

    /**
     * 
     */
    @Test
    public void addMessagesBeforeAdapterInstantiationAndMessageQueueIndicatorEnabled() {
        headlessStatusBar = new HeadlessStatusBar(new DelayedExecutor());
        messageQueue = new MessageQueue();

        Assert.assertFalse(headlessStatusBar.isMessageQueueIndicatorEnabled());
        messageQueue.addMessage(new SimpleMessage("subject", "content"));
        Assert.assertFalse(headlessStatusBar.isMessageQueueIndicatorEnabled());

        new StatusBarMessageQueueAdapter(headlessStatusBar, messageQueue).wireAdapter();
        
        Assert.assertTrue(headlessStatusBar.isMessageQueueIndicatorEnabled());
    }
}
