package org.devzendo.minimiser.persistence;

import uk.me.gumbley.commoncode.patterns.observer.ObservableEvent;

/**
 * An event that occurs when databases are opened or created.
 * 
 * @author matt
 *
 */
public class PersistenceObservableEvent implements ObservableEvent {
    private String description;

    /**
     * Create the event with a description
     * @param desc the description
     */
    public PersistenceObservableEvent(final String desc) {
        description = desc;
    }

    /**
     * @return the event description
     */
    public final String getDescription() {
        return description;
    }
}
