package uk.me.gumbley.minimiser.wiring.menu;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.gui.menu.Menu;
import uk.me.gumbley.minimiser.gui.menu.MenuWiringAdapter;
import uk.me.gumbley.minimiser.gui.menu.ViewMenuChoice;
import uk.me.gumbley.minimiser.gui.menu.helpers.ViewMenuHelper;
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;
import uk.me.gumbley.minimiser.gui.tabfactory.TabFactory;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.opentablist.OpenTabList;
import uk.me.gumbley.minimiser.opentablist.TabDescriptor;

/**
 * Adapts between view menu and tab opener to open/close tabs.
 * 
 * @author matt
 *
 */
public final class MenuViewChoiceObserver implements MenuWiringAdapter, Observer<ViewMenuChoice> {
    private static final Logger LOGGER = Logger
        .getLogger(MenuViewChoiceObserver.class);
    
    private final Menu menu;
    private final OpenTabList openTabList;
    private final TabFactory tabFactory;

    /**
     * Construct the adapter given other system objects for interaction.
     * @param leMenu the menu
     * @param tabList the open tab list
     * @param tabFact the tab factory
     */
    public MenuViewChoiceObserver(
            final Menu leMenu,
            final OpenTabList tabList,
            final TabFactory tabFact) {
                this.menu = leMenu;
                this.openTabList = tabList;
                this.tabFactory = tabFact;
    }

    /**
     * {@inheritDoc}
     */
    public void connectWiring() {
        // menu -> tab opener (the view menu)
        menu.addViewChoiceObserver(this);
    }
    
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
        final DatabaseDescriptor databaseDescriptor = observableEvent.getDatabaseDescriptor();
        final List<TabDescriptor> loadedTab = tabFactory.loadTabs(databaseDescriptor, tabListOfOne);
        
        final TabDescriptor tabDescriptor = loadedTab.get(0);
        ViewMenuHelper.addTabToTabbedPaneAndOpenTabList(openTabList, databaseDescriptor, tabDescriptor);
        ViewMenuHelper.switchToTab(databaseDescriptor, tabDescriptor);
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
