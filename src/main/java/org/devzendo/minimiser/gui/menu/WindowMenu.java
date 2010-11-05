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
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.commoncode.patterns.observer.ObserverList;
import org.devzendo.commongui.menu.AbstractRebuildableMenuGroup;
import org.devzendo.commongui.menu.MenuWiring;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.OpenDatabaseList;


/**
 * The Window menu rebuilds when databases are added, removed, or switched to.
 * It contains a list of all the open databases, allowing the user to switch
 * between them.
 * 
 * @author matt
 *
 */
public final class WindowMenu extends AbstractRebuildableMenuGroup {
    private static final Logger LOGGER = Logger.getLogger(WindowMenu.class);
    private ObserverList<DatabaseNameChoice> windowMenuChoiceObservers;
    private JMenu windowMenu;
    private final OpenDatabaseList openDatabaseList;

    /**
     * Construct the Window Menu
     * 
     * @param wiring the menu wiring
     * @param databaseList the Open Database List
     */
    public WindowMenu(final MenuWiring wiring,
            final OpenDatabaseList databaseList) {
        super(wiring);
        this.openDatabaseList = databaseList;
        windowMenuChoiceObservers = new ObserverList<DatabaseNameChoice>();
        windowMenu = new JMenu("Window");
        windowMenu.setMnemonic('W');
        // no need to call buildWindowMenu() as it's not populated initially
        windowMenu.setEnabled(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rebuildMenuGroup() {
        LOGGER.debug("building window menu");
        windowMenu.removeAll();
        final int numberOfDatabases = openDatabaseList.getNumberOfDatabases();
        if (numberOfDatabases == 0) {
            windowMenu.setEnabled(false);
            LOGGER.debug("window menu is empty");
            return;
        }
        final List<DatabaseDescriptor> openDatabases = openDatabaseList.getOpenDatabases();
        final DatabaseDescriptor currentDatabaseDescriptor = openDatabaseList.getCurrentDatabase();
        LOGGER.debug("building window list");
        for (int i = 0; i < numberOfDatabases; i++) {
            final DatabaseDescriptor databaseDescriptor = openDatabases.get(i);
            final String databaseName = databaseDescriptor.getDatabaseName();
            LOGGER.debug("adding " + databaseName + " to window list");
            final JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(databaseName, databaseDescriptor.equals(currentDatabaseDescriptor));
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    // TODO: might be better to spawn this on a separate thread?
                    windowMenuChoiceObservers.eventOccurred(new DatabaseNameChoice(databaseName));
                }
            });
            windowMenu.add(menuItem);
        }
        windowMenu.setEnabled(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JMenu getJMenu() {
        return windowMenu;
    }

    /**
     * Add a database switch observer
     * @param observer the observer
     */
    public void addDatabaseSwitchObserver(final Observer<DatabaseNameChoice> observer) {
        windowMenuChoiceObservers.addObserver(observer);
    }
}
