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

package org.devzendo.minimiser.gui.tabpanemanager;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.devzendo.minimiser.openlist.DatabaseDescriptor;

/**
 * The Tab Pane Manager's responsibilities are:
 * <ul>
 * <li> adding/removing cards for open/closed databases (upon calls from an
 *      OpenDatabaseList listener)</li>
 * <li> adding/removing tabs for open/closed views (upon calls from menu
 *      actionlisteners)</li>
 * <li> switching the current card to match the current database (upon calls
 *      from an OpenDatabaseList listener)</li>
 * <li> providing notification mechanism for open/close/switch tab</li>
 * </ul>
 * 
 * @author matt
 *
 */
public interface TabPaneManager {
    
    /**
     * Gets the main panel controlled by the card layout
     * @return the main panel
     */
    JPanel getMainPanel();
    
    /**
     * How many tab panes have been added?
     * @return the number of tab panes?
     */
    int getNumberOfTabPanes();

    /**
     * Add the tab pane that's stored in the DatabaseDescriptor.
     * Do nothing if there's no tab pane stored.
     * Replace any existing tab pane with the same name.
     * @param databaseDescriptor the database Descriptor.
     */
    void addTabPane(DatabaseDescriptor databaseDescriptor);

    /**
     * Get the tab pane for a named database
     * @param databaseName the database name
     * @return the tab pane, or null if there isn't one.
     */
    JTabbedPane getTabPane(String databaseName);
    
    /**
     * Removes a tab pane referenced by a DatabaseDescriptor.
     * Do nothing if there's no tab pane stored for this descriptor.
     * @param databaseDescriptor the database descriptor.
     */
    void removeTabPane(DatabaseDescriptor databaseDescriptor);

    /**
     * Switch to this previously added tab pane.
     * Do nothing if there's no tab pane stored for this descriptor.
     * @param databaseDescriptor the database descriptor
     */
    void switchToTabPane(DatabaseDescriptor databaseDescriptor);
    
    /**
     * Show a blank panel instead of any other tab panes.
     * Used when the list of tab panes has emptied.
     */
    void hideTabPanes();
}
