package uk.me.gumbley.minimiser.gui.menu;

import uk.me.gumbley.commoncode.patterns.observer.ObservableEvent;

/**
 * A choice of database from the Open Recent submenu has been made.
 * 
 * @author matt
 *
 */
public final class DatabaseNameAndPathChoice implements ObservableEvent {
    private final String databaseName;
    private final String databasePath;

    /**
     * @param dbName the database name that has been chosen
     * @param dbPath the database path that has been chosen
     */
    public DatabaseNameAndPathChoice(final String dbName, final String dbPath) {
        this.databaseName = dbName;
        this.databasePath = dbPath;
    }

    /**
     * @return the database name
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * @return the database path
     */
    public String getDatabasePath() {
        return databasePath;
    }
}
