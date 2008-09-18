package uk.me.gumbley.minimiser.wiring.databaseeventlistener;

import java.awt.Component;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.gui.tab.Tab;
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;
import uk.me.gumbley.minimiser.gui.tabfactory.TabFactory;
import uk.me.gumbley.minimiser.gui.tabpanemanager.TabListPrefs;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.DatabaseEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseOpenedEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;
import uk.me.gumbley.minimiser.opentablist.OpenTabList;
import uk.me.gumbley.minimiser.opentablist.TabDescriptor;


/**
 * A database has been opened, so create a JTabbedPane for it,
 * populate it with {the previously-open view tabs UNION the
 * permanent tabs} y adding these tabs to the open tab list.
 * 
 * TODO WOZERE: not developed TDD.
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
            // this is called on a background thread - Recent Opener,
            // Open Wizard background, or Lifecycle startup.
            assert !SwingUtilities.isEventDispatchThread();

            final DatabaseOpenedEvent openEvent = (DatabaseOpenedEvent) databaseEvent;
            final DatabaseDescriptor databaseDescriptor = openEvent.getDatabaseDescriptor();
            
            final String databaseName = databaseDescriptor.getDatabaseName();
            // Create the JTabbedPane
            LOGGER.info("Creating tabbed pane for database '" + databaseName + "'");
            final JTabbedPane databaseTabbedPane = createTabbedPaneOnEventThread(databaseDescriptor); 
            databaseDescriptor.setAttribute(AttributeIdentifier.TabbedPane, databaseTabbedPane);
            
            // Add this database to the open tab list, so we can calculate
            // insertion points for tabs on the JTabbedPane.
            openTabList.addDatabase(databaseDescriptor);
            
            // Load the tabs
            LOGGER.info("Loading permanent and stored tabs for database '" + databaseName + "'");
            final List<TabDescriptor> loadedTabDescriptors = loadPermanentAndStoredTabs(databaseDescriptor);
            
            // Add each ones component into the JTabbedPane.
            for (TabDescriptor tabDescriptor : loadedTabDescriptors) {
                // We need the insertion point for the JTabbedPane
                final TabDescriptor finalTabDescriptor = tabDescriptor; 
                final int insertionPoint = openTabList.getInsertionPosition(databaseName, tabDescriptor.getTabIdentifier());
                // TODO perhaps the OpenTabList should be throwing the IllegalStateException here?
                if (insertionPoint == -1) {
                    throw new IllegalStateException("Cannot get insertion point for tab: database '" 
                        + databaseDescriptor.getDatabaseName() + "' not added to open tab list");
                }
                
                // Add the tab's component to the JTabbedPane on the EDT 
                final String displayableName = finalTabDescriptor.getTabIdentifier().getDisplayableName();
                final Tab tab = finalTabDescriptor.getTab();
                LOGGER.debug("Tab " + displayableName + " implemented by " + tab.getClass().getSimpleName());
                final Component tabComponent = tab.getComponent();
                GUIUtils.runOnEventThread(new Runnable() {
                    public void run() {
                        final Component componentToAdd;
                        if (tabComponent == null) {
                            LOGGER.warn("Tab " + displayableName
                                + " has created a null component to add to the tabbed pane; replacing with a blank JPanel");
                            componentToAdd = new JPanel();
                        } else {
                            componentToAdd = tabComponent;
                        }
                        LOGGER.debug("Adding a '" + componentToAdd.getClass().getSimpleName() + "' for tab " + displayableName);
                        databaseTabbedPane.add(
                            componentToAdd,
                            displayableName,
                            insertionPoint);
                    }
                });
                
                // Add the loaded tab into the OpenTabList.
                openTabList.addTab(databaseDescriptor, tabDescriptor);
            }   
        }
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
