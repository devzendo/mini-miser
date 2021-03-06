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

import java.awt.Label;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JTabbedPane;

import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.gui.tab.IntegerTabParameter;
import org.devzendo.minimiser.gui.tab.SystemTabIdentifiers;
import org.devzendo.minimiser.gui.tab.TabIdentifier;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.DatabaseEvent;
import org.devzendo.minimiser.openlist.DatabaseOpenedEvent;
import org.devzendo.minimiser.openlist.DatabaseSwitchedEvent;
import org.devzendo.minimiser.opentablist.TabDescriptor;
import org.devzendo.minimiser.pluginmanager.PluginException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the linkage between the menu items enabling/disabling and
 * various system events.
 *
 * @author matt
 *
 */
public final class TestMenuMediator extends MenuMediatorUnittestCase {

    /**
     *
     */
    @Test
    public void testCloseDisabledWithNothingOpen() {
        startMediator();

        Assert.assertFalse(getStubMenu().isCloseEnabled());
    }

    /**
     *
     */
    @Test
    public void testCloseEnabledWithOneOpen() {
        startMediator();

        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor);
        Assert.assertTrue(getStubMenu().isCloseEnabled());
    }

    /**
     *
     */
    @Test
    public void recentListGetsAddedWhenOpenListIsAddedTo() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");

        getRecentFilesList().add(databaseDescriptor);

        startMediator();

        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor);

        Assert.assertTrue(Arrays.asList(getRecentFilesList().getRecentDatabases()).contains(databaseDescriptor));
    }

    /**
     */
    @Test
    public void recentMenuGetsRebuiltWhenOpenListIsAddedTo() {
        Assert.assertEquals(0, getStubMenu().getRecentDatabases().length);
        startMediator();

        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        Assert.assertFalse(getStubMenu().isRecentListBuilt());
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor);
        Assert.assertTrue(getStubMenu().isRecentListBuilt());
        Assert.assertEquals(1, getStubMenu().getRecentDatabases().length);
        Assert.assertEquals(databaseDescriptor, getStubMenu().getRecentDatabases()[0]);
    }

    /**
     *
     */
    @Test
    public void openRecentWhenAlreadyOpenedShouldSwitch() {
        startMediator();

        final DatabaseDescriptor databaseDescriptorOne = new DatabaseDescriptor("one");
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptorOne);

        final AtomicBoolean switchedToOne = new AtomicBoolean(false);
        getOpenDatabaseList().addDatabaseEventObserver(new Observer<DatabaseEvent>() {
            public void eventOccurred(final DatabaseEvent observableEvent) {
                if (observableEvent instanceof DatabaseSwitchedEvent) {
                    final DatabaseSwitchedEvent dse = (DatabaseSwitchedEvent) observableEvent;
                    if (dse.getDatabaseName().equals("one")) {
                        switchedToOne.set(true);
                    }
                }
            }
        });

        final DatabaseDescriptor databaseDescriptorTwo = new DatabaseDescriptor("two");
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptorTwo);

        Assert.assertTrue(getStubMenu().isRecentListBuilt());
        Assert.assertEquals(2, getStubMenu().getRecentDatabases().length);
        Assert.assertEquals(databaseDescriptorTwo, getStubMenu().getRecentDatabases()[0]);

        Assert.assertFalse(switchedToOne.get());

        getStubMenu().injectOpenRecentRequest("one", "/tmp/one");

        Assert.assertTrue("did not switch to already-opened on recent open of already-opened db", switchedToOne.get());
    }

    /**
     *
     */
    @Test
    public void openRecentWhenNotCurrentlyOpenedShouldOpen() {
        startMediator();

        final DatabaseDescriptor databaseDescriptorOne = new DatabaseDescriptor("one");
        ((StubRecentFilesList) getRecentFilesList()).addDatabaseSilently(databaseDescriptorOne);

        final AtomicBoolean openedOne = new AtomicBoolean(false);
        getOpenDatabaseList().addDatabaseEventObserver(new Observer<DatabaseEvent>() {
            public void eventOccurred(final DatabaseEvent observableEvent) {
                if (observableEvent instanceof DatabaseOpenedEvent) {
                    final DatabaseOpenedEvent doe = (DatabaseOpenedEvent) observableEvent;
                    if (doe.getDatabaseName().equals("one")) {
                        openedOne.set(true);
                    }
                }
            }
        });

        Assert.assertFalse(openedOne.get());

        getStubMenu().injectOpenRecentRequest("one", "/tmp/one");
        Assert.assertTrue("did not open not-opened on recent open of not-opened db", openedOne.get());
    }

    /**
     *
     */
    @Test
    public void viewMenuAddsAndRemovesTabs() {
        final DatabaseDescriptor databaseDescriptorOne = new DatabaseDescriptor("one");
        final Object lock = new Object();
        synchronized (lock) {
            final JTabbedPane tabbedPane = new JTabbedPane(); // EDT be damned!
            databaseDescriptorOne.setTabbedPane(tabbedPane);
            Assert.assertEquals(0, databaseDescriptorOne.getTabbedPane().getComponentCount());
        }

        getOpenDatabaseList().addOpenedDatabase(databaseDescriptorOne);
        getOpenTabList().addDatabase(databaseDescriptorOne);

        startMediator();

        final List<TabDescriptor> tabsForDatabase = getOpenTabList().getTabsForDatabase("one");
        Assert.assertNotNull(tabsForDatabase);
        Assert.assertEquals(0, tabsForDatabase.size());


        // open the tab
        getStubMenu().injectViewMenuRequest(databaseDescriptorOne, SystemTabIdentifiers.SQL, true);
        synchronized (lock) {
            Assert.assertEquals(1, databaseDescriptorOne.getTabbedPane().getComponentCount());
        }

        final List<TabDescriptor> tabsForDatabaseWithSql = getOpenTabList().getTabsForDatabase("one");
        Assert.assertNotNull(tabsForDatabaseWithSql);
        Assert.assertEquals(1, tabsForDatabaseWithSql.size());
        final TabDescriptor sql = tabsForDatabaseWithSql.get(0);
        Assert.assertEquals(SystemTabIdentifiers.SQL, sql.getTabIdentifier());

        // close the tab
        getStubMenu().injectViewMenuRequest(databaseDescriptorOne, SystemTabIdentifiers.SQL, false);

        final List<TabDescriptor> tabsForDatabaseWithNoTabs = getOpenTabList().getTabsForDatabase("one");
        Assert.assertNotNull(tabsForDatabaseWithNoTabs);
        Assert.assertEquals(0, tabsForDatabaseWithNoTabs.size());
        synchronized (lock) {
            Assert.assertEquals(0, databaseDescriptorOne.getTabbedPane().getComponentCount());
        }
    }

    /**
     *
     */
    @Test
    public void viewMenuAddsAndRemovesMultipleParameterisedTabs() {
        final DatabaseDescriptor databaseDescriptorOne = new DatabaseDescriptor("one");
        final Object lock = new Object();
        synchronized (lock) {
            final JTabbedPane tabbedPane = new JTabbedPane(); // EDT be damned!
            databaseDescriptorOne.setTabbedPane(tabbedPane);
            Assert.assertEquals(0, databaseDescriptorOne.getTabbedPane().getComponentCount());
        }

        getOpenDatabaseList().addOpenedDatabase(databaseDescriptorOne);
        getOpenTabList().addDatabase(databaseDescriptorOne);

        startMediator();

        final List<TabDescriptor> tabsForDatabase = getOpenTabList().getTabsForDatabase("one");
        Assert.assertNotNull(tabsForDatabase);
        Assert.assertEquals(0, tabsForDatabase.size());


        // open the tabs
        final TabIdentifier firstTabId = new TabIdentifier("TEST", "Test One", false, '1', "sampleTab", new IntegerTabParameter(1));
        final TabIdentifier secondTabId = new TabIdentifier("TEST", "Test Two", false, '2', "sampleTab", new IntegerTabParameter(2));
        getStubMenu().injectViewMenuRequest(databaseDescriptorOne, firstTabId, true);
        getStubMenu().injectViewMenuRequest(databaseDescriptorOne, secondTabId, true);
        synchronized (lock) {
            Assert.assertEquals(2, databaseDescriptorOne.getTabbedPane().getComponentCount());
        }

        final List<TabDescriptor> tabsForDatabaseWithTwoTabs = getOpenTabList().getTabsForDatabase("one");
        Assert.assertNotNull(tabsForDatabaseWithTwoTabs);
        Assert.assertEquals(2, tabsForDatabaseWithTwoTabs.size());
        final TabDescriptor firstTab = tabsForDatabaseWithTwoTabs.get(0);
        Assert.assertEquals(firstTabId, firstTab.getTabIdentifier());
        final TabDescriptor secondTab = tabsForDatabaseWithTwoTabs.get(1);
        Assert.assertEquals(secondTabId, secondTab.getTabIdentifier());

        // close the second tab
        getStubMenu().injectViewMenuRequest(databaseDescriptorOne, secondTabId, false);

        final List<TabDescriptor> remainingTabsForDatabase = getOpenTabList().getTabsForDatabase("one");
        Assert.assertNotNull(remainingTabsForDatabase);
        Assert.assertEquals(1, remainingTabsForDatabase.size());
        final TabDescriptor remainingTabDescriptor = remainingTabsForDatabase.get(0);
        Assert.assertEquals(firstTabId, remainingTabDescriptor.getTabIdentifier());
        final IntegerTabParameter remainingTabParameter = (IntegerTabParameter) remainingTabDescriptor.getTabIdentifier().getTabBeanParameter();
        Assert.assertEquals(new Integer(1), remainingTabParameter.getValue());
        synchronized (lock) {
            final JTabbedPane jTabbedPane = databaseDescriptorOne.getTabbedPane();
            Assert.assertEquals(1, jTabbedPane.getComponentCount());
            Assert.assertEquals("Test One", ((Label) jTabbedPane.getComponent(0)).getText()); // StubRecordingTab creates Labels
        }

        // close the only remaining (first) tab
        getStubMenu().injectViewMenuRequest(databaseDescriptorOne, firstTabId, false);

        final List<TabDescriptor> shouldBeEmptyTabsForDatabase = getOpenTabList().getTabsForDatabase("one");
        Assert.assertNotNull(shouldBeEmptyTabsForDatabase);
        Assert.assertEquals(0, shouldBeEmptyTabsForDatabase.size());
        synchronized (lock) {
            final JTabbedPane jTabbedPane = databaseDescriptorOne.getTabbedPane();
            Assert.assertEquals(0, jTabbedPane.getComponentCount());
        }
   }


    /**
     *
     */
    @Test
    public void testCloseDisabledWhenLastClosed() {
        startMediator();

        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        Assert.assertNull(getMainFrameTitle().getCurrentDatabaseName());
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor);
        Assert.assertEquals("one", getMainFrameTitle().getCurrentDatabaseName());
        getOpenDatabaseList().removeClosedDatabase(databaseDescriptor);
        Assert.assertFalse(getStubMenu().isCloseEnabled());
        Assert.assertNull(getMainFrameTitle().getCurrentDatabaseName());
    }

    /**
     *
     */
    @Test
    public void testCloseDisabledWhenLastOfMultipleClosed() {
        startMediator();

        final DatabaseDescriptor databaseDescriptor1 = new DatabaseDescriptor("one");
        final DatabaseDescriptor databaseDescriptor2 = new DatabaseDescriptor("two");
        Assert.assertNull(getMainFrameTitle().getCurrentDatabaseName());
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor1);
        Assert.assertEquals("one", getMainFrameTitle().getCurrentDatabaseName());
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor2);
        Assert.assertEquals("two", getMainFrameTitle().getCurrentDatabaseName());
        getOpenDatabaseList().removeClosedDatabase(databaseDescriptor1);
        Assert.assertEquals("two", getMainFrameTitle().getCurrentDatabaseName());
        Assert.assertTrue(getStubMenu().isCloseEnabled());
        getOpenDatabaseList().removeClosedDatabase(databaseDescriptor2);
        Assert.assertFalse(getStubMenu().isCloseEnabled());
        Assert.assertNull(getMainFrameTitle().getCurrentDatabaseName());
    }

    /**
     *
     */
    @Test
    public void testEmptySwitchList() {
        startMediator();

        Assert.assertTrue(getStubMenu().getNumberOfDatabases() == 0);
        Assert.assertNull(getStubMenu().getCurrentDatabase());
    }

    /**
     *
     */
    @Test
    public void testPopulatedSwitchList() {
        startMediator();

        final DatabaseDescriptor databaseDescriptor1 = new DatabaseDescriptor("one");
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor1);
        Assert.assertTrue(getStubMenu().getNumberOfDatabases() == 1);
        Assert.assertEquals("one", getStubMenu().getCurrentDatabase());
    }

    /**
     *
     */
    @Test
    public void testSwitchOnOpenNew() {
        startMediator();

        final DatabaseDescriptor databaseDescriptor1 = new DatabaseDescriptor("one");
        final DatabaseDescriptor databaseDescriptor2 = new DatabaseDescriptor("two");
        Assert.assertNull(getMainFrameTitle().getCurrentDatabaseName());
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor1);
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor2);
        Assert.assertTrue(getStubMenu().getNumberOfDatabases() == 2);
        Assert.assertEquals("two", getStubMenu().getCurrentDatabase());
        Assert.assertEquals("two", getMainFrameTitle().getCurrentDatabaseName());
    }

    /**
     *
     */
    @Test
    public void testSwitchBackOnClose() {
        startMediator();

        final DatabaseDescriptor databaseDescriptor1 = new DatabaseDescriptor("one");
        final DatabaseDescriptor databaseDescriptor2 = new DatabaseDescriptor("two");
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor1);
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor2);
        getOpenDatabaseList().removeClosedDatabase(databaseDescriptor2);
        Assert.assertTrue(getStubMenu().getNumberOfDatabases() == 1);
        Assert.assertEquals("one", getStubMenu().getCurrentDatabase());
    }

    /**
     *
     */
    @Test
    public void windowMenuSwitchesDatabase() {
        startMediator();

        final DatabaseDescriptor databaseDescriptor1 = new DatabaseDescriptor("one");
        final DatabaseDescriptor databaseDescriptor2 = new DatabaseDescriptor("two");
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor1);
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor2);

        Assert.assertEquals("two", getStubMenu().getCurrentDatabase());

        getStubMenu().injectWindowMenuRequest(databaseDescriptor1);

        Assert.assertEquals("one", getStubMenu().getCurrentDatabase());

    }
    /**
     *
     */
    @Test
    public void testNoCurrentWhenLastClosed() {
        startMediator();

        final DatabaseDescriptor databaseDescriptor1 = new DatabaseDescriptor("one");
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor1);
        getOpenDatabaseList().removeClosedDatabase(databaseDescriptor1);
        Assert.assertTrue(getStubMenu().getNumberOfDatabases() == 0);
        Assert.assertNull(getStubMenu().getCurrentDatabase());
        Assert.assertFalse(getStubMenu().isCloseEnabled());
    }

    /**
     *
     */
    @Test
    public void openerOpeningAddsToDatabaseList() {
        startMediator();

        final AtomicBoolean openedOne = new AtomicBoolean(false);
        getOpenDatabaseList().addDatabaseEventObserver(new Observer<DatabaseEvent>() {
            public void eventOccurred(final DatabaseEvent observableEvent) {
                if (observableEvent instanceof DatabaseOpenedEvent) {
                    final DatabaseOpenedEvent doe = (DatabaseOpenedEvent) observableEvent;
                    if (doe.getDatabaseName().equals("one")) {
                        openedOne.set(true);
                    }
                }
            }
        });

        getStubOpener().openDatabase("one", "/tmp/one", null);
        Assert.assertTrue(openedOne.get());
    }

    /**
     * @throws IOException on fail
     *
     */
    @Test
    public void hiddenTabsChangedInPrefsRebuildsViewMenu() throws IOException {
        startMediator();

        Assert.assertFalse(getStubMenu().isTabHidden("SQL"));
        Assert.assertFalse(getStubMenu().isViewMenuBuilt());

        getPrefs().setTabHidden("SQL");

        Assert.assertTrue(getStubMenu().isTabHidden("SQL"));
        Assert.assertTrue(getStubMenu().isViewMenuBuilt());

        getStubMenu().resetViewMenuBuiltFlag();
        Assert.assertTrue(getStubMenu().isTabHidden("SQL"));
        Assert.assertFalse(getStubMenu().isViewMenuBuilt());

        getPrefs().clearTabHidden("SQL");

        Assert.assertFalse(getStubMenu().isTabHidden("SQL"));
        Assert.assertTrue(getStubMenu().isViewMenuBuilt());
    }

    /**
     * This is testing the stub menu really - switching causes the menu impls
     * to rebuild the view menu.
     */
    @Test
    public void databaseSwitchRebuildsViewMenu() {
        startMediator();

        final DatabaseDescriptor databaseDescriptor1 = new DatabaseDescriptor("one");
        final DatabaseDescriptor databaseDescriptor2 = new DatabaseDescriptor("two");
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor1);
        getOpenDatabaseList().addOpenedDatabase(databaseDescriptor2);

        getStubMenu().resetViewMenuBuiltFlag();
        Assert.assertFalse(getStubMenu().isViewMenuBuilt());

        getStubMenu().injectWindowMenuRequest(databaseDescriptor1);

        Assert.assertTrue(getStubMenu().isViewMenuBuilt());
    }

    /**
     * @throws PluginException on error
     */
    @Test
    public void helpMenuIsRebuiltWhenAppPluginLoaded() throws PluginException {
        final String dummyAppName = "Dummy App";
        Assert.assertEquals(dummyAppName, getPluginManager().getApplicationPlugin().getName());
        Assert.assertEquals("", getStubMenu().getHelpMenuApplicationName());
//        getAppDetails().setApplicationName(dummyAppName);

        startMediator();

        // The Kludge in the MenuPluginLoadedObserver means that
        // the app name (if set) will be stashed in the menu
        // as early as possible. Otherwise, this would be ""
        //Assert.assertEquals(dummyAppName, getStubMenu().getHelpMenuApplicationName());

        getPluginManager().loadPlugins("not important");

        Assert.assertEquals(dummyAppName, getStubMenu().getHelpMenuApplicationName());
    }
}
