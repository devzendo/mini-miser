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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.gui.GUIUtils;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.gui.tab.TabIdentifier;
import org.devzendo.minimiser.gui.tabfactory.TabFactory;
import org.devzendo.minimiser.gui.tabpanemanager.TabListPrefs;
import org.devzendo.minimiser.openlist.DatabaseClosedEvent;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.DatabaseEvent;
import org.devzendo.minimiser.openlist.DatabaseOpenedEvent;
import org.devzendo.minimiser.opentablist.OpenTabList;
import org.devzendo.minimiser.opentablist.TabDescriptor;
import org.devzendo.minimiser.tabcontroller.TabController;



/**
 * A database has been opened, so create a JTabbedPane for it,
 * populate it with {the previously-open view tabs UNION the
 * permanent tabs} and add these tabs to the open tab list.
 *
 * On close, remove the tabs from the open tab list.
 *
 * TODO SRP I could argue that this class has two reasons to change,
 * and so violates SRP. At least it should be renamed as it's a
 * lifecycle manager for the TabPanes.
 *
 * @author matt
 *
 */
public final class TabPaneCreatingDatabaseEventListener implements Observer<DatabaseEvent> {
    private static final Logger LOGGER = Logger
        .getLogger(TabPaneCreatingDatabaseEventListener.class);

    private final TabListPrefs mPrefs;
    private final TabFactory mTabFactory;
    private final OpenTabList mOpenTabList;

    // Used by the run-on-EDT code in createTabbedPaneOnEventThread
    private final Object mLock = new Object();
    private JTabbedPane mTabbedPane;
    private final TabController mTabController;

    /**
     * Construct the tab pane creator.
     * @param prefsStore the prefs, from which previous tab view lists will be
     * read
     * @param factory the TabFactory which will create all the above tabs
     * and add them to the OpenTabList
     * @param tabList the OpenTabList used to determine the insertion order
     * and that's added to when tabs are loaded
     * @param tabController the TabController that mediates between prefs, menu and tabs.
     */
    public TabPaneCreatingDatabaseEventListener(final TabListPrefs prefsStore,
            final TabFactory factory, final OpenTabList tabList,
            final TabController tabController) {
        mPrefs = prefsStore;
        mTabFactory = factory;
        mOpenTabList = tabList;
        mTabController = tabController;
    }

    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final DatabaseEvent databaseEvent) {
        if (databaseEvent instanceof DatabaseOpenedEvent) {
            handleDatabaseOpenedEvent((DatabaseOpenedEvent) databaseEvent);
        } else if (databaseEvent instanceof DatabaseClosedEvent) {
            handleDatabaseClosedEvent((DatabaseClosedEvent) databaseEvent);
        }
    }

    private void handleDatabaseOpenedEvent(final DatabaseOpenedEvent openEvent) {
        // this is called on a background thread - Recent Opener,
        // Open Wizard background, or Lifecycle startup.
        assert !SwingUtilities.isEventDispatchThread();

        final DatabaseDescriptor databaseDescriptor = openEvent.getDatabaseDescriptor();

        // Create the JTabbedPane
        LOGGER.info("Creating tabbed pane for database '" + databaseDescriptor.getDatabaseName() + "'");
        // TODO replace this with the GUIValueObtainer
        final JTabbedPane databaseTabbedPane = createTabbedPaneOnEventThread(databaseDescriptor);
        databaseDescriptor.setTabbedPane(databaseTabbedPane);

        // Add this database to the open tab list, so we can calculate
        // insertion points for tabs on the JTabbedPane.
        LOGGER.debug("Adding the database to the Open Tab List");
        mOpenTabList.addDatabase(databaseDescriptor);

        // Load the tabs
        LOGGER.info("Loading permanent and stored tabs for database '" + databaseDescriptor.getDatabaseName() + "'");
        final List<TabDescriptor> loadedTabDescriptors = loadPermanentAndStoredTabs(databaseDescriptor);

        LOGGER.debug(loadedTabDescriptors.size() + " tab(s) loaded; adding components to JTabbedPane");
        // Add each ones component into the JTabbedPane.
        for (final TabDescriptor tabDescriptor : loadedTabDescriptors) {
            mTabController.addTabToTabbedPaneAndOpenTabList(databaseDescriptor, tabDescriptor);
        }

        // Switch to the previously active tab?
        final TabIdentifier previousActiveTab = mPrefs.getActiveTab(databaseDescriptor.getDatabaseName());
        if (previousActiveTab == null) {
            LOGGER.warn("There is no previously active tab for this database");
        } else {
            LOGGER.debug("Previously active tab is " + previousActiveTab.getTabName());
            for (final TabDescriptor tabDescriptor : loadedTabDescriptors) {
                if (tabDescriptor.getTabIdentifier().equals(previousActiveTab)) {
                    mTabController.switchToTab(databaseDescriptor, tabDescriptor);
                }
            }
        }

        LOGGER.debug("Finished adding components to JTabbedPane and handling DatabaseOpenedEvent");
    }

    private void handleDatabaseClosedEvent(final DatabaseClosedEvent closedEvent) {
        // this is called on a background thread - Closer or Lifecycle shutdown.
        assert !SwingUtilities.isEventDispatchThread();

        final String databaseName = closedEvent.getDatabaseName();
        LOGGER.info("Closing tabs for database '" + databaseName + "'");
        final DatabaseDescriptor databaseDescriptor = closedEvent.getDatabaseDescriptor();
        final List<TabDescriptor> tabsForDatabase = mOpenTabList.getTabsForDatabase(databaseName);
        LOGGER.debug("Tabs to be closed: " + tabsForDatabase);

        // Persist the open tabs
        final List<TabIdentifier> tabIdsForDatabase = new ArrayList<TabIdentifier>();
        for (final TabDescriptor tabDesc : tabsForDatabase) {
            tabIdsForDatabase.add(tabDesc.getTabIdentifier());
        }
        mPrefs.setOpenTabs(databaseName, tabIdsForDatabase);
        final TabIdentifier currentTabIdentifier = mTabController.getCurrentTab(databaseDescriptor);
        if (currentTabIdentifier == null) {
            LOGGER.warn("No currently active tab for database " + databaseName);
        } else {
            LOGGER.info("Saving " + currentTabIdentifier.getTabName() + " as the currently active tab for database " + databaseName);
            mPrefs.setActiveTab(databaseName, currentTabIdentifier.getTabName());
        }

        if (tabsForDatabase.size() == 0) {
            LOGGER.warn("Cannot close empty list of tabs");
        } else {
            LOGGER.debug("Closing tabs via tab factory");
            mTabFactory.closeTabs(databaseDescriptor, tabsForDatabase);
            LOGGER.debug("Removing tabs from open tab list");
            for (final TabDescriptor tabDescriptor : tabsForDatabase) {
                mOpenTabList.removeTab(databaseDescriptor, tabDescriptor);
            }
        }
        LOGGER.debug("Removing database from open tab list");
        mOpenTabList.removeDatabase(databaseDescriptor);
        LOGGER.debug("Finished removing tab components and handling DatabaseClosedEvent");
    }

    private List<TabDescriptor> loadPermanentAndStoredTabs(final DatabaseDescriptor databaseDescriptor) {
        final List<TabIdentifier> permanentAndOpenTabs = mPrefs.getOpenTabs(databaseDescriptor.getDatabaseName());
        LOGGER.info("Permanent and stored tabs: " + permanentAndOpenTabs);
        // TODO perhaps the check for existence of tabs in the OpenTabList should be
        // done here and not in the tab factory?
        return mTabFactory.loadTabs(databaseDescriptor,
                            permanentAndOpenTabs);
    }

    private JTabbedPane createTabbedPaneOnEventThread(final DatabaseDescriptor descriptor) {
        if (SwingUtilities.isEventDispatchThread()) {
            return new JTabbedPane();
        } else {
            final CountDownLatch latch = new CountDownLatch(1);
            GUIUtils.runOnEventThread(new Runnable() {
                public void run() {
                    synchronized (mLock) {
                        mTabbedPane = new JTabbedPane();
                    }
                    latch.countDown();
                }
            });
            try {
                latch.await();
            } catch (final InterruptedException e) {
                return null;
            }
            synchronized (mLock) {
                return mTabbedPane;
            }
        }
    }
}
