package uk.me.gumbley.minimiser.wiring.lifecycle;

import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.minimiser.lifecycle.LifecycleManager;
import uk.me.gumbley.minimiser.springloader.ApplicationContext;
import uk.me.gumbley.minimiser.springloader.SpringLoaderUnittestCase;
import uk.me.gumbley.minimiser.startupqueue.StartupQueue;

/**
 * Tests the StartupQueue that's controlled as a Lifecycle.
 * 
 * @author matt
 *
 */
@ApplicationContext("uk/me/gumbley/minimiser/wiring/lifecycle/StartupQueueLifecycleTestCase.xml")
public final class TestStartupQueueLifecycle extends SpringLoaderUnittestCase {

    private LifecycleManager lifecycleManager;
    private StartupQueue startupQueue;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        lifecycleManager = getSpringLoader().getBean("lifecycleManager", LifecycleManager.class);
        Assert.assertNotNull(lifecycleManager);
        
        startupQueue = getSpringLoader().getBean("startupQueue", StartupQueue.class);
        Assert.assertNotNull(startupQueue);
    }

    /**
     * 
     */
    @Test(timeout = 5000)
    public void taskExecutedOnStartup() {
        final AtomicBoolean executed = new AtomicBoolean(false);
        final Runnable task = new Runnable() {
            public void run() {
                executed.set(true);
            }
        };
        startupQueue.addRunnable(task);
        
        Assert.assertFalse(executed.get());
        
        lifecycleManager.startup();
        
        startupQueue.join();
        
        Assert.assertTrue(executed.get());
    }
}
