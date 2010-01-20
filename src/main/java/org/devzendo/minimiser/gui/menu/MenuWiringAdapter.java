package org.devzendo.minimiser.gui.menu;

/**
 * Implementations of this are instantiated and wires during menu initialisation
 * by the MenuMediator, to adapt various system events into menu state changes.
 * @author matt
 *
 */
public interface MenuWiringAdapter {
    /**
     * Called after instantiation to perform the wiring to the system
     * components that have been injected into this MenuWiringAdapter
     * during construction.
     */
    void connectWiring();
}
