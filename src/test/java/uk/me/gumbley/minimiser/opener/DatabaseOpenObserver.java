/**
 * 
 */
package uk.me.gumbley.minimiser.opener;

import org.junit.Assert;

import uk.me.gumbley.commoncode.patterns.observer.Observer;

/**
 * An observer of database openings.
 * 
 * @author matt
 *
 */
public final class DatabaseOpenObserver implements Observer<DatabaseOpenEvent> {
    private boolean databaseOpen = false;
    private DatabaseOpenEvent databaseOpenEvent = null;
    
    /**
     * Assert that the database has been opened.
     */
    public void assertDatabaseOpen() {
        Assert.assertTrue(databaseOpen);
    }
    
    /**
     * Assert that the database has not been opened 
     */
    public void assertDatabaseNotOpen() {
        Assert.assertFalse(databaseOpen);
    }
    
    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final DatabaseOpenEvent observableEvent) {
        databaseOpen = true;
        databaseOpenEvent = observableEvent;
    }
    
    /**
     * @return the databaseOpenEvent
     */
    public DatabaseOpenEvent getDatabaseOpenEvent() {
        return databaseOpenEvent;
    }
}