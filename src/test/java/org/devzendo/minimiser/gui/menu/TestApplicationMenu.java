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

import org.devzendo.minimiser.gui.tab.SystemTabIdentifiers;
import org.devzendo.minimiser.gui.tab.TabIdentifier;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests the validation of the ApplicationMenu.
 *
 * @author matt
 *
 */
public final class TestApplicationMenu {
    private ApplicationMenu mAppMenu;

    /**
     *
     */
    @Before
    public void getPrerequisites() {
        mAppMenu = new ApplicationMenu();
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void fileCannotBeAddedAsCustomMenu() {
        mAppMenu.addCustomMenu(new JMenu("File"));
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void editCannotBeAddedAsCustomMenu() {
        mAppMenu.addCustomMenu(new JMenu("Edit"));
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void viewCannotBeAddedAsCustomMenu() {
        mAppMenu.addCustomMenu(new JMenu("View"));
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void windowCannotBeAddedAsCustomMenu() {
        mAppMenu.addCustomMenu(new JMenu("Window"));
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void toolsCannotBeAddedAsCustomMenu() {
        mAppMenu.addCustomMenu(new JMenu("Tools"));
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void helpCannotBeAddedAsCustomMenu() {
        mAppMenu.addCustomMenu(new JMenu("Help"));
    }

    /**
     *
     */
    @Test
    public void canAddACustomMenu() {
        mAppMenu.addCustomMenu(new JMenu("Fish"));
        final List<JMenu> customMenus = mAppMenu.getCustomMenus();
        Assert.assertEquals(1, customMenus.size());
        Assert.assertEquals("Fish", customMenus.get(0).getText());
    }

    /**
     *
     */
    @Test
    public void canAddItemToFileMenu() {
        mAppMenu.addMenuComponent(ApplicationMenu.SystemMenu.File, new JMenuItem("Export..."));
        final List<JComponent> fileMenu = mAppMenu.getMenu(ApplicationMenu.SystemMenu.File);
        Assert.assertEquals(1, fileMenu.size());
        Assert.assertEquals("Export...", ((JMenuItem) fileMenu.get(0)).getText());
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void cannotAddNewItemToFileMenu() {
        mAppMenu.addMenuComponent(ApplicationMenu.SystemMenu.File, new JMenuItem("New..."));
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void cannotAddOpenItemToFileMenu() {
        mAppMenu.addMenuComponent(ApplicationMenu.SystemMenu.File, new JMenuItem("Open..."));
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void cannotAddCloseItemToFileMenu() {
        mAppMenu.addMenuComponent(ApplicationMenu.SystemMenu.File, new JMenuItem("Close"));
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void cannotAddCloseAllItemToFileMenu() {
        mAppMenu.addMenuComponent(ApplicationMenu.SystemMenu.File, new JMenuItem("Close all"));
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void cannotAddExitItemToFileMenu() {
        mAppMenu.addMenuComponent(ApplicationMenu.SystemMenu.File, new JMenuItem("Exit"));
    }

    /**
     *
     */
    @Test
    public void canAddJComponentToViewMenu() {
        mAppMenu.addMenuComponent(ApplicationMenu.SystemMenu.View, new JMenuItem("All accounts"));
        final List<JComponent> viewMenu = mAppMenu.getMenu(ApplicationMenu.SystemMenu.View);
        Assert.assertEquals(1, viewMenu.size());
        Assert.assertEquals("All accounts", ((JMenuItem) viewMenu.get(0)).getText());
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void cannotAddUndoItemToEditMenu() {
        mAppMenu.addMenuComponent(ApplicationMenu.SystemMenu.Edit, new JMenuItem("Undo"));
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void cannotAddRedoItemToEditMenu() {
        mAppMenu.addMenuComponent(ApplicationMenu.SystemMenu.Edit, new JMenuItem("Redo"));
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void cannotAddCutItemToEditMenu() {
        mAppMenu.addMenuComponent(ApplicationMenu.SystemMenu.Edit, new JMenuItem("Cut"));
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void cannotAddCopyItemToEditMenu() {
        mAppMenu.addMenuComponent(ApplicationMenu.SystemMenu.Edit, new JMenuItem("Copy"));
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void cannotAddPasteItemToEditMenu() {
        mAppMenu.addMenuComponent(ApplicationMenu.SystemMenu.Edit, new JMenuItem("Paste"));
    }

    /**
     *
     */
    @Test
    public void canAddItemToEditMenu() {
        mAppMenu.addMenuComponent(ApplicationMenu.SystemMenu.Edit, new JMenuItem("Object style"));
        final List<JComponent> editMenu = mAppMenu.getMenu(ApplicationMenu.SystemMenu.Edit);
        Assert.assertEquals(1, editMenu.size());
        Assert.assertEquals("Object style", ((JMenuItem) editMenu.get(0)).getText());
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void cannotAddOptionsItemToToolsMenu() {
        mAppMenu.addMenuComponent(ApplicationMenu.SystemMenu.Tools, new JMenuItem("Options"));
    }

    /**
     *
     */
    @Test
    public void canAddItemToToolsMenu() {
        mAppMenu.addMenuComponent(ApplicationMenu.SystemMenu.Tools, new JMenuItem("Spanner"));
        final List<JComponent> toolsMenu = mAppMenu.getMenu(ApplicationMenu.SystemMenu.Tools);
        Assert.assertEquals(1, toolsMenu.size());
        Assert.assertEquals("Spanner", ((JMenuItem) toolsMenu.get(0)).getText());
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void cannotAddToWindowMenu() {
        mAppMenu.addMenuComponent(ApplicationMenu.SystemMenu.Window, new JMenuItem("non licet abire"));
    }

    /**
     *
     */
    @Test
    public void emptySystemMenus() {
        for (final ApplicationMenu.SystemMenu systemMenu : ApplicationMenu.SystemMenu.values()) {
            Assert.assertEquals("The " + systemMenu.toString() + " menu is not initially empty",
                0, mAppMenu.getMenu(systemMenu).size());
        }
    }

    /**
     *
     */
    @Test
    public void multipleSeparatorsCoalesced() {
        for (final ApplicationMenu.SystemMenu systemMenu : ApplicationMenu.SystemMenu.values()) {
            if (systemMenu == ApplicationMenu.SystemMenu.Window) {
                continue;
            }
            mAppMenu.addMenuComponent(systemMenu, new JMenuItem("One"));
            mAppMenu.addMenuComponent(systemMenu, new JSeparator());
            mAppMenu.addMenuComponent(systemMenu, new JSeparator());
            mAppMenu.addMenuComponent(systemMenu, new JMenuItem("Two"));
            final List<JComponent> menu = mAppMenu.getMenu(systemMenu);
            Assert.assertEquals(3, menu.size());
            Assert.assertEquals("One", ((JMenuItem) menu.get(0)).getText());
            Assert.assertTrue(menu.get(1) instanceof JSeparator);
            Assert.assertEquals("Two", ((JMenuItem) menu.get(2)).getText());
        }
    }

    /**
     *
     */
    @Test
    public void cannotAddSystemTabIdentifiersToViewMenu() {
        for (final TabIdentifier systemTabIdentifier : SystemTabIdentifiers.values()) {
            try {
                mAppMenu.addViewMenuTabIdentifier(systemTabIdentifier);
                Assert.fail(
                    "Should not have been able to add a SystemTabIdentifier to the view menu, "
                    + "but succeeded with " + systemTabIdentifier);
            } catch (final IllegalArgumentException iae) {
                // OK
            }
        }
    }

    /**
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void cannotAddNullTabIdentifierToViewMenu() {
        mAppMenu.addViewMenuTabIdentifier(null);
    }

    /**
     *
     */
    @Test
    public void canAddTabIdentifiersToViewMenu() {
        final TabIdentifier liveFeedTabId = new TabIdentifier("LF", "Live stats feed", false, 'S', "irrelevantTabName", null);
        final TabIdentifier performanceTabId = new TabIdentifier("P", "Performance", false, 'P', "irrelevantTabName", null);
        Assert.assertTrue(mAppMenu.addViewMenuTabIdentifier(performanceTabId));
        Assert.assertTrue(mAppMenu.addViewMenuTabIdentifier(liveFeedTabId));
        Assert.assertFalse(mAppMenu.addViewMenuTabIdentifier(performanceTabId)); // Yes, add twice

        final List<TabIdentifier> sortedDeDuped = mAppMenu.getViewMenuTabIdentifiers();
        Assert.assertEquals(2, sortedDeDuped.size());
        Assert.assertSame(liveFeedTabId, sortedDeDuped.get(0));
        Assert.assertSame(performanceTabId, sortedDeDuped.get(1));
    }

    @Test
    public void canRemoveTabIdentifiersFromViewMenu() {
        final TabIdentifier tabId = new TabIdentifier("P", "Performance", false, 'P', "irrelevantTabName", null);
        Assert.assertFalse(mAppMenu.containsViewMenuTabIdentifier(tabId));
        Assert.assertFalse(mAppMenu.removeViewMenuTabIdentifier(tabId));
        Assert.assertTrue(mAppMenu.addViewMenuTabIdentifier(tabId));
        Assert.assertTrue(mAppMenu.containsViewMenuTabIdentifier(tabId));
        Assert.assertTrue(mAppMenu.removeViewMenuTabIdentifier(tabId));
        Assert.assertFalse(mAppMenu.containsViewMenuTabIdentifier(tabId));
        Assert.assertFalse(mAppMenu.removeViewMenuTabIdentifier(tabId));
    }
}
