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

import java.awt.Color;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.logging.LoggingUnittestHelper;
import org.devzendo.commoncode.time.Sleeper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;



/**
 * Tests the MessageButton
 * @author matt
 *
 */
public final class TestMessagesButton {
    private static final Logger LOGGER = Logger
            .getLogger(TestMessagesButton.class);
    private MessagesButton button;
    private Sleeper sleeper; 
        
    /**
     * 
     */
    @BeforeClass
    public static void setupLogging() {
        LoggingUnittestHelper.setupLogging();
    }

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
