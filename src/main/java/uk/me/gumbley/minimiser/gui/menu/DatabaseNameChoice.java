package uk.me.gumbley.minimiser.gui.menu;

import uk.me.gumbley.commoncode.patterns.observer.ObservableEvent;

/**
 * A choice of database from the Window menu has been made.
 * 
 * @author matt
 *
 */
public final class DatabaseNameChoice implements ObservableEvent {
    private final String databaseName;

    /**
     * @param dbName the database name that has been chosen
     */
    public DatabaseNameChoice(final String dbName) {
        this.databaseName = dbName;
    }

    /**
     * @return the databaseName
     */
    public String getDatabaseName() {
        return databaseName;
    }
}
