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

package org.devzendo.minimiser.gui.menu;

import org.devzendo.commoncode.patterns.observer.ObservableEvent;

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
