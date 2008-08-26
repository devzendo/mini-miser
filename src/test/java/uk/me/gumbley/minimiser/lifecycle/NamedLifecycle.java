package uk.me.gumbley.minimiser.lifecycle;

import uk.me.gumbley.minimiser.util.OrderMonitor;

/**
 * A lifecycle that has a name and records its startup and shutdown.
 * @author matt
 *
 */
public final class NamedLifecycle implements Lifecycle {
    private final String lifecycleName;
    private final OrderMonitor orderMonitor;

    /**
     * Construct a NamedLifecycle
     * @param monitor the OtrderMonitor to use for recording
     * @param name the name of this Lifecycle
     */
    public NamedLifecycle(final OrderMonitor monitor, final String name) {
        this.orderMonitor = monitor;
        this.lifecycleName = name;
        if (orderMonitor == null) {
            throw new IllegalArgumentException("no ordermonitor!");
        }
        if (lifecycleName == null) {
            throw new IllegalArgumentException("no name!");
        }
    }
    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        orderMonitor.add(lifecycleName + " shutdown");
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        orderMonitor.add(lifecycleName + " startup");
    }
}
