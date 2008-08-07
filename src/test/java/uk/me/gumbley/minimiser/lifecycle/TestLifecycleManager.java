package uk.me.gumbley.minimiser.lifecycle;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import uk.me.gumbley.minimiser.springloader.ApplicationContext;
import uk.me.gumbley.minimiser.springloader.SpringLoaderUnittestCase;


/**
 * Tests for lifecycle management.
 * 
 * @author matt
 *
 */
@ApplicationContext("uk/me/gumbley/minimiser/lifecycle/LifecycleTestCase.xml")
public class TestLifecycleManager extends SpringLoaderUnittestCase {
    
    private LifecycleManager lifecycleManager;

    /**
     * 
     */
    public void getSimpleTestPrerequisites() {
        lifecycleManager = getSpringLoader().getBean("lifecycleManager", LifecycleManager.class);
    }
    
    /**
     * 
     */
    @Test
    public void haveLivecycleManager() {
        getSimpleTestPrerequisites();
        
        Assert.assertNotNull(lifecycleManager);
    }
    
    /**
     * 
     */
    @Test
    public void verifyTestResourcesGetWired() {
        getSimpleTestPrerequisites();
        
        final Lifecycle one = getSpringLoader().getBean("one", Lifecycle.class);
        Assert.assertNotNull(one);
        Assert.assertTrue(one instanceof OneLifecycle);
        final OneLifecycle oneLifecycle = (OneLifecycle) one;
        Assert.assertEquals("ctor", oneLifecycle.getState());
        
        // Two gets given a one
        final Lifecycle two = getSpringLoader().getBean("two", Lifecycle.class);
        Assert.assertNotNull(two);
        Assert.assertTrue(two instanceof TwoLifecycle);
        final TwoLifecycle twoLifecycle = (TwoLifecycle) two;
        Assert.assertEquals("ctor", twoLifecycle.getState());
        Assert.assertSame(one, twoLifecycle.getOne());
    }
    
    /**
     * 
     */
    @Test
    public void lifeCycleManagerGetsRightConfig() {
        getSimpleTestPrerequisites();
        
        final List<String> beanNames = lifecycleManager.getLifecycleBeanNames();
        Assert.assertNotNull(beanNames);
        Assert.assertEquals(1, beanNames.size());
        Assert.assertEquals("two", beanNames.get(0));
    }
    
    /**
     * 
     */
    @Test
    public void instantiationOfLifecycles() {
        getSimpleTestPrerequisites();
        
        final Lifecycle two = getSpringLoader().getBean("two", Lifecycle.class);
        Assert.assertSame(two, lifecycleManager.getLifecycle("two"));
    }
    
    /**
     * 
     */
    @Test
    public void startupStartsUpAndShutdownShutsDown() {
        getSimpleTestPrerequisites();
        
        final Lifecycle two = getSpringLoader().getBean("two", Lifecycle.class);
        final TwoLifecycle twoLifecycle = (TwoLifecycle) two;
        Assert.assertEquals("ctor", twoLifecycle.getState());
        
        lifecycleManager.startup();
        
        Assert.assertEquals("started", twoLifecycle.getState());
        
        lifecycleManager.shutdown();

        Assert.assertEquals("shut down", twoLifecycle.getState());
    }

    /**
     * this lifecycle manager has a bad bean - nonexistant 
     */
    @Test
    public void dontStartupBadBeans() {
        lifecycleManager = getSpringLoader().getBean("badLoadLifecycleManager", LifecycleManager.class);
        Assert.assertNotNull(lifecycleManager);
        Assert.assertEquals(0, lifecycleManager.getLifecycleBeanNames().size());
    }
    
    /**
     * Tests for correct sequencing of startup and shutdown (forward and
     * reverse.
     */
    @Test
    public void startupAndShutdownSequence() {
        lifecycleManager = getSpringLoader().getBean("orderingLifecycleManager", LifecycleManager.class);
        Assert.assertNotNull(lifecycleManager);
        
        final OrderMonitor orderMonitor = getSpringLoader().getBean("orderMonitor", OrderMonitor.class);
        Assert.assertNotNull(orderMonitor);
        
        final List<String> initialOrdering = orderMonitor.getOrdering();
        Assert.assertEquals(0, initialOrdering.size());
        
        lifecycleManager.startup();
        
        final List<String> startupOrdering = orderMonitor.getOrdering();
        Assert.assertEquals(3, startupOrdering.size());
        Assert.assertEquals("a startup", startupOrdering.get(0));
        Assert.assertEquals("b startup", startupOrdering.get(1));
        Assert.assertEquals("c startup", startupOrdering.get(2));
        
        orderMonitor.reset();

        lifecycleManager.shutdown();
        
        final List<String> shutdownOrdering = orderMonitor.getOrdering();
        Assert.assertEquals(3, shutdownOrdering.size());
        Assert.assertEquals("c shutdown", shutdownOrdering.get(0));
        Assert.assertEquals("b shutdown", shutdownOrdering.get(1));
        Assert.assertEquals("a shutdown", shutdownOrdering.get(2));
        
    }
}
