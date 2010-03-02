/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devzendo.minimiser.gui.menu.helpers;

import java.awt.Component;
import java.util.concurrent.Callable;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.gui.GUIUtils;
import org.devzendo.commoncode.gui.GUIValueObtainer;
import org.devzendo.minimiser.gui.menu.Menu;
import org.devzendo.minimiser.gui.tab.SystemTabIdentifiers;
import org.devzendo.minimiser.gui.tab.Tab;
import org.devzendo.minimiser.gui.tab.TabIdentifier;
import org.devzendo.minimiser.gui.tab.TabIdentifierToolkit;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;
import org.devzendo.minimiser.opentablist.OpenTabList;
import org.devzendo.minimiser.opentablist.TabDescriptor;
import org.devzendo.minimiser.prefs.Prefs;


/**
 * A helper that:
 * <ol>
 * <li> sets the view menu items hidden/not hidden in response to
 * prefs change events, and upon startup.
 * <li> uses the TabFactory to load tabs and add them to the JTabbedPane
 * <li> allows the JTabbedPane for a database to be queried
 * </ol>
 *
 * TODO this class is now badly named - it's not just a view menu toolkit.
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
        for (final TabIdentifier tabId : SystemTabIdentifiers.values()) {
            final boolean tabHidden = prefs.isTabHidden(tabId.getTabName());
            menu.setTabHidden(tabId.getTabName(), tabHidden);
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

    /**
     * Switch to a specific tab in the database descriptor's JTabbedPane.
     * @param databaseDescriptor the DatabaseDescriptor that's having a tab
     * switched to
     * @param tabDescriptor the tab to switch to
     */
    public static void switchToTab(final DatabaseDescriptor databaseDescriptor, final TabDescriptor tabDescriptor) {
        final JTabbedPane databaseTabbedPane = getTabbedPane(databaseDescriptor);

        // We need the insertion point for the JTabbedPane
        final TabDescriptor finalTabDescriptor = tabDescriptor;
        final Tab tab = finalTabDescriptor.getTab();
        final String displayableName = finalTabDescriptor.getTabIdentifier().getDisplayableName();
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
                databaseTabbedPane.setSelectedComponent(tabComponent);
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
    public static TabIdentifier getCurrentTab(final DatabaseDescriptor databaseDescriptor) {
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
        final JTabbedPane databaseTabbedPane = (JTabbedPane) databaseDescriptor.getAttribute(AttributeIdentifier.TabbedPane);
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
                LOGGER.debug("Removing Tab " + tabDescriptor.getTabIdentifier().getTabName()
                    + " implemented by " + tab.getClass().getSimpleName()
                    + " from JTabbedPane");
                databaseTabbedPane.remove(component);
            }

        });

        LOGGER.debug("Removing tab from the OpenTabList");
        openTabList.removeTab(databaseDescriptor, tabDescriptor);
    }
}
