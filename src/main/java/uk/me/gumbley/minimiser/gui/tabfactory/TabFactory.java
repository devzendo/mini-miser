package uk.me.gumbley.minimiser.gui.tabfactory;

import java.util.List;
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.opentablist.TabDescriptor;

/**
 * The factory for creating one, or a list of tabs given
 * a database descriptor. Used when:
 * <ul>
 * <li> a database is opened, to load all persisted
 *      tabs from last time
 * <li> a database is closed, to close all its open tabs
 * <li> by the view menu, when a tab is opened/closed.
 * </ul>
 * 
 * @author matt
 *
 */
public interface TabFactory {

    /**
     * Load the supplied list of tabs.
     * <p>
     * Tabs are only loaded if they do not already
     * exist on the list - tabs are unique per TabIdentifier.
     * <p>
     * Tabs are loaded as beans from the Application Context,
     * with a bean name of tabX where X is the TabIdentifier,
     * for example 'tabSQL' or 'tabOVERVIEW'.
     * <p>
     * The DatabaseDescriptor passed here will be available
     * from the SpringLoader via a
     * <constructor-arg ref="databaseDescriptor" /> passed to
     * each tab's bean definition.
     * 
     * @param databaseDescriptor the database for which the tabs
     * are to be loaded.
     * @param tabIdentifiers the List of TabIdentifiers to
     * load.
     * @return the list of loaded TabDescriptors
     */
    List<TabDescriptor> loadTabs(DatabaseDescriptor databaseDescriptor, List<TabIdentifier> tabIdentifiers);

    /**
     * Close a list of tabs.
     * 
     * @param databaseDescriptor the database for which the tabs
     * are to be loaded.
     * @param tabsForDatabase the tabs that are to be closed.
     */
    void closeTabs(DatabaseDescriptor databaseDescriptor, List<TabDescriptor> tabsForDatabase);
}
