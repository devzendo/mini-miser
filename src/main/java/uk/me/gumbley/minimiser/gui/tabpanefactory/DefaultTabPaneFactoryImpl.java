package uk.me.gumbley.minimiser.gui.tabpanefactory;

import java.util.List;
import javax.swing.SwingUtilities;
import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.minimiser.gui.dialog.ProblemReporter;
import uk.me.gumbley.minimiser.gui.tab.Tab;
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptorFactory;
import uk.me.gumbley.minimiser.opentablist.OpenTabList;
import uk.me.gumbley.minimiser.opentablist.TabDescriptor;
import uk.me.gumbley.minimiser.springloader.SpringLoader;

/**
 * The default implementation of the TabPaneFactory.
 * 
 * @author matt
 *
 */
public final class DefaultTabPaneFactoryImpl implements TabPaneFactory {
    
    private final SpringLoader springLoader;
    private final OpenTabList openTabList;
    private final ProblemReporter problemReporter;
    
    // Used by the run-on-EDT code in callInitComponentOnSwingEventThread
    private final Object lock = new Object();

    /**
     * Construct the TabPaneFactory, given the OpenTabList into which
     * newly created tabs will be added.
     * @param loader the SpringLoader
     * @param tabList the OpenTabList
     * @param reporter the problem reporter for serious problems
     */
    public DefaultTabPaneFactoryImpl(final SpringLoader loader, final OpenTabList tabList, final ProblemReporter reporter) {
        this.springLoader = loader;
        this.openTabList = tabList;
        this.problemReporter = reporter;
    }

    /**
     * Construct the TabPaneFactory, given the OpenTabList into which
     * newly created tabs will be added. There's no parent frame; use
     * this variant in unit tests, for simplicity.
     * @param loader the SpringLoader
     * @param tabList the OpenTabList
     */
    public DefaultTabPaneFactoryImpl(final SpringLoader loader, final OpenTabList tabList) {
        this(loader, tabList, null);
    }
    
    /**
     * Get the DatabaseDescriptorFactory
     * @return the database descriptor factory 
     */
    private DatabaseDescriptorFactory getDatabaseDescriptorFactory() {
        return springLoader.getBean("&databaseDescriptor", DatabaseDescriptorFactory.class);
    }
    
    /**
     * {@inheritDoc}
     */
    public void loadTabs(final DatabaseDescriptor databaseDescriptor, final List<TabIdentifier> tabIdentifiers) {
        assert !SwingUtilities.isEventDispatchThread();

        // Set the database descriptor in the factory so that it can be
        // retrieved by any tab beans that need it
        final DatabaseDescriptorFactory databaseDescriptorFactory = getDatabaseDescriptorFactory();
        databaseDescriptorFactory.setDatabaseDescriptor(databaseDescriptor);
        
        // Now load each tab
        final String databaseName = databaseDescriptor.getDatabaseName();
        for (TabIdentifier identifier : tabIdentifiers) {
            if (!openTabList.containsTab(databaseName, identifier)) {
                loadTabAndAddToOpenList(databaseName, identifier);
            }
        }
        
        // Clear database descriptor in factory
        databaseDescriptorFactory.clearDatabaseDescriptor();
    }

    private void loadTabAndAddToOpenList(final String databaseName, final TabIdentifier identifier) {
        try {
            final Tab tab = springLoader.getBean("tab" + identifier.toString(), Tab.class);
            callInitComponentOnSwingEventThread(tab);
            openTabList.addTab(databaseName, new TabDescriptor(identifier, tab));
        } catch (final RuntimeException re) {
            problemReporter.reportProblem("while loading the " + identifier.getDisplayableName() + " tab", re);
        }
    }

    /**
     * Call the initComponent method on the Swing Event Thread.
     * Precondition: this code is never executed on the EDT - there's an
     * assertion in the calling method for this.
     * 
     * @param tab the tab to initialise
     */
    private void callInitComponentOnSwingEventThread(final Tab tab) {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                synchronized (lock) {
                    tab.initComponent();
                }
            }
        });
        // this might be voodoo, but it might help update caches on multiprocessors?
        synchronized (lock) {
            return;
        }
    }
}
