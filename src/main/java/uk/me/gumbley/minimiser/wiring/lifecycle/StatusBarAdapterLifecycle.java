package uk.me.gumbley.minimiser.wiring.lifecycle;

import uk.me.gumbley.minimiser.gui.StatusBarMessageQueueAdapter;
import uk.me.gumbley.minimiser.lifecycle.Lifecycle;

/**
 * A Lifecycle that receives the adapter between Status Bar and Message Queue,
 * and initialises it.
 * 
 * @author matt
 *
 */
public final class StatusBarAdapterLifecycle implements Lifecycle {
    
   
    private final StatusBarMessageQueueAdapter statusBarMessageQueueAdapter;

    /**
     * Create the StatusBarAdapterLifecycle, given the
     * StatusBarMessageQueueAdapter.
     * 
     * @param adapter the StatusBarMessageQueueAdapter
     */
    public StatusBarAdapterLifecycle(final StatusBarMessageQueueAdapter adapter) {
        this.statusBarMessageQueueAdapter = adapter;
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
        statusBarMessageQueueAdapter.wireAdapter();
    }
}
