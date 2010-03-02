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
import org.devzendo.minimiser.gui.menu.Menu;
import org.devzendo.minimiser.gui.menu.MenuWiringAdapter;
import org.devzendo.minimiser.recentlist.RecentFilesList;
import org.devzendo.minimiser.recentlist.RecentListChangedEvent;


/**
 * Adapts between recent list updates and the menu, to update the recent
 * list menu.
 * @author matt
 *
 */
public final class MenuRecentListEventObserver implements MenuWiringAdapter, Observer<RecentListChangedEvent> {
    private final Menu menu;
    private final RecentFilesList recentFilesList;

    /**
     * Construct the adapter given other system objects for interaction.
     * @param leMenu the menu
     * @param recents the recent file list
     */
    public MenuRecentListEventObserver(
            final Menu leMenu,
            final RecentFilesList recents) {
                this.menu = leMenu;
                this.recentFilesList = recents;
    }
    
    /**
     * {@inheritDoc}
     */
    public void connectWiring() {
        // recent list -> menu
        recentFilesList.addRecentListEventObserver(this);
    }
    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final RecentListChangedEvent observableEvent) {
        menu.refreshRecentList(recentFilesList.getRecentDatabases());
    }
}
