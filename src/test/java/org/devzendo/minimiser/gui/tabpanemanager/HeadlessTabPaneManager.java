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
