package uk.me.gumbley.minimiser.gui;

/**
 * A StatusBar that has no GUI, but can be probed via tests.
 * @author matt
 *
 */
public class HeadlessStatusBar extends AbstractStatusBar {
    private String message;
    
    public HeadlessStatusBar() {
        message = "";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void internalSetMessageTextNow(final String message) {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearProgress() {
        // TODO Auto-generated method stub
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
     * @return the internal message that is "being displayed right now"
     */
    public String getInternalMessage() {
        return message;
    }
}
