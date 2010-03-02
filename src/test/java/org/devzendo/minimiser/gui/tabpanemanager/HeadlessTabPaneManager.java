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

/**
 * Tab Pane Management without any GUI, that can be used to develop it TDD.
 * 
 * @author matt
 *
 */
public final class HeadlessTabPaneManager extends AbstractTabPaneManager {
    private JTabbedPane lastAddedTabbedPane;
    private JTabbedPane lastRemovedTabbedPane;
    private JTabbedPane lastSwitchedTabbedPane;
    private boolean cleared = false;
    
    /**
     * @return the last removed tab pane
     */
    public JTabbedPane getLastRemovedTabbedPane() {
        return lastRemovedTabbedPane;
    }
    
    /**
     * @return the last switched tab pane
     */
    public JTabbedPane getLastSwitchedTabbedPane() {
        return lastSwitchedTabbedPane;
    }
    
    /**
     * @return the last added tab pane
     */
    public JTabbedPane getLastAddedTabbedPane() {
        return lastAddedTabbedPane;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tabPaneAdded(final String databaseName, final JTabbedPane tabbedPane) {
        lastAddedTabbedPane = tabbedPane;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tabPaneRemoved(final String databaseName, final JTabbedPane tabbedPane) {
        lastRemovedTabbedPane = tabbedPane;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void tabPaneSwitched(final String databaseName, final JTabbedPane tabbedPane) {
        lastSwitchedTabbedPane = tabbedPane;
    }

    /**
     * {@inheritDoc}
     */
    public JPanel getMainPanel() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void hideTabPanes() {
        cleared = true;
    }

    /**
     * @return true iff cleared
     */
    public boolean isCleared() {
        return cleared;
    }
}
