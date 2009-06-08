package uk.me.gumbley.minimiser.util;

import org.apache.log4j.Logger;

/**
 * A Toolkit for sleeping. Allows tests to speed time up.
 * @author matt
 *
 */
public final class Sleeper {
    private static final Logger LOGGER = Logger.getLogger(Sleeper.class);
    private final int fasterBy;

    /**
     * Create a Sleeper where time is some number of times faster than
     * reality. Useful for unit tests.
     * @param timesFaster the number of times that time is faster by
     */
    public Sleeper(final int timesFaster) {
        if (timesFaster <= 0) {
            final String warning = "Multiplier cannot be zero or negative";
            LOGGER.warn(warning);
            throw new IllegalArgumentException(warning);
        }
        this.fasterBy = timesFaster;
    }

    /**
     * Create a Sleeper that sleeps in real time. This is the normal Sleeper
     * used in the real system.
     */
    public Sleeper() {
        this.fasterBy = 1;
    }
    
    /**
     * Sleep for a number of milliseconds, modified by the speedup of this
     * Sleeper.
     * @param millis a number of milliseconds that would be slept for if this
     * Sleeper was sleeping in real time.
     */
    public void sleep(final long millis) {
        try {
            Thread.sleep(millis / fasterBy);
        } catch (final InterruptedException e) {
            final String warning = "Interupted whilst asleep";
            LOGGER.debug(warning); // this isn't serious?
        }
    }
}
