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

package org.devzendo.minimiser.plugin.facade.newdatabase;

/**
 * Plugins that want to create database content after the File|New
 * wizard has collected user input must implement this and provide
 * the facade.
 * <p>
 * It is highly likely that such plugins would want to implement
 * DatabaseOpening, as if they create this data, they would want
 * to provide access to it. Also, DatabaseMigration to upgrade
 * the schema between releases.
 * 
 * @author matt
 *
 */
public interface NewDatabaseCreation {
    /**
     * @return an instance of the facade; this can be called
     * several times during creation; perhaps this could be a
     * single instance that's returned?
     */
    NewDatabaseCreationFacade getNewDatabaseCreationFacade();
}
