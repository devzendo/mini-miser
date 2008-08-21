package uk.me.gumbley.minimiser.gui;

/**
 * Controller for displaying the application name, and setting and clearing the
 * current database name in the main frame title bar.
 * 
 * @author matt
 *
 */
public interface MainFrameTitle {

    /**
     * Clear the current database name. getCurrentDatabaseName will return null.
     */
    void clearCurrentDatabaseName();

    /**
     * Set the current database name in the title.
     * @param databaseName the name of the database
     */
    void setCurrentDatabaseName(final String databaseName);

    /**
     * Obtain the current database name 
     * @return the current database name, or null if none is set or if it has
     * been cleared.
     */
    String getCurrentDatabaseName();
}
