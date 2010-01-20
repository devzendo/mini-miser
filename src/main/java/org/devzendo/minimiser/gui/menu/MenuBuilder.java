package org.devzendo.minimiser.gui.menu;


/**
 * Attaches all the ActionListeners to the relevant MenuItems, via
 * MenuWiring.
 *
 * @author matt
 *
 */
public interface MenuBuilder {
    /**
     * Build the menu by wiring it all up.
     */
    void build();
}
