package uk.me.gumbley.minimiser.plugin.facade.providemenu;

/**
 * Plugins that need to customise the menu must implement this and provide
 * the facade.
 *
 * @author matt
 *
 */
public interface MenuProviding {
    /**
     * @return an instance of the facade; this can be called
     * several times during opening; perhaps this could be a
     * single instance that's returned?
     */
    MenuProvidingFacade getMenuProvidingFacade();
}
