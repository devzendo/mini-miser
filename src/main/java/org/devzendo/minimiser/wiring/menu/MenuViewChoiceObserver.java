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
import java.util.Arrays;
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
        final DatabaseDescriptor databaseDescriptor = observableEvent.getDatabaseDescriptor();
        final TabDescriptor tabDescriptor = loadTabForViewMenuChoice(
            observableEvent, databaseDescriptor);
        mTabController.addTabToTabbedPaneAndOpenTabList(databaseDescriptor, tabDescriptor);
        mTabController.switchToTab(databaseDescriptor, tabDescriptor);
    }

    private TabDescriptor loadTabForViewMenuChoice(
            final ViewMenuChoice observableEvent,
            final DatabaseDescriptor databaseDescriptor) {
        final List<TabIdentifier> tabIdsToOpen = Arrays.asList(observableEvent.getTabId());
        final List<TabDescriptor> loadedTabs = mTabFactory.loadTabs(databaseDescriptor, tabIdsToOpen);
        final TabDescriptor tabDescriptor = loadedTabs.get(0);
        return tabDescriptor;
    }

    private void closeTab(final ViewMenuChoice observableEvent) {
        // find this tab descriptor
        final DatabaseDescriptor databaseDescriptor = observableEvent.getDatabaseDescriptor();
        final TabIdentifier tabIdentifierToClose = observableEvent.getTabId();

        final List<TabDescriptor> tabsForDatabase = mOpenTabList.getTabsForDatabase(databaseDescriptor.getDatabaseName());
        final List<TabDescriptor> tabListToClose = getTabDescriptorsMatchingTabIdentifier(
            tabIdentifierToClose, tabsForDatabase);

        // Since you can only have one TabDescriptor for each unique TabIdentifier, and we're only
        // closing one tab, there should only be one TabDescriptor to close. If there's more than one,
        // just warn, and close everything we've been given.
        if (tabListToClose.size() > 0) {
            if (tabListToClose.size() > 1) {
                LOGGER.warn("Request to close tab " + tabIdentifierToClose + " but found " + tabListToClose.size()
                    + " tabs: " + tabListToClose);
            }
            LOGGER.debug("removing from the tabbed pane and open tab list");
            for (final TabDescriptor tabDescriptor : tabListToClose) {
                mTabController.removeTabFromTabbedPaneAndOpenTabList(observableEvent.getDatabaseDescriptor(), tabDescriptor);
            }
            LOGGER.debug("closing tabs via the tab factory");
            mTabFactory.closeTabs(databaseDescriptor, tabListToClose);
            LOGGER.debug("tab " + tabIdentifierToClose + " closed");
        }
    }

    private List<TabDescriptor> getTabDescriptorsMatchingTabIdentifier(
            final TabIdentifier tabIdentifierToFind,
            final List<TabDescriptor> tabsForDatabase) {
        final List<TabDescriptor> tabListToClose = new ArrayList<TabDescriptor>();
        for (final TabDescriptor tabDescriptor : tabsForDatabase) {
            if (tabDescriptor.getTabIdentifier().equals(tabIdentifierToFind)) {
                tabListToClose.add(tabDescriptor);
                LOGGER.debug("Will close tab descriptor " + tabDescriptor);
            }
        }
        return tabListToClose;
    }
}
