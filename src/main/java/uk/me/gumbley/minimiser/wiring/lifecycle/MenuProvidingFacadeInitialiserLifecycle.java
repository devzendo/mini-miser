package uk.me.gumbley.minimiser.wiring.lifecycle;

import uk.me.gumbley.minimiser.gui.menu.MenuProvidingFacadeInitialiser;
import uk.me.gumbley.minimiser.lifecycle.Lifecycle;

/**
 * Initialises any plugins' MenuProvidingFacades.
 *
 * @author matt
 *
 */
public final class MenuProvidingFacadeInitialiserLifecycle implements Lifecycle {
    private final MenuProvidingFacadeInitialiser mInitialiser;

    /**
     * @param initialiser the MenuProvidingFacadeInitialiser
     */
    public MenuProvidingFacadeInitialiserLifecycle(final MenuProvidingFacadeInitialiser initialiser) {
        mInitialiser = initialiser;
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        mInitialiser.initialise();
    }
}
