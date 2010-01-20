package org.devzendo.minimiser.gui.menu;

/**
 * This is the amount of control MenuProvidingFacades have over the Menu.
 *
 * @author matt
 *
 */
public interface MenuFacade {
    /**
     * Rebuild the entire menu bar, all menus.
     */
    void rebuildEntireMenu();

    /**
     * Rebuild only the File menu, perhaps if the recent list has changed.
     */
    void rebuildFileMenu();

    /**
     * Rebuild only the view menu, perhaps if the list of custom views has changed.
     */
    void rebuildViewMenu();
}
