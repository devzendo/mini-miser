package uk.me.gumbley.minimiser.persistence;

import uk.me.gumbley.commoncode.patterns.observer.ObservableEvent;

/**
 * An event that occurs when databases are opened or created.
 * 
 * @author matt
 *
 */
public class PersistenceObservableEvent implements ObservableEvent {
    private String description;

    public PersistenceObservableEvent(final String desc) {
        description = desc;
    }

    public String getDescription() {
        return description;
    }
}
