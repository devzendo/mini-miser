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
