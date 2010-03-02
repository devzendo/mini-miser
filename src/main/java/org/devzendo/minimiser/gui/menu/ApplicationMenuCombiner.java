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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JComponent;
import javax.swing.JMenu;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.gui.menu.ApplicationMenu.SystemMenu;
import org.devzendo.minimiser.gui.tab.TabIdentifier;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.OpenDatabaseList;
import org.devzendo.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;


/**
 * Combines the global ApplicationMenu and the current database's ApplicationMenu.
 *
 * @author matt
 *
 */
public final class ApplicationMenuCombiner {
    private static final Logger LOGGER = Logger
            .getLogger(ApplicationMenuCombiner.class);
    private static final ApplicationMenu EMPTY_APPLICATION_MENU = new ApplicationMenu();
    private final ApplicationMenu mGlobalApplicationMenu;
    private final OpenDatabaseList mOpenDatabaseList;

    /**
     * @param globalApplicationMenu the Global ApplicationMenu used in the combining of
     * all database ApplicationMenus
     * @param openDatabaseList the OpenDatabaseList from where the current database's
     * ApplicationMenu will be retrieved
     */
    public ApplicationMenuCombiner(
            final ApplicationMenu globalApplicationMenu,
            final OpenDatabaseList openDatabaseList) {
        mGlobalApplicationMenu = globalApplicationMenu;
        mOpenDatabaseList = openDatabaseList;
    }

    /**
     * Combine the global ApplicationMenu and the current database's ApplicationMenu
     * (if there is a current database, and if it has provided an ApplicationMenu) into
     * a single ApplicationMenu, with the items from the global menu taking priority
     * over those from the database menu.
     * Also combine the View menu's TabIdentifiers.
     * @return the combined ApplicationMenu
     */
    public ApplicationMenu combineMenus() {
        final ApplicationMenu combinedMenu = new ApplicationMenu();
        combineCustomMenus(combinedMenu);
        for (final ApplicationMenu.SystemMenu systemMenu : ApplicationMenu.SystemMenu.values()) {
            combineSystemMenus(combinedMenu, systemMenu);
        }
        combineViewMenuTabIdentifiers(combinedMenu);
        return combinedMenu;
    }

    private void combineViewMenuTabIdentifiers(final ApplicationMenu combinedMenu) {
        final Set<TabIdentifier> combinedTabIdentifiers = new TreeSet<TabIdentifier>(
                new Comparator<TabIdentifier>() {
                    public int compare(final TabIdentifier o1, final TabIdentifier o2) {
                        return o1.compareTo(o2);
                    }
        });
        final List<TabIdentifier> globalViewMenuTabIdentifiers = mGlobalApplicationMenu.getViewMenuTabIdentifiers();
        final List<TabIdentifier> databaseApplicationViewMenuTabIdentifiers = getDatabaseApplicationMenu().getViewMenuTabIdentifiers();
        if (!Collections.disjoint(globalViewMenuTabIdentifiers, databaseApplicationViewMenuTabIdentifiers)) {
            LOGGER.warn("Global and Application View Menu TabIdentifiers are not disjoint");
        }
        for (final TabIdentifier globalTabIdentifier : globalViewMenuTabIdentifiers) {
            if (combinedTabIdentifiers.contains(globalTabIdentifier)) {
                LOGGER.warn("The Global TabIdentifier " + globalTabIdentifier + " is already present");
            } else {
                combinedTabIdentifiers.add(globalTabIdentifier);
            }
        }
        for (final TabIdentifier applicationTabIdentifier : databaseApplicationViewMenuTabIdentifiers) {
            if (combinedTabIdentifiers.contains(applicationTabIdentifier)) {
                LOGGER.warn("The Application TabIdentifier " + applicationTabIdentifier + " is already present");
            } else {
                combinedTabIdentifiers.add(applicationTabIdentifier);
            }
        }
        for (final TabIdentifier tabIdentifier : combinedTabIdentifiers) {
            combinedMenu.addViewMenuTabIdentifier(tabIdentifier);
        }
    }

    private void combineSystemMenus(
            final ApplicationMenu combinedMenu,
            final SystemMenu systemMenu) {
        addSystemMenuToCombinedMenu(combinedMenu, systemMenu, mGlobalApplicationMenu.getMenu(systemMenu));
        addSystemMenuToCombinedMenu(combinedMenu, systemMenu, getDatabaseApplicationMenu().getMenu(systemMenu));
    }

    private void addSystemMenuToCombinedMenu(
            final ApplicationMenu combinedMenu,
            final SystemMenu systemMenu,
            final List<JComponent> systemMenuComponents) {
        for (final JComponent systemMenuComponent : systemMenuComponents) {
            combinedMenu.addMenuComponent(systemMenu, systemMenuComponent);
        }
    }

    private void combineCustomMenus(final ApplicationMenu combinedMenu) {
        for (final JMenu globalJMenu : mGlobalApplicationMenu.getCustomMenus()) {
            combinedMenu.addCustomMenu(globalJMenu);
        }
        for (final JMenu databaseJMenu : getDatabaseApplicationMenu().getCustomMenus()) {
            combinedMenu.addCustomMenu(databaseJMenu);
        }
    }

    private ApplicationMenu getDatabaseApplicationMenu() {
        final DatabaseDescriptor descriptor = mOpenDatabaseList.getCurrentDatabase();
        if (descriptor != null) {
            final ApplicationMenu databaseApplicationMenu = (ApplicationMenu) descriptor.getAttribute(AttributeIdentifier.ApplicationMenu);
            if (databaseApplicationMenu != null) {
                return databaseApplicationMenu;
            }
        }
        return EMPTY_APPLICATION_MENU;
    }
}
