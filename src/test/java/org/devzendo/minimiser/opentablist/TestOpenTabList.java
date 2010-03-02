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

package org.devzendo.minimiser.opentablist;

import java.awt.Label;
import java.util.List;

import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.gui.tab.SystemTabIdentifiers;
import org.devzendo.minimiser.gui.tab.TabIdentifier;
import org.devzendo.minimiser.logging.LoggingTestCase;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



/**
 * Tests the open tab list.
 *
 * @author matt
 *
 */
public final class TestOpenTabList extends LoggingTestCase {
    private OpenTabList openList;

    /**
     *
     */
    @Before
    public void getPrerequisites() {
        openList = new OpenTabList();
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void emptinessForNull() {
        Assert.assertNull(openList.getTabsForDatabase(null));
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void emptinessForEmptyString() {
        final List<TabDescriptor> tabsForDatabase = openList.getTabsForDatabase("");
        Assert.assertNotNull(tabsForDatabase);
        Assert.assertEquals(0, tabsForDatabase.size());
    }

    /**
     *
     */
    @Test
    public void emptinessForNonExistant() {
        final List<TabDescriptor> tabsForDatabase = openList.getTabsForDatabase("nonexistant");
        Assert.assertNotNull(tabsForDatabase);
        Assert.assertEquals(0, tabsForDatabase.size());
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void emptinessOnAddNull() {
        openList.addDatabase(null);
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void emptinessForGetNull() {
        Assert.assertNull(openList.getTabsForDatabase(null));
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void emptinessForGetEmptyString() {
        Assert.assertNull(openList.getTabsForDatabase(""));
    }

    /**
     *
     */
    @Test
    public void addDatabaseGetsEmptyTabList() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        openList.addDatabase(databaseDescriptor);
        final List<TabDescriptor> oneTabs = openList.getTabsForDatabase("one");
        Assert.assertNotNull(oneTabs);
        Assert.assertEquals(0, oneTabs.size());
    }

    /**
     *
     */
    @Test
    public void addTabGoesInList() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        openList.addDatabase(databaseDescriptor);
        final Label label = new Label();
        final StubTab stubTab = new StubTab(label);
        final TabDescriptor tab = new TabDescriptor(SystemTabIdentifiers.OVERVIEW, stubTab);
        openList.addTab(databaseDescriptor, tab);

        final List<TabDescriptor> oneTabs = openList.getTabsForDatabase("one");
        Assert.assertNotNull(oneTabs);
        Assert.assertEquals(1, oneTabs.size());
        Assert.assertEquals(SystemTabIdentifiers.OVERVIEW, oneTabs.get(0).getTabIdentifier());
        Assert.assertEquals(label, oneTabs.get(0).getTab().getComponent());
    }

    /**
     *
     */
    @Test
    public void canAddTabToNonexistantDatabaseAndItWillAddOk() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");

        Assert.assertFalse(openList.containsTab("one", SystemTabIdentifiers.OVERVIEW));

        final Label label = new Label();
        final StubTab stubTab = new StubTab(label);
        final TabDescriptor tab = new TabDescriptor(SystemTabIdentifiers.OVERVIEW, stubTab);
        openList.addTab(databaseDescriptor, tab);

        Assert.assertTrue(openList.containsTab("one", SystemTabIdentifiers.OVERVIEW));

        final List<TabDescriptor> oneTabs = openList.getTabsForDatabase("one");
        Assert.assertNotNull(oneTabs);
        Assert.assertEquals(1, oneTabs.size());
        Assert.assertEquals(SystemTabIdentifiers.OVERVIEW, oneTabs.get(0).getTabIdentifier());
        Assert.assertEquals(label, oneTabs.get(0).getTab().getComponent());
    }

    /**
     *
     */
    @Test
    public void addExistingDatabaseDoesntWipeOutTabs() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        openList.addDatabase(databaseDescriptor);
        final Label label = new Label();
        final StubTab stubTab = new StubTab(label);
        final TabDescriptor tab = new TabDescriptor(SystemTabIdentifiers.OVERVIEW, stubTab);
        openList.addTab(databaseDescriptor, tab);

        final List<TabDescriptor> oneTabs = openList.getTabsForDatabase("one");
        Assert.assertEquals(1, oneTabs.size());

        openList.addDatabase(databaseDescriptor);
        Assert.assertEquals(1, openList.getTabsForDatabase("one").size());
    }

    /**
     *
     */
    @Test
    public void addTabsOutOfOrderAndGetTabsGetsThemBackInOrder() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");

        final Label sqlLabel = new Label();
        final StubTab sqlStubTab = new StubTab(sqlLabel);
        final TabDescriptor sqlTab = new TabDescriptor(SystemTabIdentifiers.SQL, sqlStubTab);
        openList.addTab(databaseDescriptor, sqlTab);

        final Label overviewLabel = new Label();
        final StubTab overviewStubTab = new StubTab(overviewLabel);
        final TabDescriptor overviewTab = new TabDescriptor(SystemTabIdentifiers.OVERVIEW, overviewStubTab);
        openList.addTab(databaseDescriptor, overviewTab);

        final List<TabDescriptor> oneTabs = openList.getTabsForDatabase("one");
        Assert.assertNotNull(oneTabs);
        Assert.assertEquals(2, oneTabs.size());
        Assert.assertEquals(SystemTabIdentifiers.OVERVIEW, oneTabs.get(0).getTabIdentifier());
        Assert.assertEquals(overviewLabel, oneTabs.get(0).getTab().getComponent());
        Assert.assertEquals(SystemTabIdentifiers.SQL, oneTabs.get(1).getTabIdentifier());
        Assert.assertEquals(sqlLabel, oneTabs.get(1).getTab().getComponent());
    }

    /**
     *
     */
    @Test(expected = IllegalStateException.class)
    public void addTabsTwiceFails() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");

        final Label overviewLabel = new Label();
        final StubTab overviewStubTab = new StubTab(overviewLabel);
        final TabDescriptor overviewTab = new TabDescriptor(SystemTabIdentifiers.OVERVIEW, overviewStubTab);
        openList.addTab(databaseDescriptor, overviewTab);

        openList.addTab(databaseDescriptor, overviewTab);
    }

    /**
     *
     */
    @SuppressWarnings("unchecked")
    @Test
    public void addTabFiresListener() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");

        final Label overviewLabel = new Label();
        final StubTab overviewStubTab = new StubTab(overviewLabel);
        final TabDescriptor overviewTab = new TabDescriptor(SystemTabIdentifiers.OVERVIEW, overviewStubTab);

        final Observer<TabEvent> obs = EasyMock.createStrictMock(Observer.class);
        obs.eventOccurred(EasyMock.eq(new TabOpenedEvent(databaseDescriptor, overviewTab)));
        EasyMock.replay(obs);

        openList.addTabEventObserver(obs);

        openList.addTab(databaseDescriptor, overviewTab);

        EasyMock.verify(obs);
        Assert.assertTrue(openList.containsTab("one", SystemTabIdentifiers.OVERVIEW));
    }

    /**
     *
     */
    @SuppressWarnings("unchecked")
    @Test
    public void removeTabFiresListener() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        final TabDescriptor overviewTab = new TabDescriptor(SystemTabIdentifiers.OVERVIEW, new StubTab(new Label()));
        openList.addTab(databaseDescriptor, overviewTab);
        final Observer<TabEvent> obs = EasyMock.createStrictMock(Observer.class);
        obs.eventOccurred(EasyMock.eq(new TabRemovedEvent(databaseDescriptor, overviewTab)));
        EasyMock.replay(obs);
        openList.addTabEventObserver(obs);

        openList.removeTab(databaseDescriptor, overviewTab);

        EasyMock.verify(obs);
        Assert.assertFalse(openList.containsTab("one", SystemTabIdentifiers.OVERVIEW));
    }

    // Insertion position ordering tests ---------------------------------------

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void orderOfNull() {
        openList.getInsertionPosition(null, SystemTabIdentifiers.SQL);
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void orderOfEmptyDbName() {
        openList.getInsertionPosition("", SystemTabIdentifiers.SQL);
    }

    /**
     *
     */
    @Test
    public void orderOfNonExistantDatabase() {
        Assert.assertEquals(-1, openList.getInsertionPosition("foo", SystemTabIdentifiers.SQL));
    }

    /**
     *
     */
    @Test(expected = IllegalStateException.class)
    public void orderOfDuplicate() { // only tests one TabIdentifier
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        openList.addTab(databaseDescriptor, new TabDescriptor(SystemTabIdentifiers.SQL));

        openList.getInsertionPosition("one", SystemTabIdentifiers.SQL);
    }

    /**
     *
     */
    @Test
    public void orderOnInsertionOfEmpty() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        openList.addDatabase(databaseDescriptor);

        for (final TabIdentifier tabId : SystemTabIdentifiers.values()) {
            Assert.assertEquals(0, openList.getInsertionPosition("one", tabId));
        }
    }

    /**
     * CATEGORIES, OVERVIEW, SQL
     */
    @Test
    public void orderInsertingAtHead() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        openList.addDatabase(databaseDescriptor);

        Assert.assertEquals(0, openList.getInsertionPosition("one", SystemTabIdentifiers.SQL));

        openList.addTab(databaseDescriptor, new TabDescriptor(SystemTabIdentifiers.SQL));

        Assert.assertEquals(0, openList.getInsertionPosition("one", SystemTabIdentifiers.OVERVIEW));

        openList.addTab(databaseDescriptor, new TabDescriptor(SystemTabIdentifiers.OVERVIEW));

        Assert.assertEquals(0, openList.getInsertionPosition("one", SystemTabIdentifiers.CATEGORIES));
    }

    /**
     * CATEGORIES, OVERVIEW, SQL
     */
    @Test
    public void orderInsertingAtTail() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        openList.addDatabase(databaseDescriptor);

        Assert.assertEquals(0, openList.getInsertionPosition("one", SystemTabIdentifiers.CATEGORIES));

        openList.addTab(databaseDescriptor, new TabDescriptor(SystemTabIdentifiers.CATEGORIES));

        Assert.assertEquals(1, openList.getInsertionPosition("one", SystemTabIdentifiers.OVERVIEW));

        openList.addTab(databaseDescriptor, new TabDescriptor(SystemTabIdentifiers.OVERVIEW));

        Assert.assertEquals(2, openList.getInsertionPosition("one", SystemTabIdentifiers.SQL));
    }

    /**
     * CATEGORIES, OVERVIEW, SQL
     */
    @Test
    public void orderInsertingButterfly() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        openList.addDatabase(databaseDescriptor);

        Assert.assertEquals(0, openList.getInsertionPosition("one", SystemTabIdentifiers.OVERVIEW));

        openList.addTab(databaseDescriptor, new TabDescriptor(SystemTabIdentifiers.OVERVIEW));

        Assert.assertEquals(1, openList.getInsertionPosition("one", SystemTabIdentifiers.SQL));

        openList.addTab(databaseDescriptor, new TabDescriptor(SystemTabIdentifiers.SQL));

        Assert.assertEquals(0, openList.getInsertionPosition("one", SystemTabIdentifiers.CATEGORIES));
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void removingTabFromNullDatabaseThrows() {
        openList.removeTab(null, new TabDescriptor(SystemTabIdentifiers.OVERVIEW));
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void removingNullTabFromNullDatabaseThrows() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        openList.removeTab(databaseDescriptor, null);
    }

    /**
     *
     */
    @Test(expected = IllegalStateException.class)
    public void removingTabFromNonExistantDatabaseThrows() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");

        openList.removeTab(databaseDescriptor, new TabDescriptor(SystemTabIdentifiers.OVERVIEW));
    }

    /**
     *
     */
    @Test(expected = IllegalStateException.class)
    public void removingNonExistantTabThrows() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        openList.addDatabase(databaseDescriptor);

        openList.removeTab(databaseDescriptor, new TabDescriptor(SystemTabIdentifiers.OVERVIEW));
    }

    /**
     *
     */
    @Test
    public void removingExistingTabOk() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        openList.addDatabase(databaseDescriptor);
        openList.addTab(databaseDescriptor, new TabDescriptor(SystemTabIdentifiers.OVERVIEW));
        openList.addTab(databaseDescriptor, new TabDescriptor(SystemTabIdentifiers.SQL));

        openList.removeTab(databaseDescriptor, new TabDescriptor(SystemTabIdentifiers.OVERVIEW));
        final List<TabDescriptor> oneTabs = openList.getTabsForDatabase("one");
        Assert.assertNotNull(oneTabs);
        Assert.assertEquals(1, oneTabs.size());
        Assert.assertEquals(SystemTabIdentifiers.SQL, oneTabs.get(0).getTabIdentifier());

        openList.removeTab(databaseDescriptor, new TabDescriptor(SystemTabIdentifiers.SQL));
        final List<TabDescriptor> zeroTabs = openList.getTabsForDatabase("one");
        Assert.assertNotNull(zeroTabs);
        Assert.assertEquals(0, zeroTabs.size());
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void removingNullDatabaseThrows() {
        openList.removeDatabase(null);
    }

    /**
     *
     */
    @Test(expected = IllegalStateException.class)
    public void removingNonEmptyDatabaseThrows() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        openList.addDatabase(databaseDescriptor);
        openList.addTab(databaseDescriptor, new TabDescriptor(SystemTabIdentifiers.OVERVIEW));

        openList.removeDatabase(databaseDescriptor);
    }

    /**
     *
     */
    @Test
    public void removingDatabaseRemovesIt() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        openList.addDatabase(databaseDescriptor);

        openList.removeDatabase(databaseDescriptor);
        final List<TabDescriptor> zeroTabs = openList.getTabsForDatabase("one");
        Assert.assertNotNull(zeroTabs);
        Assert.assertEquals(0, zeroTabs.size());
    }
}
