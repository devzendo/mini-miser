package uk.me.gumbley.minimiser.gui.mm;

import uk.me.gumbley.commoncode.patterns.observer.ObservableEvent;

/**
 * A choice from the Window menu has been made.
 * @author matt
 *
 */
public final class WindowMenuChoice implements ObservableEvent {
    private final String databaseName;

    /**
     * @param dbName the database name that has been chosen
     */
    public WindowMenuChoice(final String dbName) {
        this.databaseName = dbName;
    }

    /**
     * @return the databaseName
     */
    public String getDatabaseName() {
        return databaseName;
    }
}
