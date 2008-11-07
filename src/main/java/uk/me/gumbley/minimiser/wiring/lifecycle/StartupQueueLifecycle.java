package uk.me.gumbley.minimiser.wiring.lifecycle;

import uk.me.gumbley.minimiser.lifecycle.Lifecycle;
import uk.me.gumbley.minimiser.startupqueue.StartupQueue;

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
