package uk.me.gumbley.minimiser.gui.lifecycle;

import uk.me.gumbley.minimiser.lifecycle.Lifecycle;
import uk.me.gumbley.minimiser.util.DelayedExecutor;

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
