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

package org.devzendo.minimiser.gui;

/**
 * Controller for displaying the application name, and setting and clearing the
 * current database name in the main frame title bar.
 * 
 * @author matt
 *
 */
public interface MainFrameTitle {

    /**
     * Set application name.
     * @param applicationName the application name
     */
    void setApplicationName(String applicationName);
    
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
