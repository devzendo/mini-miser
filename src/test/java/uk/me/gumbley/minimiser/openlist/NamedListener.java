package uk.me.gumbley.minimiser.openlist;

import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.util.OrderMonitor;

/**
 * A database event listener that has a name and records its event occurrences.
 * 
 * @author matt
 *
 */
public final class NamedListener implements Observer<DatabaseEvent> {
    private final OrderMonitor orderMonitor;
    private final String listenerName;

    /**
     * Construct a NamedListener
     * @param monitor the OrderMonitor to use for recording
     * @param name the name of this Listener
     */
    public NamedListener(final OrderMonitor monitor, final String name) {
        this.orderMonitor = monitor;
        this.listenerName = name;
        if (orderMonitor == null) {
            throw new IllegalArgumentException("no ordermonitor!");
        }
        if (listenerName == null) {
            throw new IllegalArgumentException("no name!");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final DatabaseEvent observableEvent) {
        orderMonitor.add(listenerName + ' ' + observableEvent.getClass().getSimpleName());
    }
}
