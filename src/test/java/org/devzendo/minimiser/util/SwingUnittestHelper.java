package org.devzendo.minimiser.util;

import java.awt.EventQueue;

import org.devzendo.commoncode.concurrency.ThreadUtils;


/**
 * Helper code for ensuring the Swing/AWT event queue is active
 * before a test, and flushed afterwards.
 * 
 * @author matt
 *
 */
public final class SwingUnittestHelper {
    private SwingUnittestHelper() {
    }

    /**
     * Queue an empty Runnable to ensure the event queue is running. 
     */
    public static void ensureEventThreadStarted() {
        waitForQueue();
    }

    private static void waitForQueue() {
        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                }
            });
        } catch (final Exception e) {
        }
    }

    /**
     * Wait for a second then queue a final empty Runnable to wait for the
     * final event to be dispatched.
     */
    public static void ensureEventThreadFlushed() {
        ThreadUtils.waitNoInterruption(1000L);
        waitForQueue();
    }
}
