package org.devzendo.minimiser.opentablist;

import java.awt.Label;
import java.util.List;

import org.devzendo.commoncode.patterns.observer.Observer;
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
        final TabDescriptor tab = new TabDescriptor(TabIdentifier.OVERVIEW, stubTab);
        openList.addTab(databaseDescriptor, tab);
        
        final List<TabDescriptor> oneTabs = openList.getTabsForDatabase("one");
        Assert.assertNotNull(oneTabs);
        Assert.assertEquals(1, oneTabs.size());
        Assert.assertEquals(TabIdentifier.OVERVIEW, oneTabs.get(0).getTabIdentifier());
        Assert.assertEquals(label, oneTabs.get(0).getTab().getComponent());
    }
    
    /**
     * 
     */
    @Test
    public void canAddTabNoNonexistantDatabaseAndItllAddOk() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");

        Assert.assertFalse(openList.containsTab("one", TabIdentifier.OVERVIEW));

        final Label label = new Label();
        final StubTab stubTab = new StubTab(label);
        final TabDescriptor tab = new TabDescriptor(TabIdentifier.OVERVIEW, stubTab);
        openList.addTab(databaseDescriptor, tab);
        
        Assert.assertTrue(openList.containsTab("one", TabIdentifier.OVERVIEW));
        
        final List<TabDescriptor> oneTabs = openList.getTabsForDatabase("one");
        Assert.assertNotNull(oneTabs);
        Assert.assertEquals(1, oneTabs.size());
        Assert.assertEquals(TabIdentifier.OVERVIEW, oneTabs.get(0).getTabIdentifier());
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
        final TabDescriptor tab = new TabDescriptor(TabIdentifier.OVERVIEW, stubTab);
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

        final Label overviewLabel = new Label();
        final StubTab overviewStubTab = new StubTab(overviewLabel);
        final TabDescriptor overviewTab = new TabDescriptor(TabIdentifier.OVERVIEW, overviewStubTab);
        openList.addTab(databaseDescriptor, overviewTab);

        final Label sqlLabel = new Label();
        final StubTab sqlStubTab = new StubTab(sqlLabel);
        final TabDescriptor sqlTab = new TabDescriptor(TabIdentifier.SQL, sqlStubTab);
        openList.addTab(databaseDescriptor, sqlTab);

        final List<TabDescriptor> oneTabs = openList.getTabsForDatabase("one");
        Assert.assertNotNull(oneTabs);
        Assert.assertEquals(2, oneTabs.size());
        Assert.assertEquals(TabIdentifier.SQL, oneTabs.get(0).getTabIdentifier());
        Assert.assertEquals(sqlLabel, oneTabs.get(0).getTab().getComponent());
        Assert.assertEquals(TabIdentifier.OVERVIEW, oneTabs.get(1).getTabIdentifier());
        Assert.assertEquals(overviewLabel, oneTabs.get(1).getTab().getComponent());
    }

    /**
     * 
     */
    @Test(expected = IllegalStateException.class)
    public void addTabsTwiceFails() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");

        final Label overviewLabel = new Label();
        final StubTab overviewStubTab = new StubTab(overviewLabel);
        final TabDescriptor overviewTab = new TabDescriptor(TabIdentifier.OVERVIEW, overviewStubTab);
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
        final TabDescriptor overviewTab = new TabDescriptor(TabIdentifier.OVERVIEW, overviewStubTab);

        final Observer<TabEvent> obs = EasyMock.createStrictMock(Observer.class);
        obs.eventOccurred(EasyMock.eq(new TabOpenedEvent(databaseDescriptor, overviewTab)));
        EasyMock.replay(obs);

        openList.addTabEventObserver(obs);
        
        openList.addTab(databaseDescriptor, overviewTab);

        EasyMock.verify(obs);
        Assert.assertTrue(openList.containsTab("one", TabIdentifier.OVERVIEW));
    }
    
    // Insertion position ordering tests ---------------------------------------

    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void orderOfNull() {
        openList.getInsertionPosition(null, TabIdentifier.SQL);
    }
    
    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void orderOfEmptyDbName() {
        openList.getInsertionPosition("", TabIdentifier.SQL);
    }
    
    /**
     * 
     */
    @Test
    public void orderOfNonExistantDatabase() {
        Assert.assertEquals(-1, openList.getInsertionPosition("foo", TabIdentifier.SQL));
    }
    
    /**
     * 
     */
    @Test(expected = IllegalStateException.class)
    public void orderOfDuplicate() { // only tests one TabIdentifier
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        openList.addTab(databaseDescriptor, new TabDescriptor(TabIdentifier.SQL));
        
        openList.getInsertionPosition("one", TabIdentifier.SQL);
    }
    
    /**
     * 
     */
    @Test
    public void orderOnInsertionOfEmpty() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        openList.addDatabase(databaseDescriptor);

        for (TabIdentifier tabId : TabIdentifier.values()) {
            Assert.assertEquals(0, openList.getInsertionPosition("one", tabId));
        }
    }
    
    /**
     * SQL, OVERVIEW, CATEGORIES
     */
    @Test
    public void orderInsertingAtHead() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        openList.addDatabase(databaseDescriptor);

        Assert.assertEquals(0, openList.getInsertionPosition("one", TabIdentifier.CATEGORIES));

        openList.addTab(databaseDescriptor, new TabDescriptor(TabIdentifier.CATEGORIES));

        Assert.assertEquals(0, openList.getInsertionPosition("one", TabIdentifier.OVERVIEW));
        
        openList.addTab(databaseDescriptor, new TabDescriptor(TabIdentifier.OVERVIEW));

        Assert.assertEquals(0, openList.getInsertionPosition("one", TabIdentifier.SQL));
    }

    /**
     * SQL, OVERVIEW, CATEGORIES
     */
    @Test
    public void orderInsertingAtTail() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        openList.addDatabase(databaseDescriptor);

        Assert.assertEquals(0, openList.getInsertionPosition("one", TabIdentifier.SQL));

        openList.addTab(databaseDescriptor, new TabDescriptor(TabIdentifier.SQL));

        Assert.assertEquals(1, openList.getInsertionPosition("one", TabIdentifier.OVERVIEW));
        
        openList.addTab(databaseDescriptor, new TabDescriptor(TabIdentifier.OVERVIEW));

        Assert.assertEquals(2, openList.getInsertionPosition("one", TabIdentifier.CATEGORIES));
    }

    /**
     * SQL, OVERVIEW, CATEGORIES
     */
    @Test
    public void orderInsertingButterfly() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        openList.addDatabase(databaseDescriptor);

        Assert.assertEquals(0, openList.getInsertionPosition("one", TabIdentifier.OVERVIEW));

        openList.addTab(databaseDescriptor, new TabDescriptor(TabIdentifier.OVERVIEW));

        Assert.assertEquals(1, openList.getInsertionPosition("one", TabIdentifier.CATEGORIES));
        
        openList.addTab(databaseDescriptor, new TabDescriptor(TabIdentifier.CATEGORIES));

        Assert.assertEquals(0, openList.getInsertionPosition("one", TabIdentifier.SQL));
    }

    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void removingTabFromNullDatabaseThrows() {
        openList.removeTab(null, new TabDescriptor(TabIdentifier.OVERVIEW));
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
        
        openList.removeTab(databaseDescriptor, new TabDescriptor(TabIdentifier.OVERVIEW));
    }

    /**
     * 
     */
    @Test(expected = IllegalStateException.class)
    public void removingNonExistantTabThrows() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        openList.addDatabase(databaseDescriptor);
        
        openList.removeTab(databaseDescriptor, new TabDescriptor(TabIdentifier.OVERVIEW));
    }
    
    /**
     * 
     */
    @Test
    public void removingExistingTabOk() {
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("one");
        openList.addDatabase(databaseDescriptor);
        openList.addTab(databaseDescriptor, new TabDescriptor(TabIdentifier.OVERVIEW));
        openList.addTab(databaseDescriptor, new TabDescriptor(TabIdentifier.SQL));

        openList.removeTab(databaseDescriptor, new TabDescriptor(TabIdentifier.OVERVIEW));
        final List<TabDescriptor> oneTabs = openList.getTabsForDatabase("one");
        Assert.assertNotNull(oneTabs);
        Assert.assertEquals(1, oneTabs.size());
        Assert.assertEquals(TabIdentifier.SQL, oneTabs.get(0).getTabIdentifier());
        
        openList.removeTab(databaseDescriptor, new TabDescriptor(TabIdentifier.SQL));
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
        openList.addTab(databaseDescriptor, new TabDescriptor(TabIdentifier.OVERVIEW));

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
