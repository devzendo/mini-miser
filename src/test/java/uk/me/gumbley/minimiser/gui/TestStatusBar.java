package uk.me.gumbley.minimiser.gui;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.me.gumbley.commoncode.concurrency.ThreadUtils;
import uk.me.gumbley.minimiser.util.DelayedExecutor;


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

    /**
     * 
     */
    @Test
    public void messageQueueIndicatorDisabledWithNoMessages() {
        Assert.assertEquals(0, headlessStatusBar.getNumberOfQueuedMessages());
        Assert.assertFalse(headlessStatusBar.isMessageQueueIndicatorEnabled());
    }

    /**
     * 
     */
    @Test
    public void messageQueueIndicatorEnabledWithMessages() {
        headlessStatusBar.setNumberOfQueuedMessages(1);
        Assert.assertEquals(1, headlessStatusBar.getNumberOfQueuedMessages());
        Assert.assertTrue(headlessStatusBar.isMessageQueueIndicatorEnabled());
    }

}
