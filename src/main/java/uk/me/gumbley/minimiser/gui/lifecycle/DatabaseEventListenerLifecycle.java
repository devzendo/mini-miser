package uk.me.gumbley.minimiser.gui.lifecycle;

import uk.me.gumbley.minimiser.lifecycle.Lifecycle;
import uk.me.gumbley.minimiser.openlist.DatabaseEventListenerManager;

/**
 * A Lifecycle that wires and unwires the DatabaseEventListenerManager on
 * startup and shutdown.
 * 
 * @author matt
 *
 */
public final class DatabaseEventListenerLifecycle implements Lifecycle {
    
    private final DatabaseEventListenerManager databaseEventListenerManager;

    /**
     * Create the DatabaseEventListenerLifecycle, given the
     * DatabaseEventListenerManager to wire/unwire.
     * 
     * @param manager the DatabaseEventListenerManager
     */
    public DatabaseEventListenerLifecycle(final DatabaseEventListenerManager manager) {
        this.databaseEventListenerManager = manager;
    }
    
    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        databaseEventListenerManager.unwire();
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        databaseEventListenerManager.wire();
    }
}
