/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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