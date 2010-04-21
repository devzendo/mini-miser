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

import java.awt.event.ActionListener;

import javax.swing.JMenuBar;

import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;



/**
 * The MiniMiser menu is controlled by an implementation of Menu.
 *
 * @author matt
 *
 */
public interface Menu {

    public enum MenuIdentifier {
        /**
         *
         */
        FileNew,
        /**
         *
         */
        FileOpen,
        /**
         *
         */
        FileClose,
        /**
         *
         */
        FileCloseAll,
        /**
         *
         */
        FileImport,
        /**
         *
         */
        FileExport,
        /**
         *
         */
        FileExit,

        // Window menu is handled internally by the menu
        // View menu is handled internally by the menu

        /**
         *
         */
        ToolsOptions,
        /**
         *
         */
        HelpWelcome,
        /**
         *
         */
        HelpWhatsNew,
        /**
         *
         */
        HelpAbout,
        /**
         *
         */
        HelpContents,
        /**
         *
         */
        HelpCheckForUpdates
    }

    /**
     * Initialise the menu after it has been loaded.
     */
    void initialise();

    /**
     * Wire an ActionListener to a specific menu item
     * @param menuIdentifier the menu item's identifier
     * @param actionListener the ActionListener to fire upon menu item triggering
     */
    void addMenuActionListener(MenuIdentifier menuIdentifier, ActionListener actionListener);

    /**
     * Obtain the main GUI component for the menu, for attaching to the
     * main window.
     * @return the menu bar
     */
    JMenuBar getMenuBar();

    /**
     * Enable or disable the close menu item.
     * @param enabled true to enable, false to disable.
     */
    void enableCloseMenu(boolean enabled);


    /**
     * Add a database to the menu
     * @param dbName the name of the database, which will be unique
     */
    void addDatabase(String dbName);


    /**
     * Switch to a database that exists in the list
     * @param dbName the name of the databse
     */
    void switchDatabase(String dbName);


    /**
     * Remove a database from the menu
     * @param dbName the name of the database
     */
    void removeDatabase(String dbName);


    /**
     * The last database has been closed. Reset to initial state.
     */
    void emptyDatabaseList();

    /**
     * The list of recent databases has changed; refresh it.
     * @param databaseDescriptors the list of database name/paths, in order
     */
    void refreshRecentList(DatabaseDescriptor[] databaseDescriptors);

    /**
     * Add an observer of window menu database switches
     * @param observer of switches
     * TODO: this appears to have not been added TDD - need to write tests
     * for observation of database switching?
     */
    void addDatabaseSwitchObserver(Observer<DatabaseNameChoice> observer);

    /**
     * Add an observer of open recent submenu database open requests
     * @param observer of open recent requests
     */
    void addOpenRecentObserver(Observer<DatabaseNameAndPathChoice> observer);


    /**
     * Add an observer of view menu choices
     * @param observer of choices
     */
    void addViewChoiceObserver(Observer<ViewMenuChoice> observer);

    /**
     * Set a view menu item hidden
     * @param tabName the menu item name
     * @param tabHidden true iff hidden
     */
    void setTabHidden(String tabName, boolean tabHidden);

    /**
     * Rebuild the entire menu bar, and all menus.
     */
    void rebuildEntireMenu();

    /**
     * Rebuild the file menu.
     */
    void rebuildFileMenu();

    /**
     * Rebuild the view menu based on the current hidden flags.
     */
    void rebuildViewMenu();

    /**
     * Set the state of the Help|Check for Updates menu item
     * @param newEnabled true iff enabled
     */
    void setHelpCheckForUpdatesEnabled(final boolean newEnabled);

    /**
     * @return the enabledness of the Help|Check for Updates menu
     * item
     */
    boolean isHelpCheckForUpdatesEnabled();

    /**
     * Rebuild the help menu with the application name taken from
     * the PluginRegistry's Application PluginDescriptor
     * @param applicationName the application name
     */
    void rebuildHelpMenu(String applicationName);
}
