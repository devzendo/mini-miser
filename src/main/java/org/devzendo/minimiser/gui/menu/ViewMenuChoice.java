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
import org.devzendo.minimiser.gui.tab.TabIdentifier;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;


/**
 * A choice of view tab from the View menu has been made.
 * TODO: WOZERE will probably need the database and tab descriptor here?
 * 
 * @author matt
 *
 */
public final class ViewMenuChoice implements ObservableEvent {
    private final DatabaseDescriptor databaseDescriptor;
    private final TabIdentifier tabId;
    private final boolean opened;

    /**
     * @param database the descriptor of the database that is having
     * this tab opened or closed
     * @param tabIdentifier the tab that's been chosen
     * @param open true iff this tab has been opened
     */
    public ViewMenuChoice(final DatabaseDescriptor database, final TabIdentifier tabIdentifier, final boolean open) {
        databaseDescriptor = database;
        tabId = tabIdentifier;
        opened = open;
    }

    /**
     * @return the database descriptor that will have a tab opened or closed
     */
    public DatabaseDescriptor getDatabaseDescriptor() {
        return databaseDescriptor;
    }

    /**
     * @return the tab identifier to open or close
     */
    public TabIdentifier getTabId() {
        return tabId;
    }

    /**
     * @return true iff this tab has been opened
     */
    public boolean isOpened() {
        return opened;
    }
}
