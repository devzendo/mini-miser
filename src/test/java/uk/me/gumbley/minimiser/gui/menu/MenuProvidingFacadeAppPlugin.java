package uk.me.gumbley.minimiser.gui.menu;

import java.util.List;

import uk.me.gumbley.minimiser.plugin.ApplicationPlugin;
import uk.me.gumbley.minimiser.plugin.facade.providemenu.MenuProviding;
import uk.me.gumbley.minimiser.plugin.facade.providemenu.MenuProvidingFacade;
import uk.me.gumbley.minimiser.springloader.SpringLoader;

/**
 * A plugin that customises the ApplicationMenu.
 *
 * @author matt
 *
 */
public final class MenuProvidingFacadeAppPlugin implements ApplicationPlugin, MenuProviding {
    private final MenuProvidingFacade mMenuProvidingFacade;

    /**
     *
     */
    public MenuProvidingFacadeAppPlugin() {
        mMenuProvidingFacade = new StubMenuProvidingFacade();
    }

    /**
     * {@inheritDoc}
     */
    public String getAboutDetailsResourcePath() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getChangeLogResourcePath() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getDevelopersContactDetails() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getFullLicenceDetailsResourcePath() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getIntroPanelBackgroundGraphicResourcePath() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getShortLicenseDetails() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getUpdateSiteBaseURL() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getApplicationContextResourcePaths() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "MenuProvidingFacadeAppPlugin";
    }

    /**
     * {@inheritDoc}
     */
    public String getSchemaVersion() {
        return "1";
    }

    /**
     * {@inheritDoc}
     */
    public SpringLoader getSpringLoader() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getVersion() {
        return "0.1";
    }

    /**
     * {@inheritDoc}
     */
    public void setSpringLoader(final SpringLoader springLoader) {
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() {
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getAfter() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getBefore() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public MenuProvidingFacade getMenuProvidingFacade() {
        return mMenuProvidingFacade;
    }
}
