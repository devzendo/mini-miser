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

import org.devzendo.minimiser.gui.messagequeueviewer.HeadlessMessageQueueViewerFactory;
import org.devzendo.minimiser.messagequeue.MessageQueue;
import org.devzendo.minimiser.messagequeue.MessageQueueBorderGuardFactory;
import org.devzendo.minimiser.messagequeue.SimpleMessage;
import org.devzendo.minimiser.messagequeue.StubMessageQueueMiniMiserPrefs;
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
        messageQueue = new MessageQueue(new MessageQueueBorderGuardFactory(new StubMessageQueueMiniMiserPrefs()));

        Assert.assertFalse(headlessStatusBar.isMessageQueueIndicatorEnabled());
        messageQueue.addMessage(new SimpleMessage("subject", "content"));
        Assert.assertFalse(headlessStatusBar.isMessageQueueIndicatorEnabled());

        new StatusBarMessageQueueAdapter(headlessStatusBar, messageQueue, new HeadlessMessageQueueViewerFactory(headlessStatusBar)).wireAdapter();
        
        Assert.assertTrue(headlessStatusBar.isMessageQueueIndicatorEnabled());
    }
}
