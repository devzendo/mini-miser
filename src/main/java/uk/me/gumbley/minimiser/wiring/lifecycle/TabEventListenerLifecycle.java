package uk.me.gumbley.minimiser.wiring.lifecycle;

import uk.me.gumbley.minimiser.lifecycle.Lifecycle;
import uk.me.gumbley.minimiser.opentablist.TabEventListenerManager;

/**
 * A Lifecycle that wires and unwires the TabEventListenerManager on
 * startup and shutdown.
 * 
 * @author matt
 *
 */
public final class TabEventListenerLifecycle implements Lifecycle {
    
    private final TabEventListenerManager tabEventListenerManager;

    /**
     * Create the TabEventListenerManagerLifecycle, given the
     * TabEventListenerManager to wire/unwire.
     * 
     * @param manager the TabEventListenerManager
     */
    public TabEventListenerLifecycle(final TabEventListenerManager manager) {
        this.tabEventListenerManager = manager;
    }
    
    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        tabEventListenerManager.unwire();
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        tabEventListenerManager.wire();
    }
}
