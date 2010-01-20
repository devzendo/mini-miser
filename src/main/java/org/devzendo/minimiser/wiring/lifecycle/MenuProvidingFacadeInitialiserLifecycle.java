package org.devzendo.minimiser.wiring.lifecycle;

import org.devzendo.minimiser.gui.menu.MenuProvidingFacadeInitialiser;
import org.devzendo.minimiser.lifecycle.Lifecycle;

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
