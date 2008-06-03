package uk.me.gumbley.minimiser.util;

/**
 * Allows Runnables to be enqueued and run after some specified delay.
 * Runnables are submitted with a key, and if many are submitted with the
 * same key, the last one submitted is executed (at the end of the delay).
 * 
 * This is useful to prevent firestorms of execution - e.g. window movements
 * causing the current geometry to be stored in prefs.
 * 
 * @author matt
 *
 */
public class DelayedExecutor {
    // WOZERE
}
