package org.devzendo.minimiser.wiring.lifecycle;

import java.util.List;

import org.devzendo.minimiser.lifecycle.LifecycleManager;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.OpenDatabaseList;
import org.devzendo.minimiser.springloader.ApplicationContext;
import org.devzendo.minimiser.springloader.SpringLoaderUnittestCase;
import org.devzendo.minimiser.util.OrderMonitor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the DatabaseEventListener that's managed as a Lifecycle.
 *
 * @author matt
 *
 */
@ApplicationContext("org/devzendo/minimiser/wiring/lifecycle/DatabaseEventListenerLifecycleTestCase.xml")
public final class TestDatabaseEventListenerLifecycle extends SpringLoaderUnittestCase {

    private LifecycleManager lifecycleManager;
    private OpenDatabaseList openDatabaseList;
    private OrderMonitor orderMonitor;

    /**
     *
     */
    @Before
    public void getPrerequisites() {
        lifecycleManager = getSpringLoader().getBean("lifecycleManager", LifecycleManager.class);
        openDatabaseList = getSpringLoader().getBean("openDatabaseList", OpenDatabaseList.class);
        orderMonitor = getSpringLoader().getBean("orderMonitor", OrderMonitor.class);

        Assert.assertNotNull(lifecycleManager);
        Assert.assertNotNull(openDatabaseList);
        Assert.assertNotNull(orderMonitor);
    }

    /**
     *
     */
    @Test
    public void nothingPropagatedBeforeStartup() {
        final DatabaseDescriptor one = new DatabaseDescriptor("one");
        openDatabaseList.addOpenedDatabase(one);
        final List<String> addOrdering = orderMonitor.getOrdering();
        Assert.assertEquals(0, addOrdering.size());
    }

    /**
     *
     */
    @Test
    public void eventsPropagatedAfterStartup() {
        lifecycleManager.startup();

        // not exhaustive - see the DatabaseEventLifecycleManager for more.
        final DatabaseDescriptor one = new DatabaseDescriptor("one");
        openDatabaseList.addOpenedDatabase(one);

        final List<String> addOrdering = orderMonitor.getOrdering();
        Assert.assertEquals(2, addOrdering.size());
    }

    /**
     *
     */
    @Test
    public void nothingPropagatedAfterShutdown() {
        lifecycleManager.startup();

        final DatabaseDescriptor one = new DatabaseDescriptor("one");
        openDatabaseList.addOpenedDatabase(one);
        final List<String> addOrdering = orderMonitor.getOrdering();
        Assert.assertEquals(2, addOrdering.size());

        lifecycleManager.shutdown();
        orderMonitor.reset();

        final DatabaseDescriptor two = new DatabaseDescriptor("two");
        openDatabaseList.addOpenedDatabase(two);

        final List<String> addAgainOrdering = orderMonitor.getOrdering();
        Assert.assertEquals(0, addAgainOrdering.size());
    }
}
