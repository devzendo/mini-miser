package uk.me.gumbley.minimiser.gui;

import uk.me.gumbley.minimiser.util.DelayedExecutor;

/**
 * The logic of some parts of the status bar (those parts that can be done TDD),
 * handling the message.
 * 
 * @author matt
 *
 */
public abstract class AbstractStatusBar implements StatusBar {
    private Object lock;
    private String permanentMessage;
    private String tempMessage;
    private DelayedExecutor delayedExecutor;

    /**
     * Abstract base class constructor 
     * @param exec the Delayed Executor
     */
    public AbstractStatusBar(final DelayedExecutor exec) {
        delayedExecutor = exec;
        lock = new Object();
        synchronized (lock) {
            tempMessage = "";
            permanentMessage = "";
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void clearMessage() {
        synchronized (lock) {
            permanentMessage = "";
        }
        update();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayMessage(final String message) {
        synchronized (lock) {
            permanentMessage = message == null ? "" : message;
        }
        update();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayTemporaryMessage(final String message, final int seconds) {
        synchronized (lock) {
            tempMessage = message;
        }
        update();
        delayedExecutor.submit("AbstractStatusBar", seconds * 1000L, new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    tempMessage = "";
                }
                update();
            }
        });
    }
    
    /**
     * Used by the message timing system to set some text now in the underlying
     * GUI.
     * @param message the text, or "" to clear.
     */
    public abstract void internalSetMessageTextNow(String message);
    
    /**
     * What's the currently-displayed permanent or temporary message?
     * @return the current message.
     */
    private String getDisplayMessage() {
        synchronized (lock) {
            return tempMessage.equals("") ? permanentMessage : tempMessage;
        }
    }
    
    /**
     * Update the display
     */
    private void update() {
        internalSetMessageTextNow(getDisplayMessage());
    }
}
