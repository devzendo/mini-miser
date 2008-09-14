package uk.me.gumbley.minimiser.gui.tabpanefactory;

import java.util.List;
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
    public void loadTabs(
            final DatabaseDescriptor databaseDescriptor,
            final List<TabIdentifier> tabIdentifiers) {
        final String databaseName = databaseDescriptor.getDatabaseName();
        for (TabIdentifier identifier : tabIdentifiers) {
            if (!openTabList.containsTab(databaseName, identifier)) {
                openTabList.addTab(databaseName, new TabDescriptor(identifier, new StubRecordingTab(databaseDescriptor)));
            }
        }
    }
}
