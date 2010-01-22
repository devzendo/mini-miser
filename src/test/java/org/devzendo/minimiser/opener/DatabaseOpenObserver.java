/**
 * 
 */
package org.devzendo.minimiser.opener;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.junit.Assert;


/**
 * An observer of database openings.
 * 
 * @author matt
 *
 */
public final class DatabaseOpenObserver implements Observer<DatabaseOpenEvent> {
    private static final Logger LOGGER = Logger
            .getLogger(DatabaseOpenObserver.class);
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
        LOGGER.info("The database is open");
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