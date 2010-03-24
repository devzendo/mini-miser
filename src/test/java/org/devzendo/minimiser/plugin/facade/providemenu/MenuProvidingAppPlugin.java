package org.devzendo.minimiser.plugin.facade.providemenu;

import java.util.List;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.gui.menu.ApplicationMenu;
import org.devzendo.minimiser.gui.menu.MenuFacade;
import org.devzendo.minimiser.gui.tab.TabIdentifier;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.DatabaseEvent;
import org.devzendo.minimiser.openlist.DatabaseOpenedEvent;
import org.devzendo.minimiser.openlist.OpenDatabaseList;
import org.devzendo.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;
import org.devzendo.minimiser.plugin.ApplicationPlugin;
import org.devzendo.minimiser.springloader.SpringLoader;

/**
 * Test app plugin for menu providing facade.
 *
 * @author matt
 *
 */
public final class MenuProvidingAppPlugin implements ApplicationPlugin, MenuProviding {
    private static final Logger LOGGER = Logger
            .getLogger(MenuProvidingAppPlugin.class);
    private final MenuProvidingFacade mMenuProvidingFacade;
    private boolean mInitialised;

    /**
     * Construct the app plugin
     */
    public MenuProvidingAppPlugin() {
        LOGGER.info("** in plugin CTOR");
        mMenuProvidingFacade = new MenuProvidingFacade() {
            public void initialise(
                    final ApplicationMenu globalApplicationMenu,
                    final OpenDatabaseList openDatabaseList,
                    final MenuFacade menuFacade) {
                mInitialised = true;
                LOGGER.info("** Adding database event observer");
                openDatabaseList.addDatabaseEventObserver(new Observer<DatabaseEvent>() {
                    public void eventOccurred(final DatabaseEvent observableEvent) {
                        LOGGER.info("** Event observed");
                        if (observableEvent instanceof DatabaseOpenedEvent) {
                            databaseOpened((DatabaseOpenedEvent) observableEvent);
                        }
                    }

                    private void databaseOpened(final DatabaseOpenedEvent observableEvent) {
                        LOGGER.info("** Database opened, adding menu");
                        final DatabaseDescriptor databaseDescriptor = observableEvent.getDatabaseDescriptor();
                        final ApplicationMenu applicationMenu =
                            (ApplicationMenu) databaseDescriptor.getAttribute(
                                AttributeIdentifier.ApplicationMenu);
                        applicationMenu.addViewMenuTabIdentifier(
                            new TabIdentifier("APPLICATION", "Application menu entry", false, 'a', "irrelevant", null));
                        globalApplicationMenu.addViewMenuTabIdentifier(
                            new TabIdentifier("GLOBAL", "Global menu entry", false, 'g', "irrelevant", null));
                        menuFacade.rebuildViewMenu();
                    }
                });
            }
        };
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
        return "MenuProvidingAppPlugin";
    }

    /**
     * {@inheritDoc}
     */
    public String getSchemaVersion() {
        return "1.0";
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
    public MenuProvidingFacade getMenuProvidingFacade() {
        LOGGER.info("** Returning facade");
        return mMenuProvidingFacade;
    }

    /**
     * @return true iff initialised
     */
    public boolean isInitialised() {
        return mInitialised;
    }
}
