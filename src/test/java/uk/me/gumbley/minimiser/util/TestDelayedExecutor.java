package uk.me.gumbley.minimiser.util;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.me.gumbley.commoncode.concurrency.ThreadUtils;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.util.DelayedExecutor.Executable;
import uk.me.gumbley.minimiser.util.DelayedExecutor.ExecutableComparator;


/**
 * Tests the delayed executor.
 * 
 * @author matt
 *
 */
public final class TestDelayedExecutor extends LoggingTestCase {
    private static final Logger LOGGER = Logger
            .getLogger(TestDelayedExecutor.class);
    private static final String TEST_KEY = "test";
    private DelayedExecutor delayedExecutor;

    /**
     * 
     */
    @Before
    public void getDelayedExecutor() {
        delayedExecutor = new DelayedExecutor();
    }
  
    @Test
    public void testExecutableComparatorEqual() {
        // we're comparing one object against itself, since it might
        // be impossible to instantiate two, and have their trigger time
        // equal.
        Executable ex = new Executable(TEST_KEY, 50, null);
        ExecutableComparator comp = new ExecutableComparator();
        final int compare = comp.compare(ex, ex);
        LOGGER.info("equal gives " + compare);
        // usually a comparator would return 0, but TreeSet seems to
        // use the comparator's zero response to determine containment
        // in the set - which is wrong.
        Assert.assertTrue(0 == compare);
        Assert.assertTrue(ex.equals(ex));
    }

    @Test
    public void testExecutableComparatorBefore() {
        Executable early = new Executable("one", 50, null);
        Executable late = new Executable("two", 500, null);
        ExecutableComparator comp = new ExecutableComparator();
        final int compare = comp.compare(early, late);
        LOGGER.info("before gives " + compare);
        Assert.assertTrue(-1 == compare);
        Assert.assertFalse(early.equals(late));
    }

    @Test
    public void testExecutableComparatorAfter() {
        Executable early = new Executable("one", 50, null);
        Executable late = new Executable("two", 500, null);
        ExecutableComparator comp = new ExecutableComparator();
        final int compare = comp.compare(late, early);
        LOGGER.info("after gives " + compare);
        Assert.assertTrue(1 == compare);
        Assert.assertFalse(early.equals(late));
    }
    
    @Test
    public void testExecutableEqual() {
        Executable early = new Executable(TEST_KEY, 50, new Runnable() {
            public void run() {
            }});
        Executable late = new Executable(TEST_KEY, 500, new Runnable() {
            public void run() {
            }});
        Assert.assertTrue(early.equals(late));
    }

    @Test
    public void testExecutableInEqual() {
        Executable early = new Executable("Ee chim u jim sho", 50, new Runnable() {
            public void run() {
            }});
        Executable late = new Executable("kwamse imboku", 500, new Runnable() {
            public void run() {
            }});
        Assert.assertFalse(early.equals(late));
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
        ThreadUtils.waitNoInterruption(250);
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
        ThreadUtils.waitNoInterruption(250);
        Assert.assertEquals("Delayed Executor", name[0]);
    }
    
    @Test(timeout=5000)
    public void testMultipleSequentialImmediateSubmissionsAreAllExecutedImmediately() {
        // it should not block after removing a Runnable
        final AtomicBoolean run0 = new AtomicBoolean(false);
        final AtomicBoolean run1 = new AtomicBoolean(false);
        final AtomicBoolean run2 = new AtomicBoolean(false);
        Runnable r0 = new Runnable() {
            public void run() {
                run0.set(true);
            }
        };
        Runnable r1 = new Runnable() {
            public void run() {
                run1.set(true);
            }
        };
        Runnable r2 = new Runnable() {
            public void run() {
                run2.set(true);
            }
        };
        final long start = System.currentTimeMillis();
        delayedExecutor.submit("test1", 0L, r0);
        //Thread.yield();
        delayedExecutor.submit("test2", 0L, r1);
        //Thread.yield();
        delayedExecutor.submit("test3", 0L, r2);
        //Thread.yield();
        ThreadUtils.waitNoInterruption(1000);
        LOGGER.info("testing");
        Assert.assertTrue(run0.get());
        Assert.assertTrue(run1.get());
        Assert.assertTrue(run2.get());
        final long stop = System.currentTimeMillis();
        Assert.assertTrue(stop - start < 2000);
    }
}
