package uk.me.gumbley.minimiser.wiring.lifecycle;

import uk.me.gumbley.minimiser.lifecycle.Lifecycle;
import uk.me.gumbley.minimiser.messagequeue.MessageQueueHelper;
import uk.me.gumbley.minimiser.springloader.SpringLoader;

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
