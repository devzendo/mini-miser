package uk.me.gumbley.minimiser.opentablist;

import java.awt.Label;
import java.util.List;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;


/**
 * Tests the open tab list.
 * 
 * @author matt
 *
 */
public final class TestOpenTabList extends LoggingTestCase {
    private OpenTabList openList;
    private List<TabDescriptor> oneTabs;

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
        Assert.assertNull(openList.getTabsForDatabase(""));
    }
    
    /**
     * 
     */
    @Test
    public void emptinessForNonExistant() {
        Assert.assertNull(openList.getTabsForDatabase("nonexistant"));
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
        openList.addDatabase("one");
        oneTabs = openList.getTabsForDatabase("one");
        Assert.assertNotNull(oneTabs);
        Assert.assertEquals(0, oneTabs.size());
    }
    
    /**
     * 
     */
    @Test
    public void addTabGoesInList() {
        openList.addDatabase("one");
        final Label label = new Label();
        final StubTab stubTab = new StubTab(label);
        final TabDescriptor tab = new TabDescriptor(TabIdentifier.OVERVIEW, stubTab);
        openList.addTab("one", tab);
        
        oneTabs = openList.getTabsForDatabase("one");
        Assert.assertNotNull(oneTabs);
        Assert.assertEquals(1, oneTabs.size());
        Assert.assertEquals(TabIdentifier.OVERVIEW, oneTabs.get(0).getTabIdentifier());
        Assert.assertEquals(label, oneTabs.get(0).getComponent());
    }
    
    /**
     * 
     */
    @Test
    public void canAddTabNoNonexistantDatabaseAndItllAddOk() {
        Assert.assertFalse(openList.containsTab("one", TabIdentifier.OVERVIEW));

        final Label label = new Label();
        final StubTab stubTab = new StubTab(label);
        final TabDescriptor tab = new TabDescriptor(TabIdentifier.OVERVIEW, stubTab);
        openList.addTab("one", tab);
        
        Assert.assertTrue(openList.containsTab("one", TabIdentifier.OVERVIEW));
        
        oneTabs = openList.getTabsForDatabase("one");
        Assert.assertNotNull(oneTabs);
        Assert.assertEquals(1, oneTabs.size());
        Assert.assertEquals(TabIdentifier.OVERVIEW, oneTabs.get(0).getTabIdentifier());
        Assert.assertEquals(label, oneTabs.get(0).getComponent());
    }
    
    /**
     * 
     */
    @Test
    public void addExistingDatabaseDoesntWipeOutTabs() {
        openList.addDatabase("one");
        final Label label = new Label();
        final StubTab stubTab = new StubTab(label);
        final TabDescriptor tab = new TabDescriptor(TabIdentifier.OVERVIEW, stubTab);
        openList.addTab("one", tab);
        
        oneTabs = openList.getTabsForDatabase("one");
        Assert.assertEquals(1, oneTabs.size());
        
        openList.addDatabase("one");
        Assert.assertEquals(1, openList.getTabsForDatabase("one").size());
    }
    
    /**
     * 
     */
    @Test
    public void addTabsOutOfOrderAndGetTabsGetsThemBackInOrder() {
        final Label overviewLabel = new Label();
        final StubTab overviewStubTab = new StubTab(overviewLabel);
        final TabDescriptor overviewTab = new TabDescriptor(TabIdentifier.OVERVIEW, overviewStubTab);
        openList.addTab("one", overviewTab);

        final Label sqlLabel = new Label();
        final StubTab sqlStubTab = new StubTab(sqlLabel);
        final TabDescriptor sqlTab = new TabDescriptor(TabIdentifier.SQL, sqlStubTab);
        openList.addTab("one", sqlTab);

        oneTabs = openList.getTabsForDatabase("one");
        Assert.assertNotNull(oneTabs);
        Assert.assertEquals(2, oneTabs.size());
        Assert.assertEquals(TabIdentifier.SQL, oneTabs.get(0).getTabIdentifier());
        Assert.assertEquals(sqlLabel, oneTabs.get(0).getComponent());
        Assert.assertEquals(TabIdentifier.OVERVIEW, oneTabs.get(1).getTabIdentifier());
        Assert.assertEquals(overviewLabel, oneTabs.get(1).getComponent());
        
    }

    /**
     * 
     */
    @Test(expected = IllegalStateException.class)
    public void addTabsTwiceFails() {
        final Label overviewLabel = new Label();
        final StubTab overviewStubTab = new StubTab(overviewLabel);
        final TabDescriptor overviewTab = new TabDescriptor(TabIdentifier.OVERVIEW, overviewStubTab);
        openList.addTab("one", overviewTab);

        openList.addTab("one", overviewTab);
    }

    /**
     * 
     */
    @SuppressWarnings("unchecked")
    @Test
    public void addTabFiresListener() {
        final Label overviewLabel = new Label();
        final StubTab overviewStubTab = new StubTab(overviewLabel);
        final TabDescriptor overviewTab = new TabDescriptor(TabIdentifier.OVERVIEW, overviewStubTab);

        final Observer<TabEvent> obs = EasyMock.createStrictMock(Observer.class);
        obs.eventOccurred(EasyMock.eq(new TabOpenedEvent(overviewTab)));
        EasyMock.replay(obs);

        openList.addTabEventObserver(obs);
        
        openList.addTab("one", overviewTab);

        EasyMock.verify(obs);
        Assert.assertTrue(openList.containsTab("one", TabIdentifier.OVERVIEW));
    }
}
