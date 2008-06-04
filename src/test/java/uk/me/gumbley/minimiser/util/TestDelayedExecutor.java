package uk.me.gumbley.minimiser.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;


/**
 * Tests the delayed executor.
 * 
 * @author matt
 *
 */
public final class TestDelayedExecutor extends LoggingTestCase {
    private static final String TEST_KEY = "test";
    private DelayedExecutor delayedExecutor;

    /**
     * 
     */
    @Before
    public void getDelayedExecutor() {
        delayedExecutor = new DelayedExecutor();
    }
   
    /**
     * 
     */
    @Test(timeout=5000)
    public void testImmediateExecution() {
        final boolean[] run = new boolean[] {false};
        Runnable r = new Runnable() {
            public void run() {
                run[0] = true;
            }
        };
        delayedExecutor.submit(TEST_KEY, 0L, r);
        Assert.assertTrue(run[0]);
    }
    
    /**
     * 
     */
    @Test(timeout=5000)
    public void testExecutingOnDelayedExecutorThread() {
        final String[] name = new String[] {"change me"};
        Runnable r = new Runnable() {
            public void run() {
                name[0] = Thread.currentThread().getName();
            }
        };
        delayedExecutor.submit(TEST_KEY, 0L, r);
        Assert.assertEquals("Delayed Executor", name[0]);
    }
}
