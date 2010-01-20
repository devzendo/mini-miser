package org.devzendo.minimiser.wiring.lifecycle;

import org.devzendo.minimiser.lifecycle.Lifecycle;
import org.devzendo.minimiser.startupqueue.StartupQueue;

/**
 * A Lifecycle that executes the StartupQueue on startup.
 * This is configured to run LAST in the lifecycle list, so that it'll run
 * after any databases are open.
 * 
 * @author matt
 *
 */
public final class StartupQueueLifecycle implements Lifecycle {
    
    private final StartupQueue startupQueue;

    /**
     * Create the StartupQueueLifecycle, given the
     * StartupQueue to execute startup tasks.
     * 
     * @param sq the StartupQueue
     */
    public StartupQueueLifecycle(final StartupQueue sq) {
        this.startupQueue = sq;
    }
    
    /**
     * {@inheritDoc}
     */
    public void shutdown() {
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        startupQueue.executeQueue();
    }
}
