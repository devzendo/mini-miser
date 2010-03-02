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

package org.devzendo.minimiser.wiring.menu;

import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.gui.menu.DatabaseNameChoice;
import org.devzendo.minimiser.gui.menu.Menu;
import org.devzendo.minimiser.gui.menu.MenuWiringAdapter;
import org.devzendo.minimiser.openlist.OpenDatabaseList;


/**
 * Adapts between window menu changes and open database switching
 * 
 * @author matt
 *
 */
public final class MenuDatabaseSwitchObserver implements MenuWiringAdapter, Observer<DatabaseNameChoice> {

    private final Menu menu;
    private final OpenDatabaseList openDatabaseList;
    
    /**
     * Construct the adapter given other system objects for interaction.
     * @param leMenu the menu
     * @param databaseList the open database list
     */
    public MenuDatabaseSwitchObserver(
            final Menu leMenu,
            final OpenDatabaseList databaseList) {
        this.menu = leMenu;
        this.openDatabaseList = databaseList;
    }

    /**
     * {@inheritDoc}
     */
    public void connectWiring() {
        // menu -> ODL (which'll talk back to the menu)
        menu.addDatabaseSwitchObserver(this);
    }
    
    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final DatabaseNameChoice windowMenuChoice) {
        final String databaseName = windowMenuChoice.getDatabaseName();
        openDatabaseList.switchDatabase(databaseName);
    }
}
