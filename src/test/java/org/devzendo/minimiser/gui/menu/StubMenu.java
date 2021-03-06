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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuBar;

import org.devzendo.commonapp.gui.menu.MenuIdentifier;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.commoncode.patterns.observer.ObserverList;
import org.devzendo.minimiser.gui.tab.TabIdentifier;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;


/**
 * A Menu that tests can interrogate to test the correctness of the
 * MenuMediator.
 *
 * @author matt
 *
 */
public final class StubMenu implements Menu {
    private boolean closeMenuEnabled;
    private final List<String> databases;
    private int currentDatabaseIndex;
    private boolean recentListBuilt;
    private DatabaseDescriptor[] recentDatabases;
    private final ObserverList<DatabaseNameAndPathChoice> openRecentSubmenuChoiceObservers;
    private final ObserverList<ViewMenuChoice> viewMenuChoiceObservers;
    private final ObserverList<DatabaseNameChoice> windowMenuSwitchObservers;
    private boolean viewMenuBuilt;
    private final Map<String, Boolean> hiddenTabs;
    private boolean helpCheckForUpdatesEnabled;
    private String mHelpMenuApplicationName;
    private boolean isInitialised = false;

    /**
     *
     */
    public StubMenu() {
        databases = new ArrayList<String>();
        recentDatabases = new DatabaseDescriptor[0];
        currentDatabaseIndex = -1;
        recentListBuilt = false;
        viewMenuBuilt = false;
        helpCheckForUpdatesEnabled = false;
        openRecentSubmenuChoiceObservers = new ObserverList<DatabaseNameAndPathChoice>();
        viewMenuChoiceObservers = new ObserverList<ViewMenuChoice>();
        windowMenuSwitchObservers = new ObserverList<DatabaseNameChoice>();
        hiddenTabs = new HashMap<String, Boolean>();
        mHelpMenuApplicationName = "";
    }

    /**
     * {@inheritDoc}
     */
    public void initialise() {
        isInitialised = true;
    }

    /**
     * @return whether initialised
     */
    public boolean isInitialised() {
        return isInitialised;
    }

    /**
     * @return whether close is enabled
     */
    public boolean isCloseEnabled() {
        return closeMenuEnabled;
    }

    /**
     * {@inheritDoc}
     */
    public void enableCloseMenu(final boolean enabled) {
        closeMenuEnabled = enabled;
    }

    /**
     * @return the number of elements in the database list.
     */
    public int getNumberOfDatabases() {
        return databases.size();
    }

    /**
     * {@inheritDoc}
     */
    public void addDatabase(final String dbName) {
        databases.add(dbName);
    }

    /**
     * @return the current database name
     */
    public String getCurrentDatabase() {
        return currentDatabaseIndex == -1 ? null :
            databases.get(currentDatabaseIndex);
    }

    /**
     * {@inheritDoc}
     */
    public void switchDatabase(final String dbName) {
        currentDatabaseIndex = databases.indexOf(dbName);
        rebuildViewMenu();
    }

    /**
     * {@inheritDoc}
     */
    public void removeDatabase(final String dbName) {
        databases.remove(dbName);
    }

    /**
     * {@inheritDoc}
     */
    public void emptyDatabaseList() {
        databases.clear();
        currentDatabaseIndex = -1;
        closeMenuEnabled = false;
    }

    /**
     * {@inheritDoc}
     */
    public JMenuBar getMenuBar() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void addDatabaseSwitchObserver(final Observer<DatabaseNameChoice> observer) {
        windowMenuSwitchObservers.addObserver(observer);
    }

    /**
     * {@inheritDoc}
     */
    public void addMenuActionListener(final MenuIdentifier menuIdentifier, final ActionListener actionListener) {
    }

    /**
     * {@inheritDoc}
     */
    public void refreshRecentList(final DatabaseDescriptor[] databaseDescriptors) {
        this.recentDatabases = databaseDescriptors;
        recentListBuilt = true;
    }

    /**
     * @return whether or not the recent list has been built.
     */
    public boolean isRecentListBuilt() {
        return recentListBuilt;
    }

    /**
     * @return the details of the recently accessed databases
     */
    DatabaseDescriptor[] getRecentDatabases() {
        return recentDatabases;
    }

    /**
     * For unit tests, inject a request to open an entry from the recent list.
     * @param dbName the database name to open.
     * @param dbPath the database path to open
     */
    void injectOpenRecentRequest(final String dbName, final String dbPath) {
        openRecentSubmenuChoiceObservers.eventOccurred(new DatabaseNameAndPathChoice(dbName, dbPath));
    }

    /**
     * {@inheritDoc}
     */
    public void addOpenRecentObserver(final Observer<DatabaseNameAndPathChoice> observer) {
        openRecentSubmenuChoiceObservers.addObserver(observer);
    }

    /**
     * Is the named tab hidden?
     * @param tabName the tabName, as you would get from TabIdentifier::toString
     * @return true iff hidden?
     */
    public boolean isTabHidden(final String tabName) {
        return hiddenTabs.containsKey(tabName) && hiddenTabs.get(tabName) == Boolean.TRUE;
    }

    /**
     * Has the view menu been built?
     * @return true iff built
     */
    public boolean isViewMenuBuilt() {
        return viewMenuBuilt;
    }

    /**
     * Clear the view menu built flag, so further prefs-changing tests can
     * run
     */
    public void resetViewMenuBuiltFlag() {
        viewMenuBuilt = false;
    }

    /**
     * {@inheritDoc}
     */
    public void rebuildViewMenu() {
        viewMenuBuilt = true;
    }

    /**
     * {@inheritDoc}
     */
    public void setTabHidden(final String tabName, final boolean tabHidden) {
        hiddenTabs.put(tabName, tabHidden);
    }

    /**
     * Inject a view menu choice, either an open or a close.
     * @param database the database (the current one)
     * @param tabId the tab that's been opened or closed
     * @param opened true if this is an open choice; false if a
     * close choice.
     */
    public void injectViewMenuRequest(final DatabaseDescriptor database, final TabIdentifier tabId, final boolean opened) {
        viewMenuChoiceObservers.eventOccurred(new ViewMenuChoice(database, tabId, opened));
    }

    /**
     * {@inheritDoc}
     */
    public void addViewChoiceObserver(final Observer<ViewMenuChoice> observer) {
        viewMenuChoiceObservers.addObserver(observer);
    }

    /**
     * For unit tests, inject a choice of database to switch to, from the
     * window menu.
     * @param database the database descriptor to switch to.
     */
    public void injectWindowMenuRequest(final DatabaseDescriptor database) {
        windowMenuSwitchObservers.eventOccurred(new DatabaseNameChoice(database.getDatabaseName()));
    }

    /**
     * For unit tests, set the state of the Help|Check for Updates
     * menu item
     * @param newEnabled true iff enabled
     */
    public void setHelpCheckForUpdatesEnabled(final boolean newEnabled) {
        helpCheckForUpdatesEnabled = newEnabled;
    }

    /**
     * @return the enabledness of the Help|Check for Updates menu
     * item
     */
    public boolean isHelpCheckForUpdatesEnabled() {
        return helpCheckForUpdatesEnabled;
    }

    /**
     * @return the name of the help menu application name, "" if
     * it hasn't been set.
     */
    public String getHelpMenuApplicationName() {
        return mHelpMenuApplicationName;
    }

    /**
     * {@inheritDoc}
     */
    public void rebuildHelpMenu(final String applicationName) {
        mHelpMenuApplicationName = applicationName;
    }

    /**
     * {@inheritDoc}
     */
    public void rebuildEntireMenu() {
        // TODO: currently untested
    }

    /**
     * {@inheritDoc}
     */
    public void rebuildFileMenu() {
        // TODO: currently untested
    }
}
