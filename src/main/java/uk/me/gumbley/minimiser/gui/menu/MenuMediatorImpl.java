package uk.me.gumbley.minimiser.gui.menu;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.gui.MainFrameTitle;
import uk.me.gumbley.minimiser.gui.menu.helpers.ViewMenuHelper;
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;
import uk.me.gumbley.minimiser.gui.tabfactory.TabFactory;
import uk.me.gumbley.minimiser.opener.DatabaseOpenEvent;
import uk.me.gumbley.minimiser.opener.Opener;
import uk.me.gumbley.minimiser.opener.OpenerAdapter;
import uk.me.gumbley.minimiser.opener.OpenerAdapterFactory;
import uk.me.gumbley.minimiser.openlist.DatabaseClosedEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.DatabaseEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseListEmptyEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseOpenedEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseSwitchedEvent;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.opentablist.OpenTabList;
import uk.me.gumbley.minimiser.opentablist.TabDescriptor;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.prefs.PrefsEvent;
import uk.me.gumbley.minimiser.recentlist.RecentFilesList;
import uk.me.gumbley.minimiser.recentlist.RecentListChangedEvent;

/**
 * Mediates between application events and menu updates.
 * 
 * @author matt
 *
 */
public final class MenuMediatorImpl implements MenuMediator {
    private static final Logger LOGGER = Logger
            .getLogger(MenuMediatorImpl.class);
    private final Menu menu;
    private final OpenDatabaseList openDatabaseList;
    private final OpenTabList openTabList;
    private final RecentFilesList recentFilesList;
    private final Opener opener;
    private final OpenerAdapterFactory openerAdapterFactory;
    private final MainFrameTitle mainFrameTitle;
    private final Prefs prefs;
    private final TabFactory tabFactory;
    
    /**
     * Create a Mediator between application events and the menu
     * @param leMenu ici un menu
     * @param odl the open database list
     * @param otl the open tab list
     * @param recentFiles the recent files list
     * @param ope the opener
     * @param oaf the OpenerAdapterFactory
     * @param mainframetitle the main frame title controller
     * @param preferences the preferences
     * @param tf the TabFactory
     */
    public MenuMediatorImpl(final Menu leMenu, final OpenDatabaseList odl,
            final OpenTabList otl,
            final RecentFilesList recentFiles, final Opener ope,
            final OpenerAdapterFactory oaf, final MainFrameTitle mainframetitle,
            final Prefs preferences, final TabFactory tf) {
        LOGGER.info("initialising MenuMediatorImpl");
        menu = leMenu;
        openDatabaseList = odl;
        openTabList = otl;
        recentFilesList = recentFiles;
        opener = ope;
        openerAdapterFactory = oaf;
        mainFrameTitle = mainframetitle;
        prefs = preferences;
        tabFactory = tf;
        initialiseMenu();
        wireAdapters();
        LOGGER.info("initialised MenuMediatorImpl");
    }

    private void initialiseMenu() {
        menu.enableCloseMenu(false);
    }

    private void wireAdapters() {
        // ODL -> menu and panels
        openDatabaseList.addDatabaseEventObserver(new DatabaseEventObserver());
        // menu -> ODL (which'll talk back to the menu)
        menu.addDatabaseSwitchObserver(new DatabaseSwitchObserver());
        // menu -> opener (for the recent list)
        // TODO NONTDD
        menu.addOpenRecentObserver(new OpenRecentObserver());
        // recent list -> menu
        recentFilesList.addRecentListEventObserver(new RecentListEventObserver());
        // opener -> ODL
        opener.addDatabaseOpenObserver(new DatabaseOpenEventObserver());
        // prefs -> menu
        prefs.addChangeListener(new PrefsEventObserver());
        // menu -> tab opener (the view menu)
        menu.addViewChoiceObserver(new ViewChoiceObserver());
    }
    
    /**
     * Adapts between DatabaseEvents and Menu updates.
     * 
     * @author matt
     *
     */
    private final class DatabaseEventObserver implements Observer<DatabaseEvent> {
        /**
         * {@inheritDoc}
         */
        public void eventOccurred(final DatabaseEvent event) {
            if (event instanceof DatabaseListEmptyEvent) {
                menu.emptyDatabaseList();
            } else if (event instanceof DatabaseClosedEvent) {
                final DatabaseClosedEvent dce = (DatabaseClosedEvent) event;
                final boolean anyDatabasesOpen = openDatabaseList.getNumberOfDatabases() > 0;
                menu.enableCloseMenu(anyDatabasesOpen);
                if (!anyDatabasesOpen) {
                    mainFrameTitle.clearCurrentDatabaseName();
                }
                menu.removeDatabase(dce.getDatabaseName());
            } else if (event instanceof DatabaseOpenedEvent) {
                final DatabaseOpenedEvent doe = (DatabaseOpenedEvent) event;
                menu.enableCloseMenu(true);
                recentFilesList.add(doe.getDatabaseDescriptor());
                menu.addDatabase(doe.getDatabaseName());
            } else if (event instanceof DatabaseSwitchedEvent) {
                final String databaseName = ((DatabaseSwitchedEvent) event).getDatabaseName();
                menu.switchDatabase(databaseName);
                mainFrameTitle.setCurrentDatabaseName(databaseName);
            } else {
                throw new IllegalStateException("Unexpected a " + event.getClass().getSimpleName());
            }
        }
    }
    
    /**
     * Adapts between window menu changes and open database switching
     * 
     * @author matt
     *
     */
    private final class DatabaseSwitchObserver implements Observer<DatabaseNameChoice> {
        /**
         * {@inheritDoc}
         */
        public void eventOccurred(final DatabaseNameChoice windowMenuChoice) {
            final String databaseName = windowMenuChoice.getDatabaseName();
            openDatabaseList.switchDatabase(databaseName);
        }
    }

    /**
     * Adapts between recent list updates and the menu, to update the recent
     * list menu.
     * 
     * @author matt
     *
     */
    private final class RecentListEventObserver implements Observer<RecentListChangedEvent> {
        /**
         * {@inheritDoc}
         */
        public void eventOccurred(final RecentListChangedEvent observableEvent) {
            menu.refreshRecentList(recentFilesList.getRecentDatabases());
        }
    }

    /**
     * Adapts between recent list choices and the opening/switching mechanism.
     * If already opened, switch to it.
     * If not opened, open it.
     * 
     * @author matt
     *
     */
    private final class OpenRecentObserver implements Observer<DatabaseNameAndPathChoice> {
        /**
         * {@inheritDoc}
         */
        public void eventOccurred(final DatabaseNameAndPathChoice observableEvent) {
            final String databaseName = observableEvent.getDatabaseName();
            if (openDatabaseList.containsDatabase(new DatabaseDescriptor(databaseName))) {
                openDatabaseList.switchDatabase(databaseName);
            } else {
                final OpenerAdapter openerAdapter = openerAdapterFactory.createOpenerAdapter(databaseName);
                opener.openDatabase(databaseName, observableEvent.getDatabasePath(), openerAdapter);
            }
        }
    }
    
    /**
     * Adapts between the Opener opening a database and it being added to the
     * OpenDatabaseList.
     * 
     * @author matt
     *
     */
    private final class DatabaseOpenEventObserver implements Observer<DatabaseOpenEvent> {
        /**
         * {@inheritDoc}
         */
        public void eventOccurred(final DatabaseOpenEvent observableEvent) {
            openDatabaseList.addOpenedDatabase(observableEvent.getDatabase());
        }
    }
    
    /**
     * Adapts between the prefs change events and the menu updating the view
     * menu.
     * 
     * @author matt
     *
     */
    private final class PrefsEventObserver implements Observer<PrefsEvent> {
        /**
         * {@inheritDoc}
         */
        public void eventOccurred(final PrefsEvent observableEvent) {
            LOGGER.debug("Prefs event: " + observableEvent.getPrefsSection());
            if (observableEvent.getPrefsSection() != Prefs.PrefsSection.HIDDEN_TABS) {
                return;
            }
            ViewMenuHelper.updateViewMenuFromPrefsHiddenTabs(prefs, menu);
        }
    }
    
    /**
     * Adapts between view menu and tab opener to open/close tabs.
     * 
     * @author matt
     *
     */
    private class ViewChoiceObserver implements Observer<ViewMenuChoice> {
        /**
         * {@inheritDoc}
         */
        public void eventOccurred(final ViewMenuChoice observableEvent) {
            LOGGER.debug((observableEvent.isOpened() ? "Opening" : "Closing") 
                + " tab ID " + observableEvent.getTabId() + " for database " 
                + observableEvent.getDatabaseDescriptor());
            if (observableEvent.isOpened()) {
                openTab(observableEvent);
            } else {
                closeTab(observableEvent);
            }
        }

        private void openTab(final ViewMenuChoice observableEvent) {
            final List<TabIdentifier> tabListOfOne = new ArrayList<TabIdentifier>();
            tabListOfOne.add(observableEvent.getTabId());
            final List<TabDescriptor> loadedTab = tabFactory.loadTabs(observableEvent.getDatabaseDescriptor(), tabListOfOne);
            
            ViewMenuHelper.addTabToTabbedPaneAndOpenTabList(openTabList, observableEvent.getDatabaseDescriptor(), loadedTab.get(0));
        }

        private void closeTab(final ViewMenuChoice observableEvent) {
            // find this tab descriptor
            final DatabaseDescriptor databaseDescriptor = observableEvent.getDatabaseDescriptor();
            final TabIdentifier tabIdentifier = observableEvent.getTabId();
            
            final List<TabDescriptor> tabsForDatabase = openTabList.getTabsForDatabase(databaseDescriptor.getDatabaseName());
            final List<TabDescriptor> tabListToClose = new ArrayList<TabDescriptor>();
            for (final TabDescriptor tabDescriptor : tabsForDatabase) {
                if (tabDescriptor.getTabIdentifier() == tabIdentifier) {
                    tabListToClose.add(tabDescriptor);
                }
            }

            if (tabListToClose.size() > 0) {
                ViewMenuHelper.removeTabFromTabbedPaneAndOpenTabList(openTabList, observableEvent.getDatabaseDescriptor(), tabListToClose.get(0));
            
                tabFactory.closeTabs(databaseDescriptor, tabListToClose);
            }
        }
    }
}
