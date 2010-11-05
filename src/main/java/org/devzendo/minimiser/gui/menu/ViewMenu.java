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

package org.devzendo.minimiser.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.commoncode.patterns.observer.ObserverList;
import org.devzendo.commongui.menu.AbstractRebuildableMenuGroup;
import org.devzendo.commongui.menu.MenuWiring;
import org.devzendo.minimiser.gui.tab.SystemTabIdentifiers;
import org.devzendo.minimiser.gui.tab.TabIdentifier;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.OpenDatabaseList;
import org.devzendo.minimiser.opentablist.OpenTabList;
import org.devzendo.minimiser.opentablist.TabDescriptor;
import org.devzendo.minimiser.prefs.Prefs;


/**
 * The View menu is rebuildable on initialisation and when prefs' hidden tabs
 * change, so that tabs that are hidden are removed, and that those that have
 * been enabled are shown.
 *
 * @author matt
 *
 */
public final class ViewMenu extends AbstractRebuildableMenuGroup {
    private static final Logger LOGGER = Logger.getLogger(ViewMenu.class);
    private final JMenu viewMenu;
    private final OpenDatabaseList openDatabaseList;
    private final OpenTabList openTabList;
    private final Prefs prefs;
    private final ObserverList<ViewMenuChoice> viewMenuChoiceObservers;
    private final ApplicationMenuCombiner mApplicationMenuCombiner;

    /**
     * Construct the view menu
     *
     * @param wiring the menu wiring
     * @param databaseList the Open Database List
     * @param tabList the Open Tab List
     * @param preferences the Preferences
     * @param applicationMenuCombiner the ApplicationMenuCombiner
     */
    public ViewMenu(final MenuWiring wiring,
            final OpenDatabaseList databaseList,
            final OpenTabList tabList,
            final Prefs preferences,
            final ApplicationMenuCombiner applicationMenuCombiner) {
        super(wiring);
        this.openDatabaseList = databaseList;
        this.openTabList = tabList;
        this.prefs = preferences;
        mApplicationMenuCombiner = applicationMenuCombiner;
        viewMenuChoiceObservers = new ObserverList<ViewMenuChoice>();

        viewMenu = new JMenu("View");
        viewMenu.setMnemonic('V');
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rebuildMenuGroup() {
        viewMenu.removeAll();
        if (openDatabaseList.getNumberOfDatabases() == 0) {
            viewMenu.setEnabled(false);
            LOGGER.debug("view menu is empty");
            return;
        }

        final DatabaseDescriptor currentDatabase = openDatabaseList.getCurrentDatabase();
        LOGGER.debug("current database is " + currentDatabase);
        final HashSet<TabDescriptor> tabDescriptorSet = new HashSet<TabDescriptor>(openTabList.getTabsForDatabase(
                                        currentDatabase.getDatabaseName()));
        LOGGER.debug("tab list tabs for this are " + tabDescriptorSet);

        for (final TabIdentifier tabId : combineAllTabIdentifiers()) {
            final boolean viewMenuItemHidden = prefs.isTabHidden(tabId.getTabName());
            LOGGER.debug("View menu item " + tabId.getTabName() + " hidden:" + viewMenuItemHidden);
            // Only add items to the view menu that are not permanent - we don't need to be able to control
            // whether the permanent tabs are visible.
            if (!tabId.isTabPermanent() && !viewMenuItemHidden) {
                final boolean isSelected = tabDescriptorSet.contains(new TabDescriptor(tabId));
                LOGGER.debug("Adding view menu item for " + tabId.getTabName() + "; selected: " + isSelected);
                viewMenu.add(createTabIdentifierMenuItem(
                    currentDatabase, tabId, isSelected));
            }
        }
        viewMenu.setEnabled(true);
    }

    private List<TabIdentifier> combineAllTabIdentifiers() {
        final Set<TabIdentifier> sortedTabIdentifiers = new TreeSet<TabIdentifier>(new Comparator<TabIdentifier>() {
            public int compare(final TabIdentifier o1, final TabIdentifier o2) {
                return o1.compareTo(o2);
            }
        });
        sortedTabIdentifiers.addAll(Arrays.asList(SystemTabIdentifiers.values()));
        sortedTabIdentifiers.addAll(mApplicationMenuCombiner.combineMenus().getViewMenuTabIdentifiers());
        return Arrays.asList(sortedTabIdentifiers.toArray(new TabIdentifier[0]));
    }

    private JCheckBoxMenuItem createTabIdentifierMenuItem(
            final DatabaseDescriptor currentDatabase,
            final TabIdentifier tabId,
            final boolean isSelected) {
        final JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(tabId.getDisplayableName());
        menuItem.setMnemonic(tabId.getMnemonic());
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final boolean opened = menuItem.isSelected();
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.currentThread().setName("ViewOpener:" + tabId.getTabName());
                            Thread.currentThread().setPriority(Thread.MIN_PRIORITY + 1);
                            LOGGER.info((opened ? "Opening" : "Closing") + " view '" + tabId.getTabName() + "'");
                            viewMenuChoiceObservers.eventOccurred(new ViewMenuChoice(currentDatabase, tabId, opened));
                        } catch (final Throwable t) {
                            LOGGER.error("View opener thread caught unexpected " + t.getClass().getSimpleName(), t);
                        } finally {
                            LOGGER.debug("View menu choice complete");
                        }
                    }
                }).start();
            }
        });
        menuItem.setSelected(isSelected);
        return menuItem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JMenu getJMenu() {
        return viewMenu;
    }

    /**
     * Add a recent menu observer
     * @param observer the observer
     */
    public void addViewObserver(final Observer<ViewMenuChoice> observer) {
        viewMenuChoiceObservers.addObserver(observer);
    }
}
