package uk.me.gumbley.minimiser.opentablist;

import java.awt.Label;
import java.util.List;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.springloader.ApplicationContext;
import uk.me.gumbley.minimiser.springloader.SpringLoaderUnittestCase;
import uk.me.gumbley.minimiser.util.OrderMonitor;


/**
 * Tests the ability to listen to the OpenTabList via a list of listener
 * beans.
 * 
 * @author matt
 *
 */
@ApplicationContext("uk/me/gumbley/minimiser/opentablist/TabEventListenerTestCase.xml")
public final class TestTabEventListenerManager extends SpringLoaderUnittestCase {
    private static final Logger LOGGER = Logger
            .getLogger(TestTabEventListenerManager.class);
    
    private TabEventListenerManager listenerManager;
    private OpenTabList openTabList;
    private OrderMonitor orderMonitor;

    /**
     * 
     */
    @Before
    public void getSimpleTestPrerequisites() {
        listenerManager = getSpringLoader().getBean("listenerManager", TabEventListenerManager.class);
        openTabList = getSpringLoader().getBean("openTabList", OpenTabList.class);
        orderMonitor = getSpringLoader().getBean("orderMonitor", OrderMonitor.class);
        
        Assert.assertNotNull(listenerManager);
        Assert.assertNotNull(openTabList);
        Assert.assertNotNull(orderMonitor);
    }
    
    /**
     * 
     */
    @Test
    public void nothingPropagatedUntilWired() {
        final Label label = new Label();
        final StubTab stubTab = new StubTab(label);
        final TabDescriptor tab = new TabDescriptor(TabIdentifier.SQL, stubTab);
        openTabList.addTab(new DatabaseDescriptor("one"), tab);
        final List<String> addOrdering = orderMonitor.getOrdering();
        Assert.assertEquals(0, addOrdering.size());
    }
    
    /**
     * 
     */
    @Test
    public void eventsArePropagated() {
        listenerManager.wire();
        
        final List<String> initialOrdering = orderMonitor.getOrdering();
        Assert.assertEquals(0, initialOrdering.size());
        final List<TabDescriptor> initialTabsForDatabase = openTabList.getTabsForDatabase("one");
        Assert.assertNotNull(initialTabsForDatabase);
        Assert.assertEquals(0, initialTabsForDatabase.size());

        final Label label = new Label();
        final StubTab stubTab = new StubTab(label);
        final TabDescriptor tab = new TabDescriptor(TabIdentifier.SQL, stubTab);
        openTabList.addTab(new DatabaseDescriptor("one"), tab);

        final List<TabDescriptor> tabsForDatabase = openTabList.getTabsForDatabase("one");
        Assert.assertNotNull(tabsForDatabase);
        Assert.assertEquals(1, tabsForDatabase.size());
        Assert.assertEquals(TabIdentifier.SQL, tabsForDatabase.get(0).getTabIdentifier());
        Assert.assertSame(label, tabsForDatabase.get(0).getTab().getComponent());
        
        final List<String> addOrdering = orderMonitor.getOrdering();
        LOGGER.debug("addOrdering is '" + addOrdering + "'");
        Assert.assertEquals(2, addOrdering.size());
        Assert.assertEquals("one TabOpenedEvent", addOrdering.get(0));
        Assert.assertEquals("two TabOpenedEvent", addOrdering.get(1));
        
        // TODO add tests for switch ordering in here
    }
    
    /**
     * 
     */
    @Test
    public void nothingPropagatedAfterUnwiring() {
        listenerManager.wire();

        final Label label = new Label();
        final StubTab stubTab = new StubTab(label);
        final TabDescriptor tab = new TabDescriptor(TabIdentifier.SQL, stubTab);
        openTabList.addTab(new DatabaseDescriptor("one"), tab);

        final List<String> addOrdering = orderMonitor.getOrdering();
        Assert.assertEquals(2, addOrdering.size());

        listenerManager.unwire();
        orderMonitor.reset();


        final Label label2 = new Label();
        final StubTab stubTab2 = new StubTab(label2);
        final TabDescriptor tab2 = new TabDescriptor(TabIdentifier.OVERVIEW, stubTab2);
        openTabList.addTab(new DatabaseDescriptor("two"), tab2);

        final List<String> addAgainOrdering = orderMonitor.getOrdering();
        Assert.assertEquals(0, addAgainOrdering.size());
    }
}
