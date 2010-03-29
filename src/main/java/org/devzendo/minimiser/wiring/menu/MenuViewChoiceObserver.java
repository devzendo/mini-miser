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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.gui.menu.Menu;
import org.devzendo.minimiser.gui.menu.MenuWiringAdapter;
import org.devzendo.minimiser.gui.menu.ViewMenuChoice;
import org.devzendo.minimiser.gui.tab.TabIdentifier;
import org.devzendo.minimiser.gui.tabfactory.TabFactory;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.opentablist.OpenTabList;
import org.devzendo.minimiser.opentablist.TabDescriptor;
import org.devzendo.minimiser.tabcontroller.TabController;


/**
 * Adapts between view menu and tab opener to open/close tabs.
 *
 * @author matt
 *
 */
public final class MenuViewChoiceObserver implements MenuWiringAdapter, Observer<ViewMenuChoice> {
    private static final Logger LOGGER = Logger
        .getLogger(MenuViewChoiceObserver.class);

    private final Menu mMenu;
    private final OpenTabList mOpenTabList;
    private final TabFactory mTabFactory;
    private final TabController mTabController;

    /**
     * Construct the adapter given other system objects for interaction.
     * @param menu the menu
     * @param openTabList the open tab list
     * @param tabFactory the tab factory
     * @param tabController the tab controller
     */
    public MenuViewChoiceObserver(
            final Menu menu,
            final OpenTabList openTabList,
            final TabFactory tabFactory,
            final TabController tabController) {
        mMenu = menu;
        mOpenTabList = openTabList;
        mTabFactory = tabFactory;
        mTabController = tabController;
    }

    /**
     * {@inheritDoc}
     */
    public void connectWiring() {
        // menu -> tab opener (the view menu)
        mMenu.addViewChoiceObserver(this);
    }

    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final ViewMenuChoice observableEvent) {
        LOGGER.debug((observableEvent.isOpened() ? "Opening" : "Closing")
            + " tab ID " + observableEvent.getTabId() + " for database "
            + observableEvent.getDatabaseDescriptor());
        if (observableEvent.isOpened()) {
            openTab(observableEvent);
        } else {
            closeTab(observableEvent);
        }
    }

    private void openTab(final ViewMenuChoice observableEvent) {
        final List<TabIdentifier> tabListOfOne = new ArrayList<TabIdentifier>();
        tabListOfOne.add(observableEvent.getTabId());
        final DatabaseDescriptor databaseDescriptor = observableEvent.getDatabaseDescriptor();
        final List<TabDescriptor> loadedTab = mTabFactory.loadTabs(databaseDescriptor, tabListOfOne);

        final TabDescriptor tabDescriptor = loadedTab.get(0);
        mTabController.addTabToTabbedPaneAndOpenTabList(databaseDescriptor, tabDescriptor);
        mTabController.switchToTab(databaseDescriptor, tabDescriptor);
    }

    private void closeTab(final ViewMenuChoice observableEvent) {
        // find this tab descriptor
        final DatabaseDescriptor databaseDescriptor = observableEvent.getDatabaseDescriptor();
        final TabIdentifier tabIdentifier = observableEvent.getTabId();

        final List<TabDescriptor> tabsForDatabase = mOpenTabList.getTabsForDatabase(databaseDescriptor.getDatabaseName());
        final List<TabDescriptor> tabListToClose = new ArrayList<TabDescriptor>();
        for (final TabDescriptor tabDescriptor : tabsForDatabase) {
            if (tabDescriptor.getTabIdentifier().equals(tabIdentifier)) {
                tabListToClose.add(tabDescriptor);
            }
        }

        if (tabListToClose.size() > 0) {
            mTabController.removeTabFromTabbedPaneAndOpenTabList(observableEvent.getDatabaseDescriptor(), tabListToClose.get(0));

            mTabFactory.closeTabs(databaseDescriptor, tabListToClose);
        }
    }
}
