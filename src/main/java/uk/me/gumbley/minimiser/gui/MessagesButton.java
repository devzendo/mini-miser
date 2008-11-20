package uk.me.gumbley.minimiser.gui;

import java.awt.Color;
import javax.swing.JButton;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.string.StringUtils;
import uk.me.gumbley.minimiser.util.Sleeper;

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
    private volatile int numberOfMessages;
    private final Sleeper sleeper;
    private final Object notification;
    private final Color normalBackground;
    private boolean isMessageViewerShowing;

    /**
     * Construct the MessagesButton
     * @param sleepr the Sleeper to be used to help animate the pulsing button
     */
    public MessagesButton(final Sleeper sleepr) {
        this.sleeper = sleepr;
        numberOfMessages = 0;
        normalBackground = getBackground();
        notification = new Object();
        
        startAnimatorThread();
        
        refreshCurrentState();
    }

    private Thread startAnimatorThread() {
        final Thread animatorThread = new Thread(this);
        animatorThread.setName("Messages Button Animator");
        animatorThread.setDaemon(true);
        animatorThread.start();
        return animatorThread;
    }

    private void refreshCurrentState() {
        setVisible(numberOfMessages > 0 && !isMessageViewerShowing);
        setTextToNumberOfMessages();
        synchronized (notification) {
            notification.notify();
        }
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
        final ColorTransform transform = new ColorTransform(normalBackground, Color.RED);
        final double maxprop = 0.3;
        final long millisUntilChangeText = 3000L;
        final int sleepDelay = 250;
        while (Thread.currentThread().isAlive()) {
            synchronized (notification) {
                try {
                    notification.wait();
                } catch (final InterruptedException e) {
                    LOGGER.warn("Interrupted whilst waiting for pulse notification", e);
                }
            }
            if (numberOfMessages == 0) {
                setBackground(normalBackground);
            } else {
                final double amt = 0.02;
                double prop = 0.0;
                int dx = 1;
                long millisWithThisText = 0L;
                boolean showMessageCount = true;
                while (numberOfMessages != 0) {
                    setBackground(transform.getProportionalColor(prop));
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
                        if (showMessageCount) {
                            setTextToNumberOfMessages();
                        } else {
                            setText("Click to read");
                        }
                    }
                    
                }
            }
        }
    }
}
