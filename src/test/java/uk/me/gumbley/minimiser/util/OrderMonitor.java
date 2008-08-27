package uk.me.gumbley.minimiser.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Detector for ordering of calls.
 * 
 * @author matt
 *
 */
public final class OrderMonitor {
    private List<String> ordering;
    private Object lock;
    /**
     * Make an order monitor
     */
    public OrderMonitor() {
        lock = new Object();
        synchronized (lock) {
            ordering = new ArrayList<String>();
        }
    }

    /**
     * @param string a string to add to the ordering
     */
    public void add(final String string) {
        synchronized (lock) {
            ordering.add(string);
        }
    }

    /**
     * @return a list of the current orderings
     */
    public List<String> getOrdering() {
        synchronized (lock) {
            return new ArrayList<String>(ordering);
        }
    }
    
    /**
     * Reset the list of orderings
     */
    public void reset() {
        synchronized (lock) {
            ordering.clear();
        }
    }
}
