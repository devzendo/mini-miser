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

package org.devzendo.minimiser.wiring.databaseeventlistener;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.gui.tabpanemanager.TabPaneManager;
import org.devzendo.minimiser.openlist.DatabaseClosedEvent;
import org.devzendo.minimiser.openlist.DatabaseEvent;
import org.devzendo.minimiser.openlist.DatabaseListEmptyEvent;
import org.devzendo.minimiser.openlist.DatabaseOpenedEvent;
import org.devzendo.minimiser.openlist.DatabaseSwitchedEvent;


/**
 * Adapts between database events and the tab pane manager's card layout.
 * 
 * @author matt
 *
 */
public final class TabPaneManagerDatabaseEventListener implements Observer<DatabaseEvent> {

    private static final Logger LOGGER = Logger
            .getLogger(TabPaneManagerDatabaseEventListener.class);
    private final TabPaneManager paneManager;

    /**
     * Construct an adapter between database events and the tab pane manager
     * @param tabPaneManager the tab pane manager.
     */
    public TabPaneManagerDatabaseEventListener(final TabPaneManager tabPaneManager) {
        this.paneManager = tabPaneManager;
    }

    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final DatabaseEvent databaseEvent) {
        LOGGER.info("Received a " + databaseEvent.getClass().getSimpleName());
        if (databaseEvent instanceof DatabaseOpenedEvent) {
            final DatabaseOpenedEvent openEvent = (DatabaseOpenedEvent) databaseEvent;
            paneManager.addTabPane(openEvent.getDatabaseDescriptor());
        } else if (databaseEvent instanceof DatabaseClosedEvent) {
            final DatabaseClosedEvent closedEvent = (DatabaseClosedEvent) databaseEvent;
            paneManager.removeTabPane(closedEvent.getDatabaseDescriptor());
        } else if (databaseEvent instanceof DatabaseListEmptyEvent) {
            paneManager.hideTabPanes();
        } else if (databaseEvent instanceof DatabaseSwitchedEvent) {
            final DatabaseSwitchedEvent switchedEvent = (DatabaseSwitchedEvent) databaseEvent;
            paneManager.switchToTabPane(switchedEvent.getDatabaseDescriptor());
        }
    }
}
