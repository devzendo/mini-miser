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
import org.devzendo.minimiser.gui.menu.MenuWiringAdapter;
import org.devzendo.minimiser.opener.DatabaseOpenEvent;
import org.devzendo.minimiser.opener.Opener;
import org.devzendo.minimiser.openlist.OpenDatabaseList;


/**
 * Adapts between the Opener opening a database and it being added to the
 * OpenDatabaseList.
 * @author matt
 *
 */
public final class MenuDatabaseOpenEventObserver implements MenuWiringAdapter, Observer<DatabaseOpenEvent> {
    
    private final OpenDatabaseList openDatabaseList;
    private final Opener opener;

    /**
     * Construct the adapter given other system objects for interaction.
     * @param databaseList the open database list
     * @param openr the opener
     */
    public MenuDatabaseOpenEventObserver(
            final OpenDatabaseList databaseList,
            final Opener openr) {
                this.openDatabaseList = databaseList;
                this.opener = openr;
    }
    
    /**
     * {@inheritDoc}
     */
    public void connectWiring() {
        // opener -> ODL
        opener.addDatabaseOpenObserver(this);
    }
    
    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final DatabaseOpenEvent observableEvent) {
        openDatabaseList.addOpenedDatabase(observableEvent.getDatabase());
    }

}
