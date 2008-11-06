package uk.me.gumbley.minimiser.startupqueue;

import org.junit.Test;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;


/**
 * Tests the startup queue.
 * 
 * @author matt
 *
 */
public final class TestStartupQueue extends LoggingTestCase {
    
    private StartupQueue startupQueue;

    @Test
    public void addedTaskGetsExecutedOnStartup() {
        startupQueue = new StartupQueue();
    }
}
