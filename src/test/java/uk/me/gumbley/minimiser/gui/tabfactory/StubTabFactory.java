package uk.me.gumbley.minimiser.gui.tabfactory;

import java.util.ArrayList;
import java.util.List;
import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.minimiser.gui.tab.Tab;
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.opentablist.OpenTabList;
import uk.me.gumbley.minimiser.opentablist.TabDescriptor;

/**
 * A Stub TabFactory that just loads StubRecordingTabs.
 * 
 * @author matt
 *
 */
public final class StubTabFactory implements TabFactory {
    private final OpenTabList openTabList;
    // Used by the run-on-EDT code in callInitComponentOnSwingEventThread
    // and callDisposeComponentOnSwingEventThread
    private final Object lock = new Object();
    
    /**
     * Create the factory
     * @param tabList the open tab list
     */
    public StubTabFactory(final OpenTabList tabList) {
        this.openTabList = tabList;
    }
    
    /**
     * {@inheritDoc}
     */
    public List<TabDescriptor> loadTabs(
            final DatabaseDescriptor databaseDescriptor,
            final List<TabIdentifier> tabIdentifiers) {
        final String databaseName = databaseDescriptor.getDatabaseName();
        final ArrayList<TabDescriptor> tabDescriptorList = new ArrayList<TabDescriptor>();
        for (TabIdentifier identifier : tabIdentifiers) {
            if (!openTabList.containsTab(databaseName, identifier)) {
                final StubRecordingTab stubRecordingTab = new StubRecordingTab(databaseDescriptor, identifier);
                callInitComponentOnSwingEventThread(stubRecordingTab);
                tabDescriptorList.add(new TabDescriptor(identifier, stubRecordingTab));
            }
        }
        return tabDescriptorList;
    }
    
    /**
     * {@inheritDoc}
     */
    public void closeTabs(final DatabaseDescriptor databaseDescriptor, final List<TabDescriptor> tabsForDatabase) {
        for (final TabDescriptor tabDescriptor : tabsForDatabase) {
            final Tab tab = tabDescriptor.getTab();
            if (tab != null) {
                tab.destroy();
                callDisposeComponentOnSwingEventThread(tab);
            }
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
