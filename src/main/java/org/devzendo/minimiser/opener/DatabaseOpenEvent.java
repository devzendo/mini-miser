package org.devzendo.minimiser.opener;

import org.devzendo.commoncode.patterns.observer.ObservableEvent;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;


/**
 * A database has been opened, this is the notification you receive from the 
 * Opener, if you're an observer.
 * 
 * @author matt
 *
 */
public final class DatabaseOpenEvent implements ObservableEvent {
    private final DatabaseDescriptor database;

    /**
     * Create a DatabaseOpenEvent containing a specific database.
     * @param db the DatabaseDescriptor for the newly opened database
     */
    public DatabaseOpenEvent(final DatabaseDescriptor db) {
        database = db;
    }

    /**
     * Get the database
     * @return the database descriptor
     */
    public DatabaseDescriptor getDatabase() {
        return database;
    }
}
