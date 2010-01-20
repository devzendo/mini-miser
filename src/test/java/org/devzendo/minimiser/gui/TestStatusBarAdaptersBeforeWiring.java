package org.devzendo.minimiser.gui;

import org.devzendo.minimiser.gui.messagequeueviewer.HeadlessMessageQueueViewerFactory;
import org.devzendo.minimiser.messagequeue.MessageQueue;
import org.devzendo.minimiser.messagequeue.MessageQueueBorderGuardFactory;
import org.devzendo.minimiser.messagequeue.SimpleMessage;
import org.devzendo.minimiser.messagequeue.StubMessageQueuePrefs;
import org.devzendo.minimiser.util.DelayedExecutor;
import org.junit.Assert;
import org.junit.Test;


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
        messageQueue = new MessageQueue(new MessageQueueBorderGuardFactory(new StubMessageQueuePrefs()));

        Assert.assertFalse(headlessStatusBar.isMessageQueueIndicatorEnabled());
        messageQueue.addMessage(new SimpleMessage("subject", "content"));
        Assert.assertFalse(headlessStatusBar.isMessageQueueIndicatorEnabled());

        new StatusBarMessageQueueAdapter(headlessStatusBar, messageQueue, new HeadlessMessageQueueViewerFactory(headlessStatusBar)).wireAdapter();
        
        Assert.assertTrue(headlessStatusBar.isMessageQueueIndicatorEnabled());
    }
}
