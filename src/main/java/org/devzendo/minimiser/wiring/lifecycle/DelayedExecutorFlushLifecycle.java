package org.devzendo.minimiser.wiring.lifecycle;

import org.devzendo.minimiser.lifecycle.Lifecycle;
import org.devzendo.minimiser.util.DelayedExecutor;

/**
 * A Lifecycle that flushes any remaining tasks on the DelayedExecutor.
 * @author matt
 *
 */
public final class DelayedExecutorFlushLifecycle implements Lifecycle {
    private final DelayedExecutor delayedExecutor;

    /**
     * Construct
     * @param executor th edelayed executor
     */
    public DelayedExecutorFlushLifecycle(final DelayedExecutor executor) {
        this.delayedExecutor = executor;
        
    }
    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        // TODO write the flushing functionality
        // delayedExecutor.flush();
        delayedExecutor.toString(); // no checkstyle warning please Bob
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        // do nothing
    }
}
