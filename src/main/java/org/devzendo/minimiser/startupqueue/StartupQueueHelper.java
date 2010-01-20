package org.devzendo.minimiser.startupqueue;

import org.devzendo.minimiser.springloader.SpringLoader;

/**
 * Toolkit for easily adding Runnables to the StartupQueue..
 * 
 * @author matt
 *
 */
public final class StartupQueueHelper {
    private static StartupQueue startupQueue;

    /**
     * 
     */
    private StartupQueueHelper() {
        // no instances
    }
    
    /**
     * Initialise the toolkit by obtaining the things it needs to add Runnables
     * to the Startup Queue.
     * 
     * @param springLoader the spring loader
     */
    public static void initialise(final SpringLoader springLoader) {
        startupQueue = springLoader.getBean("startupQueue", StartupQueue.class);
    }

    /**
     * @see StartupQueue.addRunnable(Runnable)
     * @param runnable the Runnable to add.
     */
    public static void addRunnable(final Runnable runnable) {
        startupQueue.addRunnable(runnable);
    }
}
