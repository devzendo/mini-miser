package uk.me.gumbley.minimiser.wiring.databaseeventlistener;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.gui.menu.helpers.ViewMenuHelper;
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;
import uk.me.gumbley.minimiser.gui.tabfactory.TabFactory;
import uk.me.gumbley.minimiser.gui.tabpanemanager.TabListPrefs;
import uk.me.gumbley.minimiser.openlist.DatabaseClosedEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.DatabaseEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseOpenedEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;
import uk.me.gumbley.minimiser.opentablist.OpenTabList;
import uk.me.gumbley.minimiser.opentablist.TabDescriptor;


/**
 * A database has been opened, so create a JTabbedPane for it,
 * populate it with {the previously-open view tabs UNION the
 * permanent tabs} and add these tabs to the open tab list.
 * 
 * On close, remove the tabs from the open tab list.
 * 
 * @author matt
 *
 */
public final class TabPaneCreatingDatabaseEventListener implements Observer<DatabaseEvent> {
    private static final Logger LOGGER = Logger
        .getLogger(TabPaneCreatingDatabaseEventListener.class);   
    
    private final TabListPrefs prefs;
    private final TabFactory tabFactory;
    private final OpenTabList openTabList;
    
    // Used by the run-on-EDT code in createTabbedPaneOnEventThread
    private final Object lock = new Object();
    private JTabbedPane tabbedPane;

    

    /**
     * Construct the tab pane creator.
     * @param prefsStore the prefs, from which previous tab view lists will be
     * read
     * @param factory the TabFactory which will create all the above tabs
     * and add them to the OpenTabList
     * @param tabList the OpenTabList used to determine the insertion order
     * and that's added to when tabs are loaded
     */
    public TabPaneCreatingDatabaseEventListener(final TabListPrefs prefsStore,
            final TabFactory factory, final OpenTabList tabList) {
        prefs = prefsStore;
        tabFactory = factory;
        openTabList = tabList;
    }

    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final DatabaseEvent databaseEvent) {
        if (databaseEvent instanceof DatabaseOpenedEvent) {
            handleDatabaseOpenedEvent((DatabaseOpenedEvent) databaseEvent);
        } else if (databaseEvent instanceof DatabaseClosedEvent) {
            handleDatabaseClosedEvent((DatabaseClosedEvent) databaseEvent);
        }
    }

    private void handleDatabaseOpenedEvent(final DatabaseOpenedEvent openEvent) {
        // this is called on a background thread - Recent Opener,
        // Open Wizard background, or Lifecycle startup.
        assert !SwingUtilities.isEventDispatchThread();

        final DatabaseDescriptor databaseDescriptor = openEvent.getDatabaseDescriptor();
        
        // Create the JTabbedPane
        LOGGER.info("Creating tabbed pane for database '" + databaseDescriptor.getDatabaseName() + "'");
        final JTabbedPane databaseTabbedPane = createTabbedPaneOnEventThread(databaseDescriptor); 
        databaseDescriptor.setAttribute(AttributeIdentifier.TabbedPane, databaseTabbedPane);
        
        // Add this database to the open tab list, so we can calculate
        // insertion points for tabs on the JTabbedPane.
        LOGGER.debug("Adding the database to the Open Tab List");
        openTabList.addDatabase(databaseDescriptor);
        
        // Load the tabs
        LOGGER.info("Loading permanent and stored tabs for database '" + databaseDescriptor.getDatabaseName() + "'");
        final List<TabDescriptor> loadedTabDescriptors = loadPermanentAndStoredTabs(databaseDescriptor);

        LOGGER.debug(loadedTabDescriptors.size() + " tab(s) loaded; adding components to JTabbedPane");
        // Add each ones component into the JTabbedPane.
        for (TabDescriptor tabDescriptor : loadedTabDescriptors) {
            ViewMenuHelper.addTabToTabbedPaneAndOpenTabList(openTabList, databaseDescriptor, tabDescriptor);
        }
        LOGGER.debug("Finished adding components to JTabbedPane and handling DatabaseOpenedEvent");
    }

    private void handleDatabaseClosedEvent(final DatabaseClosedEvent closedEvent) {
        // this is called on a background thread - Closer or Lifecycle shutdown.
        assert !SwingUtilities.isEventDispatchThread();
        
        final String databaseName = closedEvent.getDatabaseName();
        final DatabaseDescriptor databaseDescriptor = closedEvent.getDatabaseDescriptor();
        final List<TabDescriptor> tabsForDatabase = openTabList.getTabsForDatabase(databaseName);
        if (tabsForDatabase.size() == 0) {
            LOGGER.warn("Cannot close empty list of tabs");
        } else {
            tabFactory.closeTabs(databaseDescriptor, tabsForDatabase);
            for (final TabDescriptor tabDescriptor : tabsForDatabase) {
                openTabList.removeTab(databaseDescriptor, tabDescriptor);
            }
        }
        LOGGER.debug("Removing database from OpenTabList");
        openTabList.removeDatabase(databaseDescriptor);
        LOGGER.debug("Finished removing tab components and handling DatabaseClosedEvent");
    }

    private List<TabDescriptor> loadPermanentAndStoredTabs(final DatabaseDescriptor databaseDescriptor) {
        final List<TabIdentifier> permanentAndOpenTabs = prefs.getOpenTabs(databaseDescriptor.getDatabaseName());
        LOGGER.info("Permanent and stored tabs: " + permanentAndOpenTabs);
        // TODO perhaps the check for existence of tabs in the OpenTabList should be
        // done here and not in the tab factory?
        return tabFactory.loadTabs(databaseDescriptor,
                            permanentAndOpenTabs);
    }

    private JTabbedPane createTabbedPaneOnEventThread(final DatabaseDescriptor descriptor) {
        if (SwingUtilities.isEventDispatchThread()) {
            return new JTabbedPane();
        } else {
            final CountDownLatch latch = new CountDownLatch(1);
            GUIUtils.runOnEventThread(new Runnable() {
                public void run() {
                    synchronized (lock) {
                        tabbedPane = new JTabbedPane();
                    }
                    latch.countDown();
                }
            });
            try {
                latch.await();
            } catch (final InterruptedException e) {
                return null;
            }
            synchronized (lock) {
                return tabbedPane;
            }
        }
    }
}
