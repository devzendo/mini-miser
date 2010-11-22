package org.devzendo.minimiser.tabcontroller;

import java.awt.Component;
import java.util.concurrent.Callable;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.devzendo.commonapp.gui.GUIUtils;
import org.devzendo.commonapp.gui.GUIValueObtainer;
import org.devzendo.minimiser.gui.tab.Tab;
import org.devzendo.minimiser.gui.tab.TabIdentifier;
import org.devzendo.minimiser.gui.tab.TabIdentifierToolkit;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.opentablist.OpenTabList;
import org.devzendo.minimiser.opentablist.TabDescriptor;

/**
 * Handles interactions between the OpenTabList and JTabbedPanes.
 *
 * @author matt
 *
 */
public final class TabController {
    private static final Logger LOGGER = Logger.getLogger(TabController.class);

    private final OpenTabList mOpenTabList;

    /**
     * Create the TabController.
     *
     * @param openTabList the OpenTabList
     */
    public TabController(final OpenTabList openTabList) {
        mOpenTabList = openTabList;
    }

    /**
     * Add a tab to the database descriptor's JTabbedPane, at the
     * correct insertion point, given any existing tabs, and also add the tab
     * to the OpenTabList
     * @param databaseDescriptor the DatabaseDescriptor that's having a tab
     * added
     * @param tabDescriptor the tab to add
     */
    public void addTabToTabbedPaneAndOpenTabList(final DatabaseDescriptor databaseDescriptor,
        final TabDescriptor tabDescriptor) {
        final JTabbedPane databaseTabbedPane = getTabbedPane(databaseDescriptor);

        // We need the insertion point for the JTabbedPane
        final TabDescriptor finalTabDescriptor = tabDescriptor;
        final String displayableName = finalTabDescriptor.getTabIdentifier().getDisplayableName();
        LOGGER.debug("Adding tab " + displayableName);
        final Tab tab = finalTabDescriptor.getTab();
        LOGGER.debug("Getting insertion point for tab " + displayableName);
        final int insertionPoint = mOpenTabList.getInsertionPosition(databaseDescriptor.getDatabaseName(), tabDescriptor.getTabIdentifier());
        // TODO: perhaps the OpenTabList should be throwing the IllegalStateException here?
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
        mOpenTabList.addTab(databaseDescriptor, tabDescriptor);
    }

    /**
     * Switch to a specific tab in the database descriptor's JTabbedPane.
     * @param databaseDescriptor the DatabaseDescriptor that's having a tab
     * switched to
     * @param tabDescriptor the tab to switch to
     */
    public void switchToTab(final DatabaseDescriptor databaseDescriptor, final TabDescriptor tabDescriptor) {
        final Tab tab = tabDescriptor.getTab();
        final String displayableName = tabDescriptor.getTabIdentifier().getDisplayableName();
        LOGGER.debug("Switching to tab " + displayableName);

        // Switch to the tab's component on the EDT
        final Component tabComponent = tab.getComponent();
        if (tabComponent == null) {
            LOGGER.warn("Tab " + displayableName
                + " has created a null component; cannot switch to it");
            return;
        }
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                getTabbedPane(databaseDescriptor).setSelectedComponent(tabComponent);
            }
        });
    }

    /**
     * Obtain the TabIdentifier of the currently selected tab, for a given
     * database (by querying the JTabbedPane)
     * @param databaseDescriptor the database descriptor whose JTabbedPane is
     * to be queried.
     * @return the TabIdentifier of the currently selected tab, or null if no
     * TabIdentifier can be discovered.
     */
    public TabIdentifier getCurrentTab(final DatabaseDescriptor databaseDescriptor) {
        LOGGER.debug("Getting current tab for database " + databaseDescriptor.getDatabaseName());
        final JTabbedPane databaseTabbedPane = getTabbedPane(databaseDescriptor);
        if (databaseTabbedPane == null) {
            LOGGER.warn("No JTabbedPane for this database");
            return null;
        }
        final GUIValueObtainer<TabIdentifier> obtainer = new GUIValueObtainer<TabIdentifier>();
        try {
            return obtainer.obtainFromEventThread(new Callable<TabIdentifier>() {

                public TabIdentifier call() throws Exception {
                    final int selectedIndex = databaseTabbedPane.getSelectedIndex();
                    LOGGER.debug("Selected index is " + selectedIndex);
                    final String tabDisplayName = databaseTabbedPane.getTitleAt(selectedIndex);
                    LOGGER.debug("Selected tab name is " + tabDisplayName);
                    final TabIdentifier toTabIdentifier = TabIdentifierToolkit.toTabIdentifierFromDisplayName(tabDisplayName);
                    LOGGER.debug("Selected TabIdentifier is " + toTabIdentifier.getTabName());
                    return toTabIdentifier;
                }

            });
        } catch (final Exception e) {
            // it has been logged by the GUIValueObtainer
            return null;
        }
    }

    private static JTabbedPane getTabbedPane(final DatabaseDescriptor databaseDescriptor) {
        final JTabbedPane databaseTabbedPane = databaseDescriptor.getTabbedPane();
        if (databaseTabbedPane == null) {
            final String warning = "No JTabbedPane stored in database descriptor";
            LOGGER.warn(warning);
            throw new IllegalStateException(warning);
        }
        return databaseTabbedPane;
    }

    /**
     * Remove a tab from the database descriptor's JTabbedPane, and also remove
     * the tab from the OpenTabList
     * @param databaseDescriptor the DatabaseDescriptor that's having a tab
     * remove
     * @param tabDescriptor the tab to remove
     */
    public void removeTabFromTabbedPaneAndOpenTabList(final DatabaseDescriptor databaseDescriptor,
            final TabDescriptor tabDescriptor) {
        GUIUtils.runOnEventThread(new Runnable() {

            public void run() {
                final JTabbedPane databaseTabbedPane = getTabbedPane(databaseDescriptor);
                final Tab tab = tabDescriptor.getTab();
                final Component component = tab.getComponent();
                LOGGER.debug("Removing Tab " + tabDescriptor.getTabIdentifier().getTabName()
                    + " implemented by " + tab.getClass().getSimpleName()
                    + " from JTabbedPane");
                databaseTabbedPane.remove(component);
            }

        });

        LOGGER.debug("Removing tab from the OpenTabList");
        mOpenTabList.removeTab(databaseDescriptor, tabDescriptor);
    }
}
