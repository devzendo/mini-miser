package uk.me.gumbley.minimiser.gui.menu;

import java.util.List;
import org.apache.log4j.Logger;
import uk.me.gumbley.minimiser.gui.MainFrameTitle;
import uk.me.gumbley.minimiser.gui.tabfactory.TabFactory;
import uk.me.gumbley.minimiser.opener.Opener;
import uk.me.gumbley.minimiser.opener.OpenerAdapterFactory;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.opentablist.OpenTabList;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.recentlist.RecentFilesList;
import uk.me.gumbley.minimiser.springbeanlistloader.AbstractSpringBeanListLoaderImpl;
import uk.me.gumbley.minimiser.springloader.SpringLoader;

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
    private final OpenDatabaseList openDatabaseList;
    private final OpenTabList openTabList;
    private final RecentFilesList recentFilesList;
    private final Opener opener;
    private final OpenerAdapterFactory openerAdapterFactory;
    private final MainFrameTitle mainFrameTitle;
    private final Prefs prefs;
    private final TabFactory tabFactory;
    private List<String> menuWiringAdapterBeanNames;
    
    /**
     * Create a Mediator between application events and the menu
     * @param loader the SpringLoader
     * @param leMenu ici un menu
     * @param odl the open database list
     * @param otl the open tab list
     * @param recentFiles the recent files list
     * @param ope the opener
     * @param oaf the OpenerAdapterFactory
     * @param mainframetitle the main frame title controller
     * @param preferences the preferences
     * @param tf the TabFactory
     * @param beanNames the menu wiring adapter beans to load 
     */
    public MenuMediatorImpl(
            final SpringLoader loader,
            final Menu leMenu, final OpenDatabaseList odl,
            final OpenTabList otl,
            final RecentFilesList recentFiles, final Opener ope,
            final OpenerAdapterFactory oaf, final MainFrameTitle mainframetitle,
            final Prefs preferences, final TabFactory tf,
            final List<String> beanNames) {
        LOGGER.info("initialising MenuMediatorImpl");
        springLoader = loader;
        menu = leMenu;
        openDatabaseList = odl;
        openTabList = otl;
        recentFilesList = recentFiles;
        opener = ope;
        openerAdapterFactory = oaf;
        mainFrameTitle = mainframetitle;
        prefs = preferences;
        tabFactory = tf;
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
