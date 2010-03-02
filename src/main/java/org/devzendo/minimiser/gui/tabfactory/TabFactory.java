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

package org.devzendo.minimiser.gui.tabfactory;

import java.util.List;

import org.devzendo.minimiser.gui.tab.TabIdentifier;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.opentablist.TabDescriptor;

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
     * with a bean name given in the TabIdentifier,
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
