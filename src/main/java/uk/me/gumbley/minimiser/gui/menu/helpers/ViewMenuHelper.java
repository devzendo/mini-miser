package uk.me.gumbley.minimiser.gui.menu.helpers;

import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.minimiser.gui.menu.Menu;
import uk.me.gumbley.minimiser.gui.tab.Tab;
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;
import uk.me.gumbley.minimiser.opentablist.OpenTabList;
import uk.me.gumbley.minimiser.opentablist.TabDescriptor;
import uk.me.gumbley.minimiser.prefs.Prefs;

/**
 * A helper that:
 * <ol>
 * <li> sets the view menu items hidden/not hidden in response to
 * prefs change events, and upon startup.
 * <li> uses the TabFactory to load tabs and add them to the JTabbedPane
 * </ol>
 * 
 * @author matt
 *
 */
public final class ViewMenuHelper {
    private static final Logger LOGGER = Logger.getLogger(ViewMenuHelper.class);
    private ViewMenuHelper() {
        // no instances
    }
    
    /**
     * Update the view menu with hidden tabs from prefs
     * @param prefs the prefs
     * @param menu the menu
     */
    public static void updateViewMenuFromPrefsHiddenTabs(final Prefs prefs, final Menu menu) {
        LOGGER.debug("Setting hidden tabs");
        for (final TabIdentifier tabId : TabIdentifier.values()) {
            final boolean tabHidden = prefs.isTabHidden(tabId.toString());
            menu.setTabHidden(tabId.toString(), tabHidden);
        }
        LOGGER.debug("Rebuilding view menu");
        menu.rebuildViewMenu();
    }

    /**
     * Add a tab to the database descriptor's JTabbedPane, at the
     * correct insertion point, given any existing tabs, and also add the tab
     * to the OpenTabList
     * @param openTabList the OpenTabList
     * @param databaseDescriptor the DatabaseDescriptor that's having a tab
     * added
     * @param tabDescriptor the tab to add
     */
    public static void addTabToTabbedPaneAndOpenTabList(final OpenTabList openTabList,
        final DatabaseDescriptor databaseDescriptor, final TabDescriptor tabDescriptor) {
        final JTabbedPane databaseTabbedPane = getTabbedPane(databaseDescriptor);

        // We need the insertion point for the JTabbedPane
        final TabDescriptor finalTabDescriptor = tabDescriptor; 
        final String displayableName = finalTabDescriptor.getTabIdentifier().getDisplayableName();
        LOGGER.debug("Adding tab " + displayableName);
        final Tab tab = finalTabDescriptor.getTab();
        LOGGER.debug("Getting insertion point for tab " + displayableName);
        final int insertionPoint = openTabList.getInsertionPosition(databaseDescriptor.getDatabaseName(), tabDescriptor.getTabIdentifier());
        // TODO perhaps the OpenTabList should be throwing the IllegalStateException here?
        if (insertionPoint == -1) {
            final String warning = "Cannot get insertion point for tab: database '" 
                                    + databaseDescriptor.getDatabaseName() + "' not added to open tab list";
            LOGGER.warn(warning);
            throw new IllegalStateException(warning);
        }
        
        // Add the tab's component to the JTabbedPane on the EDT 
        LOGGER.debug("Tab " + displayableName + " implemented by " + tab.getClass().getSimpleName() + " insertion point " + insertionPoint);
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
        LOGGER.debug("Adding tab to the OpenTabList");
        openTabList.addTab(databaseDescriptor, tabDescriptor);
    }

    private static JTabbedPane getTabbedPane(final DatabaseDescriptor databaseDescriptor) {
        final JTabbedPane databaseTabbedPane = (JTabbedPane) databaseDescriptor.getAttribute(AttributeIdentifier.TabbedPane);
        if (databaseTabbedPane == null) {
            final String warning = "No JTabbedPane stored in database descriptor to add tab to";
            LOGGER.warn(warning);
            throw new IllegalStateException(warning);
        }
        return databaseTabbedPane;
    }

    /**
     * Remove a tab from the database descriptor's JTabbedPane, and also remove
     * the tab from the OpenTabList
     * @param openTabList the OpenTabList
     * @param databaseDescriptor the DatabaseDescriptor that's having a tab
     * remove
     * @param tabDescriptor the tab to remove
     */
    public static void removeTabFromTabbedPaneAndOpenTabList(final OpenTabList openTabList,
            final DatabaseDescriptor databaseDescriptor, final TabDescriptor tabDescriptor) {
        GUIUtils.runOnEventThread(new Runnable() {

            public void run() {
                // TODO Auto-generated method stub
                final JTabbedPane databaseTabbedPane = getTabbedPane(databaseDescriptor);
                final Tab tab = tabDescriptor.getTab();
                final Component component = tab.getComponent();
                LOGGER.debug("Removing Tab " + tabDescriptor.getTabIdentifier().toString()
                    + " implemented by " + tab.getClass().getSimpleName()
                    + " from JTabbedPane");
                databaseTabbedPane.remove(component);
            }
            
        });
        
        LOGGER.debug("Removing tab from the OpenTabList");
        openTabList.removeTab(databaseDescriptor, tabDescriptor);
    }
}
