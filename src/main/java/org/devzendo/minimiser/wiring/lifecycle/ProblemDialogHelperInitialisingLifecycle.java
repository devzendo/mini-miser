package org.devzendo.minimiser.wiring.lifecycle;

import org.devzendo.minimiser.gui.dialog.problem.ProblemDialogHelper;
import org.devzendo.minimiser.lifecycle.Lifecycle;
import org.devzendo.minimiser.springloader.SpringLoader;

/**
 * Initialises the Problem Dialog Helper toolkit.
 * 
 * @author matt
 *
 */
public final class ProblemDialogHelperInitialisingLifecycle implements Lifecycle {
    private final SpringLoader springLoader;

    /**
     * Stash the SpringLoader for passing on to the toolkit.
     * @param loader the SpringLoader
     */
    public ProblemDialogHelperInitialisingLifecycle(final SpringLoader loader) {
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
        ProblemDialogHelper.initialise(springLoader);
    }
}
