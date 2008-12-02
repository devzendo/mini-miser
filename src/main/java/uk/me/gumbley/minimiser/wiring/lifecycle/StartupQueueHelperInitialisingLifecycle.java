package uk.me.gumbley.minimiser.wiring.lifecycle;

import uk.me.gumbley.minimiser.lifecycle.Lifecycle;
import uk.me.gumbley.minimiser.springloader.SpringLoader;
import uk.me.gumbley.minimiser.startupqueue.StartupQueueHelper;

/**
 * Initialises the Startup Queue Helper toolkit.
 * 
 * @author matt
 *
 */
public final class StartupQueueHelperInitialisingLifecycle implements Lifecycle {
    private final SpringLoader springLoader;

    /**
     * Stash the SpringLoader for passing on to the toolkit.
     * @param loader the SpringLoader
     */
    public StartupQueueHelperInitialisingLifecycle(final SpringLoader loader) {
        this.springLoader = loader;
    }
    
    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        // nothing
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        StartupQueueHelper.initialise(springLoader);
    }
}
