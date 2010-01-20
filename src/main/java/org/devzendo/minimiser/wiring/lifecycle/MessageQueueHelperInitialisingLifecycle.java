package org.devzendo.minimiser.wiring.lifecycle;

import org.devzendo.minimiser.lifecycle.Lifecycle;
import org.devzendo.minimiser.messagequeue.MessageQueueHelper;
import org.devzendo.minimiser.springloader.SpringLoader;

/**
 * Initialises the Message Queue Helper toolkit.
 * 
 * @author matt
 *
 */
public final class MessageQueueHelperInitialisingLifecycle implements Lifecycle {
    private final SpringLoader springLoader;

    /**
     * Stash the SpringLoader for passing on to the toolkit.
     * @param loader the SpringLoader
     */
    public MessageQueueHelperInitialisingLifecycle(final SpringLoader loader) {
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
        MessageQueueHelper.initialise(springLoader);
    }
}
