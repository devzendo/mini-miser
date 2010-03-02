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

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import org.devzendo.minimiser.gui.tab.TabIdentifier;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.OpenDatabaseList;
import org.devzendo.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



/**
 * Tests for the combining of global application menu and database-specific application menu.
 *
 * @author matt
 *
 */
public final class TestApplicationMenuCombiner {
    private ApplicationMenu mGlobalApplicationMenu;
    private OpenDatabaseList mOpenDatabaseList;
    private ApplicationMenuCombiner mApplicationMenuCombiner;

    /**
     *
     */
    @Before
    public void getPrerequisites() {
        mGlobalApplicationMenu = new ApplicationMenu();
        mOpenDatabaseList = new OpenDatabaseList();
        mApplicationMenuCombiner = new ApplicationMenuCombiner(
            mGlobalApplicationMenu, mOpenDatabaseList);
    }

    /**
     *
     */
    @Test
    public void emptyApplicationMenus() {
        Assert.assertNull(mOpenDatabaseList.getCurrentDatabase());

        final ApplicationMenu menu = mApplicationMenuCombiner.combineMenus();

        Assert.assertEquals(0, menu.getCustomMenus().size());
        for (final ApplicationMenu.SystemMenu systemMenu :
            ApplicationMenu.SystemMenu.values()) {
            final List<JComponent> systemMenuList = menu.getMenu(systemMenu);
            Assert.assertEquals(0, systemMenuList.size());
        }
    }

    /**
     *
     */
    @Test
    public void customMenusFromGlobalAreOrderedFirst() {
        mGlobalApplicationMenu.addCustomMenu(new JMenu("GlobalCustom"));
        final ApplicationMenu databaseApplicationMenu = new ApplicationMenu();
        databaseApplicationMenu.addCustomMenu(new JMenu("DatabaseCustom"));
        setCurrentDatabaseApplicationMenu(databaseApplicationMenu);
        Assert.assertNotNull(mOpenDatabaseList.getCurrentDatabase());

        final ApplicationMenu menu = mApplicationMenuCombiner.combineMenus();

        final List<JMenu> customMenus = menu.getCustomMenus();
        Assert.assertEquals(2, customMenus.size());
        Assert.assertEquals("GlobalCustom", customMenus.get(0).getText());
        Assert.assertEquals("DatabaseCustom", customMenus.get(1).getText());
    }

    private void setCurrentDatabaseApplicationMenu(
            final ApplicationMenu applicationMenu) {
        final DatabaseDescriptor descriptor = new DatabaseDescriptor("FOO");
        descriptor.setAttribute(AttributeIdentifier.ApplicationMenu, applicationMenu);
        mOpenDatabaseList.addOpenedDatabase(descriptor);
    }

    /**
     *
     */
    @Test
    public void separatorsBetweenGlobalAndDatabaseAreCoalesced() {
        mGlobalApplicationMenu.addMenuComponent(
            ApplicationMenu.SystemMenu.File, new JSeparator());
        final ApplicationMenu databaseApplicationMenu = new ApplicationMenu();
        databaseApplicationMenu.addMenuComponent(
            ApplicationMenu.SystemMenu.File, new JSeparator());
        setCurrentDatabaseApplicationMenu(databaseApplicationMenu);

        final ApplicationMenu menu = mApplicationMenuCombiner.combineMenus();

        final List<JComponent> fileMenu = menu.getMenu(ApplicationMenu.SystemMenu.File);
        Assert.assertEquals(1, fileMenu.size());
        Assert.assertTrue(fileMenu.get(0) instanceof JSeparator);
    }

    /**
     *
     */
    @Test
    public void menuItemsFromGlobalAreOrderedFirst() {
        mGlobalApplicationMenu.addMenuComponent(
            ApplicationMenu.SystemMenu.File, new JMenuItem("GlobalMenuItem"));
        final ApplicationMenu databaseApplicationMenu = new ApplicationMenu();
        databaseApplicationMenu.addMenuComponent(
            ApplicationMenu.SystemMenu.File, new JMenuItem("DatabaseMenuItem"));
        setCurrentDatabaseApplicationMenu(databaseApplicationMenu);

        final ApplicationMenu menu = mApplicationMenuCombiner.combineMenus();

        final List<JComponent> fileMenu = menu.getMenu(ApplicationMenu.SystemMenu.File);
        Assert.assertEquals(2, fileMenu.size());
        Assert.assertEquals("GlobalMenuItem", ((JMenuItem) fileMenu.get(0)).getText());
        Assert.assertEquals("DatabaseMenuItem", ((JMenuItem) fileMenu.get(1)).getText());

    }

    /**
     *
     */
    @Test
    public void closingDatabaseJustYieldsGlobal() {
        mGlobalApplicationMenu.addMenuComponent(
            ApplicationMenu.SystemMenu.File, new JMenuItem("GlobalMenuItem"));
        final ApplicationMenu databaseApplicationMenu = new ApplicationMenu();
        databaseApplicationMenu.addMenuComponent(
            ApplicationMenu.SystemMenu.File, new JMenuItem("DatabaseMenuItem"));
        setCurrentDatabaseApplicationMenu(databaseApplicationMenu);
        final DatabaseDescriptor currentDatabase = mOpenDatabaseList.getCurrentDatabase();
        Assert.assertNotNull(currentDatabase);
        mOpenDatabaseList.removeClosedDatabase(currentDatabase);

        final ApplicationMenu menu = mApplicationMenuCombiner.combineMenus();

        final List<JComponent> fileMenu = menu.getMenu(ApplicationMenu.SystemMenu.File);
        Assert.assertEquals(1, fileMenu.size());
        Assert.assertEquals("GlobalMenuItem", ((JMenuItem) fileMenu.get(0)).getText());
    }

    /**
     *
     */
    @Test
    public void tabIdentifiersAreCombinedAndSortedByDisplayableName() {
        mGlobalApplicationMenu.addViewMenuTabIdentifier(
            new TabIdentifier("G", "Global tabidentifier", false, 'G', "irrelevantTabName", null));
        final ApplicationMenu databaseApplicationMenu = new ApplicationMenu();
        databaseApplicationMenu.addViewMenuTabIdentifier(
            new TabIdentifier("D", "Database tabidentifier", false, 'D', "irrelevantTabName", null));
        setCurrentDatabaseApplicationMenu(databaseApplicationMenu);

        final ApplicationMenu menu = mApplicationMenuCombiner.combineMenus();
        final List<TabIdentifier> viewMenuTabIdentifiers = menu.getViewMenuTabIdentifiers();
        Assert.assertEquals(2, viewMenuTabIdentifiers.size());
        Assert.assertEquals("D", viewMenuTabIdentifiers.get(0).getTabName());
        Assert.assertEquals("G", viewMenuTabIdentifiers.get(1).getTabName());
    }

    /**
     *
     */
    @Test
    public void tabIdentifiersAreDeDupedByTabName() {
        mGlobalApplicationMenu.addViewMenuTabIdentifier(
            new TabIdentifier("G", "Global tabidentifier", false, 'G', "irrelevantTabName", null));
        final ApplicationMenu databaseApplicationMenu = new ApplicationMenu();
        databaseApplicationMenu.addViewMenuTabIdentifier(
            new TabIdentifier("G", "Global tabidentifier", false, 'G', "irrelevantTabName", null));
        setCurrentDatabaseApplicationMenu(databaseApplicationMenu);
        final ApplicationMenu menu = mApplicationMenuCombiner.combineMenus();
        final List<TabIdentifier> viewMenuTabIdentifiers = menu.getViewMenuTabIdentifiers();
        Assert.assertEquals(1, viewMenuTabIdentifiers.size());
        Assert.assertEquals("G", viewMenuTabIdentifiers.get(0).getTabName());
    }
}
