package org.devzendo.minimiser.wiring.lifecycle;

import org.devzendo.minimiser.gui.dialog.dstamessage.DSTAMessageHelper;
import org.devzendo.minimiser.lifecycle.Lifecycle;
import org.devzendo.minimiser.springloader.SpringLoader;

/**
 * Initialises the DSTA Message Helper toolkit.
 * 
 * @author matt
 *
 */
public final class DSTAMessageHelperInitialisingLifecycle implements Lifecycle {
    private final SpringLoader springLoader;

    /**
     * Stash the SpringLoader for passing on to the toolkit.
     * @param loader the SpringLoader
     */
    public DSTAMessageHelperInitialisingLifecycle(final SpringLoader loader) {
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
        DSTAMessageHelper.initialise(springLoader);
    }
}
