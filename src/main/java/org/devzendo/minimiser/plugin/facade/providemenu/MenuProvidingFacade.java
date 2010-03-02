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

package org.devzendo.minimiser.plugin.facade.providemenu;

import org.devzendo.minimiser.gui.menu.ApplicationMenu;
import org.devzendo.minimiser.gui.menu.MenuFacade;
import org.devzendo.minimiser.openlist.OpenDatabaseList;

/**
 * Facade provided to allow application plugins to customise the menu.
 *
 * @author matt
 *
 */
public interface MenuProvidingFacade {
    /**
     * Allow the plugin to populate the global (non-database-specific) ApplicationMenu,
     * wire itself to the OpenDatabaseList to be notified of database opening/closing (so
     * as to populate a database-specific ApplicationMenu or enable/disable menu items
     * based on the number of databases open), and to demand a rebuild of the menu.
     *
     * @param globalApplicationMenu the global ApplicationMenu that's not database-
     * specific
     * @param openDatabaseList the OpenDatabaseList to which the plugin can be added
     * as an Observer<DatabaseEvent>
     * @param menuFacade the Menu which can be rebuilt from system menu items, the global
     * ApplicationMenu, and the current database-specific ApplicationMenu on demand.
     */
    void initialise(
            ApplicationMenu globalApplicationMenu,
            OpenDatabaseList openDatabaseList,
            MenuFacade menuFacade);
}
