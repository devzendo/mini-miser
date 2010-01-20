package org.devzendo.minimiser.util;

import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the Sleeper
 * @author matt
 *
 */
public final class TestSleeper {
    
    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void negativeSleeperDisallowed() {
        new Sleeper(-2);
    }

    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void zeroSleeperDisallowed() {
        new Sleeper(0);
    }
    
    private long sleepFor(final Sleeper sleeper, final long duration) {
        final long start = System.currentTimeMillis();
        sleeper.sleep(duration);
        return System.currentTimeMillis() - start;
    }

    /**
     * 
     */
    @Test(timeout = 8000)
    public void timeSpeedsUp() {
        final Sleeper fourTimesFaster = new Sleeper(4);
        final long dur = sleepFor(fourTimesFaster, 1000);
        Assert.assertTrue(dur >= 250 && dur <= 500); // upper bound dodgy?
    }
    
    /**
     * 
     */
    @Test(timeout = 4000)
    public void realTimeIsNormal() {
        final Sleeper normalTime = new Sleeper();
        final long dur = sleepFor(normalTime, 250);
        Assert.assertTrue(dur >= 250 && dur <= 500); // upper bound dodgy?
    }
}
