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

import java.awt.Component;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.devzendo.commonapp.gui.menu.MenuWiring;
import org.devzendo.minimiser.gui.tab.SystemTabIdentifiers;
import org.devzendo.minimiser.gui.tab.TabIdentifier;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.OpenDatabaseList;
import org.devzendo.minimiser.opentablist.OpenTabList;
import org.devzendo.minimiser.prefs.MiniMiserPrefs;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests written after the development of ViewMenu - it wasn't done with TDD.
 *
 * @author matt
 *
 */
public final class TestViewMenu {

    private ViewMenu mViewMenu;
    private OpenDatabaseList mOpenDatabaseList;
    private MiniMiserPrefs mPrefs;
    private ApplicationMenu mGlobalApplicationMenu;

    /**
     *
     */
    @Before
    public void getPrerequisites() {
        final MenuWiring menuWiring = new MenuWiring();
        mOpenDatabaseList = new OpenDatabaseList();
        final OpenTabList openTabList = new OpenTabList();
        mPrefs = EasyMock.createMock(MiniMiserPrefs.class);
        mGlobalApplicationMenu = new ApplicationMenu();
        final ApplicationMenuCombiner applicationMenuCombiner = new ApplicationMenuCombiner(mGlobalApplicationMenu, mOpenDatabaseList);
        mViewMenu = new ViewMenu(menuWiring, mOpenDatabaseList, openTabList, mPrefs, applicationMenuCombiner);
    }

    /**
     *
     */
    @Test
    public void menuDisabledIfNoDatabases() {
        mViewMenu.rebuildMenuGroup();
        Assert.assertFalse(mViewMenu.getJMenu().isEnabled());
    }

    /**
     *
     */
    @Test
    public void nonPermanentSystemTabIdentifiersAllPresent() {
        EasyMock.expect(mPrefs.isTabHidden((String) EasyMock.anyObject())).andReturn(false).anyTimes();
        EasyMock.replay(mPrefs);
        mOpenDatabaseList.addOpenedDatabase(new DatabaseDescriptor("testdb"));

        mViewMenu.rebuildMenuGroup();

        final JMenu jMenu = mViewMenu.getJMenu();
        Assert.assertTrue(jMenu.isEnabled());
        final int numberOfNonPermanentTabIdentifiers =
            SystemTabIdentifiers.values().length
            - SystemTabIdentifiers.getPermanentTabIdentifiers().size();
        Assert.assertEquals(numberOfNonPermanentTabIdentifiers, jMenu.getMenuComponentCount());
        EasyMock.verify(mPrefs);
    }

    /**
     *
     */
    @Test
    public void hiddenTabIdentifierNotPresent() {
        EasyMock.expect(mPrefs.isTabHidden(EasyMock.eq("SQL"))).andReturn(true).anyTimes();
        EasyMock.expect(mPrefs.isTabHidden((String) EasyMock.anyObject())).andReturn(false).anyTimes();
        EasyMock.replay(mPrefs);
        mOpenDatabaseList.addOpenedDatabase(new DatabaseDescriptor("testdb"));

        mViewMenu.rebuildMenuGroup();

        final JMenu jMenu = mViewMenu.getJMenu();
        boolean foundSQL = false;
        final Component[] menuComponents = jMenu.getMenuComponents();
        for (final Component component : menuComponents) {
            final JMenuItem jMenuItem = (JMenuItem) component;
            if (jMenuItem.getText().equals(SystemTabIdentifiers.SQL.getDisplayableName())) {
                foundSQL = true;
            }
        }
        Assert.assertFalse(foundSQL);
        EasyMock.verify(mPrefs);
    }

    /**
     *
     */
    @Test
    public void databaseApplicationMenuTabIdentifierPresent() {
        final TabIdentifier tabIdentifier = createTestTabIdentifier();
        EasyMock.expect(mPrefs.isTabHidden((String) EasyMock.anyObject())).andReturn(false).anyTimes();
        EasyMock.replay(mPrefs);
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("testdb");
        mOpenDatabaseList.addOpenedDatabase(databaseDescriptor);
        final ApplicationMenu databaseApplicationMenu = new ApplicationMenu();
        databaseApplicationMenu.addViewMenuTabIdentifier(tabIdentifier);
        databaseDescriptor.setApplicationMenu(databaseApplicationMenu);

        mViewMenu.rebuildMenuGroup();

        checkForTestTabIdentifier(tabIdentifier);
        EasyMock.verify(mPrefs);
    }

    /**
     *
     */
    @Test
    public void globalApplicationMenuTabIdentifierPresent() {
        final TabIdentifier tabIdentifier = createTestTabIdentifier();
        EasyMock.expect(mPrefs.isTabHidden((String) EasyMock.anyObject())).andReturn(false).anyTimes();
        EasyMock.replay(mPrefs);
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("testdb");
        mOpenDatabaseList.addOpenedDatabase(databaseDescriptor);
        mGlobalApplicationMenu.addViewMenuTabIdentifier(tabIdentifier);
        final ApplicationMenu databaseApplicationMenu = new ApplicationMenu();
        databaseDescriptor.setApplicationMenu(databaseApplicationMenu);

        mViewMenu.rebuildMenuGroup();

        checkForTestTabIdentifier(tabIdentifier);
        EasyMock.verify(mPrefs);
    }

    /**
     *
     */
    @Test
    public void viewMenuOrderingCorrect() {
        final TabIdentifier tabIdentifierA = new TabIdentifier("AARDVARK", "Aardvark", false, 'A', "irrelevantTabBean", null);
        final TabIdentifier tabIdentifierZ = new TabIdentifier("ZYGOTE", "Zygote", false, 'Z', "irrelevantTabBean", null);
        EasyMock.expect(mPrefs.isTabHidden((String) EasyMock.anyObject())).andReturn(false).anyTimes();
        EasyMock.replay(mPrefs);
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor("testdb");
        mOpenDatabaseList.addOpenedDatabase(databaseDescriptor);
        mGlobalApplicationMenu.addViewMenuTabIdentifier(tabIdentifierA);
        final ApplicationMenu databaseApplicationMenu = new ApplicationMenu();
        databaseApplicationMenu.addViewMenuTabIdentifier(tabIdentifierZ);
        databaseDescriptor.setApplicationMenu(databaseApplicationMenu);

        mViewMenu.rebuildMenuGroup();

        final JMenu jMenu = mViewMenu.getJMenu();
        final Component[] menuComponents = jMenu.getMenuComponents();
        Assert.assertEquals(4, menuComponents.length); // Remember Overview is permanent!
        checkTabIdentifierDisplayableName(menuComponents[0], SystemTabIdentifiers.CATEGORIES);
        checkTabIdentifierDisplayableName(menuComponents[1], SystemTabIdentifiers.SQL);
        checkTabIdentifierDisplayableName(menuComponents[2], tabIdentifierA);
        checkTabIdentifierDisplayableName(menuComponents[3], tabIdentifierZ);
        EasyMock.verify(mPrefs);
    }

    private void checkTabIdentifierDisplayableName(
            final Component component,
            final TabIdentifier tabIdentifier) {
        final JMenuItem jMenuItem = (JMenuItem) component;
        Assert.assertEquals(jMenuItem.getText(), tabIdentifier.getDisplayableName());
    }

    private void checkForTestTabIdentifier(final TabIdentifier tabIdentifier) {
        final JMenu jMenu = mViewMenu.getJMenu();

        boolean foundTestTabIdentifier = false;
        final Component[] menuComponents = jMenu.getMenuComponents();
        for (final Component component : menuComponents) {
            final JMenuItem jMenuItem = (JMenuItem) component;
            if (jMenuItem.getText().equals(tabIdentifier.getDisplayableName())) {
                foundTestTabIdentifier = true;
            }
        }
        Assert.assertTrue(foundTestTabIdentifier);
    }

    private TabIdentifier createTestTabIdentifier() {
        return new TabIdentifier("TEST", "Test Tab Identifier", false, 'T', "irrelevantTabBean", null);
    }
}
