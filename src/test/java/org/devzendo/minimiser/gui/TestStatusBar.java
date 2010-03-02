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

import org.devzendo.commoncode.concurrency.ThreadUtils;
import org.devzendo.minimiser.util.DelayedExecutor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



/**
 * Test the status bar.
 * @author matt
 *
 */
public final class TestStatusBar {
    
    private static final String HELLO = "hello";
    private static final String TEST = "test";
    private HeadlessStatusBar headlessStatusBar;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        headlessStatusBar = new HeadlessStatusBar(new DelayedExecutor());
    }
    
    /**
     * 
     */
    @Test
    public void emptiness() {
        Assert.assertEquals("", headlessStatusBar.getDisplayedMessage());
    }
    
    /**
     * 
     */
    @Test
    public void displayMessageSetsPermanently() {
        headlessStatusBar.displayMessage(TEST);
        Assert.assertEquals(TEST, headlessStatusBar.getDisplayedMessage());
    }
    
    /**
     * 
     */
    @Test
    public void displayNullShowsEmpty() {
        headlessStatusBar.displayMessage(null);
        Assert.assertEquals("", headlessStatusBar.getDisplayedMessage());
    }
    
    /**
     * 
     */
    @Test
    public void clearMessageClearsPermanently() {
        headlessStatusBar.displayMessage(TEST);
        headlessStatusBar.clearMessage();
        Assert.assertEquals("", headlessStatusBar.getDisplayedMessage());
    }

    /**
     * 
     */
    @Test
    public void displayTemporaryOverridesPermanentThenReverts() {
        headlessStatusBar.displayMessage(TEST);
        headlessStatusBar.displayTemporaryMessage(HELLO, 1);
        Assert.assertEquals(HELLO, headlessStatusBar.getDisplayedMessage());
        ThreadUtils.waitNoInterruption(1250);
        Assert.assertEquals(TEST, headlessStatusBar.getDisplayedMessage());
    }

    /**
     * 
     */
    @Test
    public void displayTemporaryOverridesPermanentClearThenReverts() {
        headlessStatusBar.displayTemporaryMessage(HELLO, 1);
        Assert.assertEquals(HELLO, headlessStatusBar.getDisplayedMessage());
        ThreadUtils.waitNoInterruption(1250);
        Assert.assertEquals("", headlessStatusBar.getDisplayedMessage());
    }
    
    private void assertMessageQueueIndicatorDisabled() {
        Assert.assertFalse(headlessStatusBar.isMessageQueueIndicatorEnabled());
    }
    
    private void assertMessageQueueIndicatorEnabled() {
        Assert.assertTrue(headlessStatusBar.isMessageQueueIndicatorEnabled());
    }

    /**
     * 
     */
    @Test
    public void messageQueueIndicatorDisabledWithNoMessages() {
        assertMessageQueueIndicatorDisabled();
        Assert.assertEquals(0, headlessStatusBar.getNumberOfQueuedMessages());
    }

    /**
     * 
     */
    @Test
    public void messageQueueIndicatorEnabledWithMessages() {
        headlessStatusBar.setNumberOfQueuedMessages(1);
        assertMessageQueueIndicatorEnabled();
        Assert.assertEquals(1, headlessStatusBar.getNumberOfQueuedMessages());
    }

    /**
     * 
     */
    @Test
    public void messageQueueIndicatorDisabledWithMessagesAndViewerShowing() {
        headlessStatusBar.setNumberOfQueuedMessages(1);
        headlessStatusBar.setMessageQueueViewerShowing(true);
        assertMessageQueueIndicatorDisabled();
        
    }

    /**
     * Should never happen - the message queue indicator button is not
     * visible when there are no messages, so how could the viewer be made
     * visible? 
     */
    @Test
    public void messageQueueIndicatorDisabledWithNoMessagesAndViewerShowing() {
        headlessStatusBar.setMessageQueueViewerShowing(true);
        assertMessageQueueIndicatorDisabled();
    }
    
    /**
     * 
     */
    @Test
    public void messageQueueIndicatorBecomesEnabledWhenMessageViewerClosedWithMessagesRemaining() {
        headlessStatusBar.setNumberOfQueuedMessages(1);
        headlessStatusBar.setMessageQueueViewerShowing(true);
        assertMessageQueueIndicatorDisabled(); // repetition, but for clarity
        
        headlessStatusBar.setMessageQueueViewerShowing(false);
        assertMessageQueueIndicatorEnabled();
    }

    /**
     * 
     */
    @Test
    public void messageQueueIndicatorIsDisabledWhenMessageViewerClosedWithNoMessagesRemaining() {
        headlessStatusBar.setNumberOfQueuedMessages(1);
        headlessStatusBar.setMessageQueueViewerShowing(true);
        assertMessageQueueIndicatorDisabled(); // repetition, but for clarity

        headlessStatusBar.setNumberOfQueuedMessages(0);
        
        headlessStatusBar.setMessageQueueViewerShowing(false);
        assertMessageQueueIndicatorDisabled();
    }

}
