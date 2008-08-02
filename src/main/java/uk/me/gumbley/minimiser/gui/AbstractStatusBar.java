package uk.me.gumbley.minimiser.gui;

/**
 * The logic of some parts of the status bar (those parts that can be done TDD),
 * handling the message.
 * 
 * @author matt
 *
 */
public abstract class AbstractStatusBar implements StatusBar {
    /**
     * {@inheritDoc}
     */
    @Override
    public void clearMessage() {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayMessage(final String message) {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayTemporaryMessage(final String message, final int seconds) {
        // TODO Auto-generated method stub
    }
    
    /**
     * Used by the message timing system to set some text now in the underlying
     * GUI.
     * @param message the text, or "" to clear.
     */
    public abstract void internalSetMessageTextNow(String message);
}
