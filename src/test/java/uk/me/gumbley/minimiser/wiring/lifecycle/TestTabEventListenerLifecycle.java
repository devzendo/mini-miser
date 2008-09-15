package uk.me.gumbley.minimiser.wiring.lifecycle;

import java.awt.Label;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;
import uk.me.gumbley.minimiser.lifecycle.LifecycleManager;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.opentablist.OpenTabList;
import uk.me.gumbley.minimiser.opentablist.StubTab;
import uk.me.gumbley.minimiser.opentablist.TabDescriptor;
import uk.me.gumbley.minimiser.springloader.ApplicationContext;
import uk.me.gumbley.minimiser.springloader.SpringLoaderUnittestCase;
import uk.me.gumbley.minimiser.util.OrderMonitor;

/**
 * Tests the TabEventListener that's managed as a Lifecycle.
 * 
 * @author matt
 *
 */
@ApplicationContext("uk/me/gumbley/minimiser/wiring/lifecycle/TabEventListenerLifecycleTestCase.xml")
public final class TestTabEventListenerLifecycle extends SpringLoaderUnittestCase {

    private LifecycleManager lifecycleManager;
    private OpenTabList openTabList;
    private OrderMonitor orderMonitor;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        lifecycleManager = getSpringLoader().getBean("lifecycleManager", LifecycleManager.class);
        openTabList = getSpringLoader().getBean("openTabList", OpenTabList.class);
        orderMonitor = getSpringLoader().getBean("orderMonitor", OrderMonitor.class);
        
        Assert.assertNotNull(lifecycleManager);
        Assert.assertNotNull(openTabList);
        Assert.assertNotNull(orderMonitor);
    }

    /**
     * 
     */
    @Test
    public void nothingPropagatedBeforeStartup() {
        final Label label = new Label();
        final StubTab stubTab = new StubTab(label);
        final TabDescriptor one = new TabDescriptor(TabIdentifier.SQL, stubTab);
        openTabList.addTab(new DatabaseDescriptor("one"), one);
        final List<String> addOrdering = orderMonitor.getOrdering();
        Assert.assertEquals(0, addOrdering.size());
    }
    
    /**
     * 
     */
    @Test
    public void eventsPropagatedAfterStartup() {
        lifecycleManager.startup();

        // not exhaustive - see the TabEventLifecycleManager for more.
        final Label label = new Label();
        final StubTab stubTab = new StubTab(label);
        final TabDescriptor one = new TabDescriptor(TabIdentifier.SQL, stubTab);
        openTabList.addTab(new DatabaseDescriptor("one"), one);
        
        final List<String> addOrdering = orderMonitor.getOrdering();
        Assert.assertEquals(1, addOrdering.size());
    }
    
    /**
     * 
     */
    @Test
    public void nothingPropagatedAfterShutdown() {
        lifecycleManager.startup();

        final Label label = new Label();
        final StubTab stubTab = new StubTab(label);
        final TabDescriptor one = new TabDescriptor(TabIdentifier.SQL, stubTab);
        final DatabaseDescriptor dbDescriptor = new DatabaseDescriptor("db");
        openTabList.addTab(dbDescriptor, one);

        final List<String> addOrdering = orderMonitor.getOrdering();
        Assert.assertEquals(1, addOrdering.size());

        lifecycleManager.shutdown();
        orderMonitor.reset();

        final Label label2 = new Label();
        final StubTab stubTab2 = new StubTab(label2);
        final TabDescriptor two = new TabDescriptor(TabIdentifier.OVERVIEW, stubTab2);
        openTabList.addTab(dbDescriptor, two);

        final List<String> addAgainOrdering = orderMonitor.getOrdering();
        Assert.assertEquals(0, addAgainOrdering.size());
    }
}
