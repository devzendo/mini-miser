package uk.me.gumbley.minimiser.gui.odl;

/**
 * The last database has been closed, so now no database detail should be shown,
 * this is the notification you receive from the OpenDatabaseList, if you're an observer.
 * 
 * @author matt
 *
 */
public final class DatabaseListEmptyEvent extends DatabaseEvent {
    /**
     * Create a DatabaseListEmptyEvent
     */
    public DatabaseListEmptyEvent() {
        super();
    }
}
