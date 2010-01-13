package uk.me.gumbley.minimiser.gui.menu;

/**
 * This is the amount of control MenuProvidingFacades have over the Menu.
 *
 * @author matt
 *
 */
public interface MenuFacade {
    void rebuildEntireMenu();
    void rebuildFileMenu();
    void rebuildViewMenu();
}
