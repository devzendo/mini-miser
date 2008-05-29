package uk.me.gumbley.minimiser.gui.mm;

import uk.me.gumbley.commoncode.patterns.observer.ObservableEvent;

public class WindowMenuChoice implements ObservableEvent {
    private final String databaseName;

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
