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
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JButton;

import org.apache.log4j.Logger;
import org.devzendo.commonapp.gui.GUIUtils;
import org.devzendo.commoncode.string.StringUtils;
import org.devzendo.minimiser.util.Sleeper;


/**
 * The Message Button is invisible when there are no messages, and pulses
 * when there are messages added to the Message Queue (and it's notified of
 * this). It's also invisible when the message viewer is showing.
 * @author matt
 *
 */
@SuppressWarnings("serial")
public final class MessagesButton extends JButton implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(MessagesButton.class);
    private final Sleeper sleeper;
    private final LinkedBlockingQueue<Boolean> notification;
    private final Color normalBackground;

    private volatile int numberOfMessages;
    private volatile boolean isMessageViewerShowing;
    private volatile boolean bAlive;
    private Thread animatorThread;

    /**
     * Construct the MessagesButton
     * @param sleepr the Sleeper to be used to help animate the pulsing button
     */
    public MessagesButton(final Sleeper sleepr) {
        this.sleeper = sleepr;
        numberOfMessages = 0;
        normalBackground = getBackground();
        notification = new LinkedBlockingQueue<Boolean>();
        
        startAnimatorThread();
        
        refreshCurrentState();
    }

    private Thread startAnimatorThread() {
        animatorThread = new Thread(this);
        animatorThread.setName("Messages Button Animator");
        animatorThread.setDaemon(true);
        animatorThread.start();
        return animatorThread;
    }

    private void refreshCurrentState() {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                setVisible(numberOfMessages > 0 && !isMessageViewerShowing);
                setTextToNumberOfMessages();
                LOGGER.debug("Notifiying colour&message cycle thread");
                try {
                    notification.put(Boolean.TRUE);
                } catch (final InterruptedException e) {
                    LOGGER.warn("Interrupted whilst queueing pulse notification", e);
                }
            }
        });
    }

    private void setTextToNumberOfMessages() {
        setText("" + numberOfMessages + " " + StringUtils.pluralise("Message", numberOfMessages));
    }
    
    /**
     * How many messages are there?
     * @return the number of messages this button has been notified of
     */
    public int getNumberOfMessages() {
        return numberOfMessages;
    }

    /**
     * Set the number of messages that are in the queue.
     * @param msgs the number of messages
     */
    public void setNumberOfMessages(final int msgs) {
        numberOfMessages = msgs;
        refreshCurrentState();
    }

    /**
     * Set and cleared when the message queue viewer window is shown and closed,
     * this is used to prevent the message queue indicator from displaying
     * when the viewer is present. 
     * @param showing true iff the viewer is showing.
     */
    public void setMessageQueueViewerShowing(final boolean showing) {
        isMessageViewerShowing = showing;
        refreshCurrentState();
    }

    /**
     * Animate the messages button's background colour and text when there
     * are messages.
     * {@inheritDoc}
     */
    public void run() {
        LOGGER.debug("Started colour & message cycle thread");
        final ColorTransform transform = new ColorTransform(normalBackground, Color.RED);
        final double maxprop = 0.3;
        final long millisUntilChangeText = 3000L;
        final int sleepDelay = 250;
        bAlive = true;
        while (bAlive && Thread.currentThread().isAlive()) {
            LOGGER.debug("Colour & message cycle thread waiting for notification");
            synchronized (notification) {
                try {
                    notification.take();
                } catch (final InterruptedException e) {
                    LOGGER.warn("Interrupted whilst waiting for pulse notification", e);
                }
            }
            LOGGER.debug("Colour & message cycle thread out of wait");
            if (numberOfMessages == 0) {
                LOGGER.debug("No messages");
                GUIUtils.runOnEventThread(new Runnable() {
                    public void run() {
                        setBackground(normalBackground);
                    }
                });
            } else {
                LOGGER.debug("Cycling...");
                cycle(transform, maxprop, millisUntilChangeText, sleepDelay);
            }
        }
    }

    private void cycle(
            final ColorTransform transform,
            final double maxprop,
            final long millisUntilChangeText,
            final int sleepDelay) {
        final double amt = 0.02;
        double prop = 0.0;
        int dx = 1;
        long millisWithThisText = 0L;
        boolean showMessageCount = true;
        while (bAlive && Thread.currentThread().isAlive() && numberOfMessages != 0) {
            final double finalProp = prop;
            GUIUtils.runOnEventThread(new Runnable() {
                public void run() {
                    setBackground(transform.getProportionalColor(finalProp));
                }
            });
            prop = prop + (dx * amt);
            if (prop <= 0.0) {
                prop = 0.0;
                dx = -dx;
            } else if (prop > maxprop) {
                prop = maxprop;
                dx = -dx;
            }
            
            sleeper.sleep(sleepDelay);
            
            millisWithThisText += sleepDelay;
            if (millisWithThisText > millisUntilChangeText) {
                millisWithThisText = 0L;
                showMessageCount = !showMessageCount;
                final boolean finalShowMessageCount = showMessageCount;
                GUIUtils.runOnEventThread(new Runnable() {
                    public void run() {
                        if (finalShowMessageCount) {
                            setTextToNumberOfMessages();
                        } else {
                            setText("Click to read");
                        }
                    }
                });
            }
            
        }
    }

    /**
     * For use in unit tests - kill the animator. Die, Disney!
     */
    public void interruptThread() {
        bAlive = false;
        animatorThread.interrupt();
    }
}
