package uk.me.gumbley.minimiser.gui;

import uk.me.gumbley.minimiser.util.DelayedExecutor;

/**
 * A StatusBar that has no GUI, but can be probed via tests.
 * @author matt
 *
 */
public class HeadlessStatusBar extends AbstractStatusBar {
    private String displayedMessage = "";
    
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
    @Override
    public void clearProgress() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEncryptedIndicator(final boolean encrypted) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProgressLength(final int max) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProgressStep(final int step) {
    }

    /**
     * For tests, what's currently being displayed?
     * @return the string
     */
    public String getDisplayedMessage() {
        return displayedMessage;
    }
}
