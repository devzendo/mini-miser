package org.devzendo.minimiser.opener;


/**
 * A Factory for creating OpenerAdapters.
 * <p>
 * This is used by the MenuMediator to create an OpenerAdapter at runtime
 * that can report progress and failures for a particular database.
 * <p>
 * There are two main implementors; one that generates "proper" ones, and one
 * that generates stub ones for testing.
 *  
 * @author matt
 *
 */
public interface OpenerAdapterFactory {
    /**
     * Create an OpenerAdapter that can monitor progress for a specific database
     * and display problems over a main window frame.
     * @param databaseName the name of the database.
     * @return the OpenerAdapter
     */
    OpenerAdapter createOpenerAdapter(final String databaseName);
}
