package uk.me.gumbley.minimiser.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import uk.me.gumbley.minimiser.util.DelayedExecutor;

/**
 * A StatusBar that has no GUI, but can be probed via tests.
 * @author matt
 *
 */
public final class HeadlessStatusBar extends AbstractStatusBar {
    private String displayedMessage = "";
    private ActionListener launchMessageViewerActionListener;
    
    /**
     * Construct with no head.
     * @param exec the Delayed Executor
     */
    public HeadlessStatusBar(final DelayedExecutor exec) {
        super(exec);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void internalSetMessageTextNow(final String message) {
        displayedMessage = message;
    }

    /**
     * {@inheritDoc}
     */
    public void clearProgress() {
    }

    /**
     * {@inheritDoc}
     */
    public void setEncryptedIndicator(final boolean encrypted) {
    }

    /**
     * {@inheritDoc}
     */
    public void setProgressLength(final int max) {
    }

    /**
     * {@inheritDoc}
     */
    public void setProgressStep(final int step) {
    }

    /**
     * For tests, what's currently being displayed?
     * @return the string
     */
    public String getDisplayedMessage() {
        return displayedMessage;
    }

    /**
     * Is the message queue indicator enabled?
     * @return true iff there are queued messages
     */
    public boolean isMessageQueueIndicatorEnabled() {
        return getNumberOfQueuedMessages() > 0 && !isMessageQueueViewerShowing();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addLaunchMessageQueueActionListener(final ActionListener listener) {
        launchMessageViewerActionListener = listener;
    }
    
    /**
     * Allow tests to press the message queue indicator button
     */
    public void internalTriggerLaunchMessageQueueActionListener() {
        launchMessageViewerActionListener.actionPerformed(new ActionEvent(this, 0, "click"));
    }
}
