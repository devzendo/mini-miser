package org.devzendo.minimiser.gui.menu;

import java.awt.event.ActionListener;

import javax.swing.JMenuBar;

import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.commongui.menu.MenuIdentifier;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;

/**
 * A Stub Menu that just rebuilds the (real) view menu when asked to.
 * @author matt
 *
 */
public class StubMenuForViewMenuTests implements Menu {
    private final ViewMenu mViewMenu;

    /**
     * @param viewMenu the real view menu
     */
    public StubMenuForViewMenuTests(final ViewMenu viewMenu) {
        mViewMenu = viewMenu;
    }

    /**
     * {@inheritDoc}
     */
    public void addDatabase(final String dbName) {
    }

    /**
     * {@inheritDoc}
     */
    public void addDatabaseSwitchObserver(final Observer<DatabaseNameChoice> observer) {
    }

    /**
     * {@inheritDoc}
     */
    public void addMenuActionListener(
            final MenuIdentifier menuIdentifier,
            final ActionListener actionListener) {
    }

    /**
     * {@inheritDoc}
     */
    public void addOpenRecentObserver(
            final Observer<DatabaseNameAndPathChoice> observer) {
    }

    /**
     * {@inheritDoc}
     */
    public void addViewChoiceObserver(final Observer<ViewMenuChoice> observer) {
    }

    /**
     * {@inheritDoc}
     */
    public void emptyDatabaseList() {
    }

    /**
     * {@inheritDoc}
     */
    public void enableCloseMenu(final boolean enabled) {
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
    public void initialise() {
    }

    /**
     * {@inheritDoc}
     */
    public boolean isHelpCheckForUpdatesEnabled() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void rebuildEntireMenu() {
        mViewMenu.rebuildMenuGroup();
    }

    /**
     * {@inheritDoc}
     */
    public void rebuildFileMenu() {
    }

    /**
     * {@inheritDoc}
     */
    public void rebuildHelpMenu(final String applicationName) {
    }

    /**
     * {@inheritDoc}
     */
    public void rebuildViewMenu() {
        mViewMenu.rebuildMenuGroup();
    }

    /**
     * {@inheritDoc}
     */
    public void refreshRecentList(final DatabaseDescriptor[] databaseDescriptors) {
    }

    /**
     * {@inheritDoc}
     */
    public void removeDatabase(final String dbName) {
    }

    /**
     * {@inheritDoc}
     */
    public void setHelpCheckForUpdatesEnabled(final boolean newEnabled) {
    }

    /**
     * {@inheritDoc}
     */
    public void setTabHidden(final String tabName, final boolean tabHidden) {
    }

    /**
     * {@inheritDoc}
     */
    public void switchDatabase(final String dbName) {
    }
}
