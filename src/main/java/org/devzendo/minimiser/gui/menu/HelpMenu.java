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

import javax.swing.JMenu;
import javax.swing.JSeparator;

import org.apache.log4j.Logger;
import org.devzendo.commonapp.gui.menu.AbstractRebuildableMenuGroup;
import org.devzendo.commonapp.gui.menu.MenuWiring;
import org.devzendo.minimiser.pluginmanager.PluginRegistry;


/**
 * The Help menu. Small, has no interesting functionality that
 * couldn't be implemented directly in MenuImpl, but encapsulated
 * here to reduce the coupling in MenuImpl.
 * 
 * @author matt
 *
 */
public final class HelpMenu extends AbstractRebuildableMenuGroup {
    private static final Logger LOGGER = Logger.getLogger(HelpMenu.class);
    private final JMenu mHelpmenu;
    private String mApplicationName;

    /**
     * Construct the help menu
     * 
     * @param wiring the menu wiring
     * @param pluginRegistry the plugin registry
     */
    public HelpMenu(final MenuWiring wiring, final PluginRegistry pluginRegistry) {
        super(wiring);
        mApplicationName = pluginRegistry.getApplicationName();

        mHelpmenu = new JMenu("Help");
        mHelpmenu.setMnemonic('H');
        LOGGER.debug("Creating help menu");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JMenu getJMenu() {
        return mHelpmenu;
    }

    /**
     * @return the enabledness of the Help|Check for Updates menu
     * item
     */
    public boolean isHelpCheckForUpdatesEnabled() {
        return getMenuWiring().isMenuItemEnabled(SystemMenuIdentifiers.HELP_CHECK_FOR_UPDATES);
    }
    
    /**
     * Set the state of the Help|Check for Updates menu item
     * @param newEnabled true iff enabled
     */
    public void setHelpCheckForUpdatesEnabled(final boolean newEnabled) {
        getMenuWiring().setMenuItemEnabled(SystemMenuIdentifiers.HELP_CHECK_FOR_UPDATES, newEnabled);
    }

    /**
     * Set the application name, to be displayed when the menu is
     * rebuilt.
     * @param applicationName the application name
     */
    public synchronized void setApplicationName(final String applicationName) {
        LOGGER.debug("The help menu has been notified that the application name is " + applicationName);
        mApplicationName = applicationName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void rebuildMenuGroup() {
        LOGGER.debug("Rebuilding the help menu with application name " + mApplicationName);
        mHelpmenu.removeAll();
        
        replaceMenuItem(SystemMenuIdentifiers.HELP_WELCOME, "Welcome to " + mApplicationName, 'W', mHelpmenu);
        createMenuItem(SystemMenuIdentifiers.HELP_WHATS_NEW, "What's new in this release?", 'N', mHelpmenu);
        mHelpmenu.add(new JSeparator());
        //createMenuItem(MenuIdentifier.HelpContents, "Help Contents", 'H', menu);
        replaceMenuItem(SystemMenuIdentifiers.HELP_ABOUT, "About " + mApplicationName, 'A', mHelpmenu);
        mHelpmenu.add(new JSeparator());
        createMenuItem(SystemMenuIdentifiers.HELP_CHECK_FOR_UPDATES, "Check for updates", 'U', mHelpmenu);
    }
}
