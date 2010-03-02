/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devzendo.minimiser.gui;

import org.devzendo.minimiser.gui.messagequeueviewer.HeadlessMessageQueueViewer;
import org.devzendo.minimiser.gui.messagequeueviewer.HeadlessMessageQueueViewerFactory;
import org.devzendo.minimiser.gui.messagequeueviewer.MessageQueueViewerFactory;
import org.devzendo.minimiser.messagequeue.MessageQueue;
import org.devzendo.minimiser.messagequeue.MessageQueueBorderGuardFactory;
import org.devzendo.minimiser.messagequeue.SimpleMessage;
import org.devzendo.minimiser.messagequeue.StubMessageQueuePrefs;
import org.devzendo.minimiser.util.DelayedExecutor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


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
        messageQueue = new MessageQueue(new MessageQueueBorderGuardFactory(new StubMessageQueuePrefs()));
        headlessMessageQueueViewerFactory = new HeadlessMessageQueueViewerFactory(headlessStatusBar);
        new StatusBarMessageQueueAdapter(headlessStatusBar, messageQueue, headlessMessageQueueViewerFactory).wireAdapter();
        
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
