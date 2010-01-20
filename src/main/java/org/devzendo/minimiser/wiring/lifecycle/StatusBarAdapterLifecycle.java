package org.devzendo.minimiser.wiring.lifecycle;

import org.devzendo.minimiser.gui.StatusBarMessageQueueAdapter;
import org.devzendo.minimiser.lifecycle.Lifecycle;

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
