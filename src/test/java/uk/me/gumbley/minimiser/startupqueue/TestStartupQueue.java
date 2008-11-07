package uk.me.gumbley.minimiser.startupqueue;

import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.util.OrderMonitor;


/**
 * Tests the startup queue.
 * 
 * @author matt
 *
 */
public final class TestStartupQueue extends LoggingTestCase {
    
    private StartupQueue startupQueue;
    private OrderMonitor orderMonitor;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        startupQueue = new StartupQueue();
    }

    /**
     * 
     */
    @Test
    public void addedTaskGetsExecutedOnExecuteQueue() {
        orderMonitor = new OrderMonitor();
        
        final Runnable one = new Runnable() {
            public void run() {
                orderMonitor.add("one");
            }
        };
        startupQueue.addRunnable(one);
        final Runnable two = new Runnable() {
            public void run() {
                orderMonitor.add("two");
            }
        };
        startupQueue.addRunnable(two);
        
        startupQueue.executeQueue();
        
        startupQueue.join();
        
        final List<String> ordering = orderMonitor.getOrdering();
        Assert.assertEquals(2, ordering.size());
        Assert.assertEquals("one", ordering.get(0));
        Assert.assertEquals("two", ordering.get(1));
    }
    
    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void cantAddNull() {
        startupQueue.addRunnable(null);
    }
    
    /**
     * 
     */
    @Test
    public void addedTaskRunsOnOwnQueue() {
        final Thread testRunnerThread = Thread.currentThread();
        final Thread[] runningThread = new Thread[] {null};
        final Runnable one = new Runnable() {
            public void run() {
                runningThread[0] = Thread.currentThread();
            }
        };
        
        startupQueue.addRunnable(one);
        
        startupQueue.executeQueue();

        startupQueue.join();

        Assert.assertNotNull(runningThread[0]);
        Assert.assertNotSame(testRunnerThread, runningThread[0]);
    }
}
