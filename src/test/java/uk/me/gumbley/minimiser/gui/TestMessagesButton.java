package uk.me.gumbley.minimiser.gui;

import java.awt.Color;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.util.Sleeper;


/**
 * Tests the MessageButton
 * @author matt
 *
 */
public final class TestMessagesButton extends LoggingTestCase {
    private static final Logger LOGGER = Logger
            .getLogger(TestMessagesButton.class);
    private MessagesButton button;
    private Sleeper sleeper; 
        
    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        sleeper = new Sleeper(10);
        button = new MessagesButton(sleeper);
    }
    
    /**
     * 
     */
    @After
    public void terminateWithPrejudice() {
        button.interruptThread();
    }
    
    /**
     * 
     */
    @Test
    public void checkInitialState() {
        Assert.assertFalse(button.isVisible());
        Assert.assertEquals(0, button.getNumberOfMessages());
        Assert.assertEquals("0 Messages", button.getText());
    }
    
    /**
     * 
     */
    @Test
    public void checkVisibleWhenMessageAdded() {
        button.setNumberOfMessages(1);
        Assert.assertTrue(button.isVisible());
        Assert.assertEquals(1, button.getNumberOfMessages());
        Assert.assertEquals("1 Message", button.getText());

        button.setNumberOfMessages(2);
        Assert.assertTrue(button.isVisible());
        Assert.assertEquals(2, button.getNumberOfMessages());
        Assert.assertEquals("2 Messages", button.getText());
    }

    /**
     * 
     */
    @Test
    public void checkGoingInvisibleWhenMessagesRemoved() {
        button.setNumberOfMessages(1);

        button.setNumberOfMessages(0);
        Assert.assertFalse(button.isVisible());
        Assert.assertEquals(0, button.getNumberOfMessages());
        Assert.assertEquals("0 Messages", button.getText());
    }

    private void assertPulsing(final boolean pulsing) {
        final HashSet<Color> backColours = new HashSet<Color>(); 
        
        // run for 4 seconds realtime.
        // changing background color every virtual 250ms.
        // this would be 16 real changes
        // time is 10x faster
        for (int i = 0; i < 16; i++) {
            backColours.add(button.getBackground());
            sleeper.sleep(250);
        }
        
        LOGGER.info("There have been " + backColours.size() + " background colour changes");
        if (pulsing) {
            Assert.assertTrue(backColours.size() > 5); // account for some judder
        } else {
            Assert.assertTrue(backColours.size() < 5); 
        }
    }
    /**
     * 
     */
    @Test(timeout = 8000)
    public void checkPulsingWhenMessagesPresent() {
        button.setNumberOfMessages(1);
        assertPulsing(true);
    }
    
    /**
     * 
     */
    @Test(timeout = 8000)
    public void checkNotPulsingWhenNoMessagesPresent() {
        button.setNumberOfMessages(0);
        assertPulsing(false);
    }
    
    /**
     * 
     */
    @Test(timeout = 16000)
    public void checkStopsPulsingWhenNoMessagesPresent() {
        button.setNumberOfMessages(1);
        assertPulsing(true);
        sleeper.sleep(250);
        button.setNumberOfMessages(0);
        assertPulsing(false);
    }
}
