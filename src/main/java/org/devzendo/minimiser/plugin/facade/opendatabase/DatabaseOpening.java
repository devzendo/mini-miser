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

package org.devzendo.minimiser.plugin.facade.opendatabase;


/**
 * Plugins that persist their own data and need a DAOFactory in
 * order to obtain access to it must implement this and provide
 * the facade.
 * <p>
 * It is highly likely that such plugins would also implement
 * NewDatabaseCreation - as they would want to create this data.
 * 
 * @author matt
 *
 */
public interface DatabaseOpening {
    /**
     * @return an instance of the facade; this can be called
     * several times during opening; perhaps this could be a
     * single instance that's returned?
     */
    DatabaseOpeningFacade getDatabaseOpeningFacade();
}
