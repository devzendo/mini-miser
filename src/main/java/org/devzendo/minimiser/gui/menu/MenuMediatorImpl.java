package org.devzendo.minimiser.gui.menu;

import java.util.List;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.springbeanlistloader.AbstractSpringBeanListLoaderImpl;
import org.devzendo.minimiser.springloader.SpringLoader;

/**
 * Mediates between application events and menu updates.
 * 
 * @author matt
 *
 */
public final class MenuMediatorImpl implements MenuMediator {
    private static final Logger LOGGER = Logger
            .getLogger(MenuMediatorImpl.class);
    private SpringLoader springLoader;
    private final Menu menu;
    private List<String> menuWiringAdapterBeanNames;

    /**
     * Create a Mediator between application events and the menu
     * @param loader the SpringLoader
     * @param leMenu ici un menu
     * @param beanNames the menu wiring adapter beans to load 
     */
    public MenuMediatorImpl(
            final SpringLoader loader,
            final Menu leMenu,
            final List<String> beanNames) {
        LOGGER.info("initialising MenuMediatorImpl");
        springLoader = loader;
        menu = leMenu;
        menuWiringAdapterBeanNames = beanNames;

        initialiseMenu();
        loadAndWireAdapters();
        LOGGER.info("initialised MenuMediatorImpl");
    }

    private void initialiseMenu() {
        menu.enableCloseMenu(false);
    }

    private void loadAndWireAdapters() {
        LOGGER.debug("Loading menu wiring adapter beans");
        final MenuWiringAdapterLoader loader = new MenuWiringAdapterLoader(springLoader, menuWiringAdapterBeanNames);
        LOGGER.debug("Menu wiring adapter beans loaded; wiring up");
        for (final String beanName : loader.getBeanNames()) {
            final MenuWiringAdapter adapter = loader.getBean(beanName);
            adapter.connectWiring();
        }
        LOGGER.debug("Components initialised");
    }
    
    /**
     * A SpringBeanListLoader that loads the menu wiring adapters given a list.
     * @author matt
     *
     */
    private static class MenuWiringAdapterLoader extends AbstractSpringBeanListLoaderImpl<MenuWiringAdapter> {

        /**
         * Load the menu wiring adapters from a list of beans
         * @param springLoader the SpringLoader
         * @param wiringAdapterBeanNames the list of wiring adapter bean names
         */
        public MenuWiringAdapterLoader(final SpringLoader springLoader, final List<String> wiringAdapterBeanNames) {
            super(springLoader, wiringAdapterBeanNames);
        }
    }
}
