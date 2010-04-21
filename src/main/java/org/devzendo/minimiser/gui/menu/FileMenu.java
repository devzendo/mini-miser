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
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.commoncode.patterns.observer.ObserverList;
import org.devzendo.minimiser.gui.menu.Menu.MenuIdentifier;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.OpenDatabaseList;
import org.devzendo.minimiser.recentlist.RecentFilesList;


/**
 * The File Menu is rebuilt when the Open Recent -> list is changed. It has
 * the Close / Close All (and later, other) menu items disabled when there
 * is no database open.
 *
 * @author matt
 *
 */
public final class FileMenu extends AbstractRebuildableMenuGroup {
    private static final Logger LOGGER = Logger.getLogger(FileMenu.class);
    private final ObserverList<DatabaseNameAndPathChoice> openRecentSubmenuChoiceObservers;
    private final JMenu fileMenu;
    private final OpenDatabaseList openDatabaseList;
    private final RecentFilesList recentFilesList;

    /**
     * Construct the File Menu.
     *
     * @param wiring the menu wiring
     * @param databaseList the OpenDatabaseList
     * @param recentList the RecentFilesList
     */
    public FileMenu(final MenuWiring wiring,
            final OpenDatabaseList databaseList,
            final RecentFilesList recentList) {
        super(wiring);
        this.openDatabaseList = databaseList;
        this.recentFilesList = recentList;
        openRecentSubmenuChoiceObservers = new ObserverList<DatabaseNameAndPathChoice>();

        fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');

        // Trigger the first build; initially we'll have no recent files list.
        // Need to do an initial rebuild so the menu wiring is initially populated
        // (enableCloseAllMenuIfDatabasesOpen needs this)
        rebuildMenuGroup();
        enableCloseAllMenuIfDatabasesOpen();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rebuildMenuGroup() {
        fileMenu.removeAll();
        // TODO: remove duplication of menu text
        createMenuItem(MenuIdentifier.FileNew, "New...", 'N', fileMenu);
        createMenuItem(MenuIdentifier.FileOpen, "Open...", 'O', fileMenu);
        fileMenu.add(buildRecentList());

        fileMenu.add(new JSeparator());

        createMenuItem(MenuIdentifier.FileClose, "Close", 'C', fileMenu);
        createMenuItem(MenuIdentifier.FileCloseAll, "Close all", 'l', fileMenu);

        // TODO: enable import / export in 0.2
//        fileMenu.add(new JSeparator());
//
//        getMainMenu().createMenuItem(MenuIdentifier.FileImport, "Import...", 'I', fileMenu);
//        getMainMenu().createMenuItem(MenuIdentifier.FileExport, "Export...", 'E', fileMenu);

        fileMenu.add(new JSeparator());

        createMenuItem(MenuIdentifier.FileExit, "Exit", 'x', fileMenu);
    }


    // EDT
    private JMenu buildRecentList() {
        final JMenu submenu = new JMenu("Open recent");
        submenu.setMnemonic('r');
        final int numberOfRecentFiles = recentFilesList.getNumberOfEntries();
        if (numberOfRecentFiles == 0) {
            submenu.setEnabled(false);
        } else {
            final DatabaseDescriptor[] recentDatabases = recentFilesList.getRecentDatabases();
            for (int i = 0; i < numberOfRecentFiles; i++) {
                final String recentDbName = recentDatabases[i].getDatabaseName();
                final String recentDbPath = recentDatabases[i].getDatabasePath();
                final int mnemonic = 1 + i;
                final JMenuItem menuItem = new JMenuItem("" + mnemonic + " " + recentDbName);
                menuItem.setMnemonic(KeyEvent.VK_0 + mnemonic);
                menuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent e) {
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    Thread.currentThread().setName("RecentOpener:" + recentDbName);
                                    Thread.currentThread().setPriority(Thread.MIN_PRIORITY + 1);
                                    LOGGER.info("Opening recent database '" + recentDbName + "'");
                                        openRecentSubmenuChoiceObservers.eventOccurred(
                                            new DatabaseNameAndPathChoice(recentDbName, recentDbPath));
                                } catch (final Throwable t) {
                                    LOGGER.error("Recent opener thread caught unexpected " + t.getClass().getSimpleName(), t);
                                } finally {
                                    LOGGER.debug("Open recent complete");
                                }
                            }
                        }).start();
                    }
                });
                submenu.add(menuItem);
            }
        }
        return submenu;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public JMenu getJMenu() {
        return fileMenu;
    }

    /**
     * Enable the close all menu item, if there are any open databases.
     */
    public void enableCloseAllMenuIfDatabasesOpen() {
        final int numberOfDatabases = openDatabaseList.getNumberOfDatabases();
        LOGGER.debug("Close All Menu - size of database list is "  + numberOfDatabases);
        getMenuWiring().setMenuItemEnabled(MenuIdentifier.FileCloseAll, numberOfDatabases != 0);
    }

    /**
     * Enable or disable the close menu item (and later, import/export items)
     * @param enabled true to enable; false to disable
     */
    public void enableCloseMenu(final boolean enabled) {
        LOGGER.debug("Close menu is " + (enabled ? "enabled" : "disabled"));
        getMenuWiring().setMenuItemEnabled(MenuIdentifier.FileClose, enabled);
        // TODO: enable import / export in 0.2
        // getMenuWiring().setMenuItemEnabled(MenuIdentifier.FileImport, enabled);
        // getMenuWiring().setMenuItemEnabled(MenuIdentifier.FileExport, enabled);
    }

    /**
     * Add a recent menu observer
     * @param observer the observer
     */
    public void addOpenRecentObserver(final Observer<DatabaseNameAndPathChoice> observer) {
        openRecentSubmenuChoiceObservers.addObserver(observer);
    }
}
