package org.devzendo.minimiser.gui.tabfactory;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.gui.GUIUtils;
import org.devzendo.minimiser.gui.dialog.problem.ProblemReporter;
import org.devzendo.minimiser.gui.tab.Tab;
import org.devzendo.minimiser.gui.tab.TabIdentifier;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.DatabaseDescriptorFactory;
import org.devzendo.minimiser.opentablist.OpenTabList;
import org.devzendo.minimiser.opentablist.TabDescriptor;
import org.devzendo.minimiser.springloader.SpringLoader;


/**
 * The default implementation of the TabFactory.
 *
 * @author matt
 *
 */
public final class DefaultTabFactoryImpl implements TabFactory {
    private static final Logger LOGGER = Logger
            .getLogger(DefaultTabFactoryImpl.class);

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
    public DefaultTabFactoryImpl(final SpringLoader loader, final OpenTabList tabList, final ProblemReporter reporter) {
        this.springLoader = loader;
        this.openTabList = tabList;
        this.problemReporter = reporter;
    }

    /**
     * Construct the TabPaneFactory, given the OpenTabList into which
     * newly created tabs will be added. There's no problem reporter; use
     * this variant in unit tests, for simplicity.
     * @param loader the SpringLoader
     * @param tabList the OpenTabList
     */
    public DefaultTabFactoryImpl(final SpringLoader loader, final OpenTabList tabList) {
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
    public List<TabDescriptor> loadTabs(final DatabaseDescriptor databaseDescriptor, final List<TabIdentifier> tabIdentifiers) {
        assert !SwingUtilities.isEventDispatchThread();

        // Set the database descriptor in the factory so that it can be
        // retrieved by any tab beans that need it
        LOGGER.debug("Storing database descriptor " + databaseDescriptor + " in factory for tabs to use");
        final DatabaseDescriptorFactory databaseDescriptorFactory = getDatabaseDescriptorFactory();
        databaseDescriptorFactory.setDatabaseDescriptor(databaseDescriptor);

        // Now load each tab
        final String databaseName = databaseDescriptor.getDatabaseName();
        final ArrayList<TabDescriptor> tabDescriptorList = new ArrayList<TabDescriptor>();
        for (final TabIdentifier identifier : tabIdentifiers) {
            if (!openTabList.containsTab(databaseName, identifier)) {
                LOGGER.debug("Loading tab " + identifier.getTabName());
                final Tab loadedTab = loadTab(databaseDescriptor, identifier);
                if (loadedTab == null) { // TODO need test for failure to load causes lack of addition to list
                    LOGGER.warn("Could not load tab " + identifier.getTabName());
                } else {
                    tabDescriptorList.add(new TabDescriptor(identifier, loadedTab));
                }
            } else {
                LOGGER.debug("Tab " + identifier.getTabName() + " already loaded; not reloading it");
            }
        }

        // Clear database descriptor in factory
        LOGGER.debug("Clearing database descriptor from factory");
        databaseDescriptorFactory.clearDatabaseDescriptor();

        return tabDescriptorList;
    }

    // TODO: get rid of the databaseDescriptor parameter, it's not used.
    private Tab loadTab(final DatabaseDescriptor databaseDescriptor, final TabIdentifier identifier) {
        try {
            LOGGER.info("Loading " + identifier.getTabName() + " tab; bean name " + identifier.getTabBeanName());
            final Tab tab = springLoader.getBean(identifier.getTabBeanName(), Tab.class);
            callInitComponentOnSwingEventThread(tab);
            return tab;
        } catch (final RuntimeException re) {
            LOGGER.warn("Failed to load tab " + identifier.getTabName() + ": " + re.getMessage(), re);
            problemReporter.reportProblem("while loading the " + identifier.getDisplayableName() + " tab", re);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void closeTabs(final DatabaseDescriptor databaseDescriptor, final List<TabDescriptor> tabsForDatabase) {
        assert !SwingUtilities.isEventDispatchThread();

        if (tabsForDatabase == null || tabsForDatabase.size() == 0) {
            LOGGER.warn("Cannot close a null or empty list of tabs");
            return;
        }

        for (final TabDescriptor tabDescriptor : tabsForDatabase) {
            final Tab tab = tabDescriptor.getTab();
            if (tab == null) {
                LOGGER.warn("Tab for tab descriptor " + tabDescriptor.getTabIdentifier() + " is null; cannot close");
            } else {
                LOGGER.info("Destroying " + tabDescriptor.getTabIdentifier() + " tab");
                tab.destroy();
                callDisposeComponentOnSwingEventThread(tab);
            }
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

    /**
     * Call the disposeComponent method on the Swing Event Thread.
     * Precondition: this code is never executed on the EDT - there's an
     * assertion in the calling method for this.
     *
     * @param tab the tab to dispose whose component is to be disposed.
     */
    private void callDisposeComponentOnSwingEventThread(final Tab tab) {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                synchronized (lock) {
                    tab.disposeComponent();
                }
            }
        });
        // this might be voodoo, but it might help update caches on multiprocessors?
        synchronized (lock) {
            return;
        }
    }
}
