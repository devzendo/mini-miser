package org.devzendo.minimiser.wiring.lifecycle;

import org.devzendo.minimiser.lifecycle.Lifecycle;
import org.devzendo.minimiser.springloader.SpringLoader;
import org.devzendo.minimiser.startupqueue.StartupQueueHelper;

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
