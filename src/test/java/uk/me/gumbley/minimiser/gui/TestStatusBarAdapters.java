package uk.me.gumbley.minimiser.gui;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.minimiser.gui.messagequeueviewer.HeadlessMessageQueueViewer;
import uk.me.gumbley.minimiser.gui.messagequeueviewer.HeadlessMessageQueueViewerFactory;
import uk.me.gumbley.minimiser.gui.messagequeueviewer.MessageQueueViewerFactory;
import uk.me.gumbley.minimiser.messagequeue.MessageQueue;
import uk.me.gumbley.minimiser.messagequeue.SimpleMessage;
import uk.me.gumbley.minimiser.util.DelayedExecutor;


/**
 * Tests the interaction between Status Bar, Message Queue and Message Queue
 * Viewer as handled by the StatusBarMessageQueueAdapter.
 * 
 * Also see TestStatusBarAdaptersBeforeWiring which tests a use case that
 * would only be triggered before the wiring happens.
 * 
 * @author matt
 *
 */
public final class TestStatusBarAdapters {
    private HeadlessStatusBar headlessStatusBar;
    private MessageQueue messageQueue;
    private MessageQueueViewerFactory headlessMessageQueueViewerFactory;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        headlessStatusBar = new HeadlessStatusBar(new DelayedExecutor());
        messageQueue = new MessageQueue();
        new StatusBarMessageQueueAdapter(headlessStatusBar, messageQueue).wireAdapter();
        headlessMessageQueueViewerFactory = new HeadlessMessageQueueViewerFactory(headlessStatusBar);
    }
    
    /**
     * 
     */
    @Test
    public void addMessageAndMessageQueueIndicatorEnabled() {
        Assert.assertFalse(headlessStatusBar.isMessageQueueIndicatorEnabled());
        messageQueue.addMessage(new SimpleMessage("subject", "content"));
        Assert.assertTrue(headlessStatusBar.isMessageQueueIndicatorEnabled());
    }
    
    /**
     * 
     */
    @Test
    public void launchMessageViewerHidesMessageQueueIndicator() {
        messageQueue.addMessage(new SimpleMessage("subject", "content"));
        Assert.assertTrue(headlessStatusBar.isMessageQueueIndicatorEnabled());

        headlessMessageQueueViewerFactory.createMessageQueueViewer();
        
        Assert.assertFalse(headlessStatusBar.isMessageQueueIndicatorEnabled());
    }

    /**
     * 
     */
    @Test
    public void closeMessageViewerShowsMessageQueueIndicator() {
        messageQueue.addMessage(new SimpleMessage("subject", "content"));
        Assert.assertTrue(headlessStatusBar.isMessageQueueIndicatorEnabled());

        final HeadlessMessageQueueViewer headlessMessageQueueViewer =
            (HeadlessMessageQueueViewer) headlessMessageQueueViewerFactory.createMessageQueueViewer();
        Assert.assertFalse(headlessStatusBar.isMessageQueueIndicatorEnabled());
        
        headlessMessageQueueViewer.closeViewer();
        Assert.assertTrue(headlessStatusBar.isMessageQueueIndicatorEnabled());
    }
}
