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
