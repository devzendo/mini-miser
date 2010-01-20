package org.devzendo.minimiser.wiring.lifecycle;

import org.devzendo.minimiser.gui.dialog.welcome.WelcomeDialogHelper;
import org.devzendo.minimiser.lifecycle.Lifecycle;
import org.devzendo.minimiser.springloader.SpringLoader;

/**
 * Initialises the Welcome Dialog Helper toolkit.
 * 
 * @author matt
 *
 */
public final class WelcomeDialogHelperInitialisingLifecycle implements Lifecycle {
    private final SpringLoader springLoader;

    /**
     * Stash the SpringLoader for passing on to the toolkit.
     * @param loader the SpringLoader
     */
    public WelcomeDialogHelperInitialisingLifecycle(final SpringLoader loader) {
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
        WelcomeDialogHelper.initialise(springLoader);
    }
}
