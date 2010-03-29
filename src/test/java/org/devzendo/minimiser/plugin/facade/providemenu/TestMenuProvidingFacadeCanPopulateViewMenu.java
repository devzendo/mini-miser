package org.devzendo.minimiser.plugin.facade.providemenu;

import java.awt.Component;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.devzendo.minimiser.gui.dialog.problem.StubProblemReporter;
import org.devzendo.minimiser.gui.menu.ApplicationMenu;
import org.devzendo.minimiser.gui.menu.MenuFacade;
import org.devzendo.minimiser.gui.menu.MenuFacadeImpl;
import org.devzendo.minimiser.gui.menu.StubMenuForViewMenuTests;
import org.devzendo.minimiser.gui.menu.ViewMenu;
import org.devzendo.minimiser.gui.menu.ViewMenuHelper;
import org.devzendo.minimiser.gui.tab.SystemTabIdentifiers;
import org.devzendo.minimiser.gui.tab.TabIdentifier;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.OpenDatabaseList;
import org.devzendo.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;
import org.devzendo.minimiser.opentablist.OpenTabList;
import org.devzendo.minimiser.persistence.DefaultPluginManagerPersistenceUnittestCase;
import org.devzendo.minimiser.plugin.Plugin;
import org.devzendo.minimiser.pluginmanager.PluginException;
import org.devzendo.minimiser.pluginmanager.PluginManager;
import org.devzendo.minimiser.prefs.Prefs;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test that the view menu is correctly initialised, after a
 * database is opened. It should contain several TabIdentifiers.
 *
 * @author matt
 *
 */
public class TestMenuProvidingFacadeCanPopulateViewMenu extends
        DefaultPluginManagerPersistenceUnittestCase {
    private static final String PLUGINDBNAME = "pluginmenudb";

    private OpenDatabaseList mOpenDatabaseList;
    private Prefs mPrefs;
    private ViewMenu mViewMenu;
    private ApplicationMenu mGlobalApplicationMenu;

    /**
     * @throws PluginException
     *         never
     */
    @Before
    public final void getPrerequisites() throws PluginException {
        mOpenDatabaseList = new OpenDatabaseList();
        final OpenTabList mOpenTabList = new OpenTabList();
        final PluginManager pluginManager = getPluginManager();
        pluginManager.loadPlugins(
            "org/devzendo/minimiser/plugin/facade/providemenu/providemenuplugin.properties");
        final MenuProvidingAppPlugin menuProvidingAppPlugin = getCheckedMenuProvidingAppPlugin(pluginManager);
        mPrefs = createMockPrefs();
        final ViewMenuHelper viewMenuHelper =
            new ViewMenuHelper(
                mOpenDatabaseList, mOpenTabList, mPrefs);
        mViewMenu = viewMenuHelper.createViewMenu();
        Assert.assertFalse(menuProvidingAppPlugin.isInitialised());
        viewMenuHelper.initialiseMenuProvidingPlugins(
            pluginManager,
            createMenuFacade(mViewMenu),
            new StubProblemReporter());
        Assert.assertTrue(menuProvidingAppPlugin.isInitialised());
        mGlobalApplicationMenu = viewMenuHelper.getGlobalApplicationMenu();
        Assert.assertEquals(0, mGlobalApplicationMenu.getViewMenuTabIdentifiers().size());
    }

    private MenuProvidingAppPlugin getCheckedMenuProvidingAppPlugin(
            final PluginManager pluginManager) {
        final List<Plugin> plugins = pluginManager.getPlugins();
        Assert.assertEquals(1, plugins.size());
        final MenuProvidingAppPlugin menuProvidingAppPlugin = (MenuProvidingAppPlugin) plugins.get(0);
        Assert.assertNotNull(menuProvidingAppPlugin);
        final List<MenuProviding> menuProvidingPlugins = pluginManager
                .getPluginsImplementingFacade(MenuProviding.class);
        Assert.assertEquals(1, menuProvidingPlugins.size());
        return menuProvidingAppPlugin;
    }

    private MenuFacade createMenuFacade(final ViewMenu viewMenu) {
        return new MenuFacadeImpl(
            new StubMenuForViewMenuTests(viewMenu));
    }

    private Prefs createMockPrefs() {
        final Prefs prefs = EasyMock.createMock(Prefs.class);
        for (final TabIdentifier systemTabIdentifier : SystemTabIdentifiers.values()) {
            EasyMock.expect(prefs.isTabHidden(systemTabIdentifier.getTabName())).andReturn(Boolean.FALSE);
        }
        EasyMock.expect(prefs.isTabHidden("APPLICATION")).andReturn(Boolean.FALSE);
        EasyMock.expect(prefs.isTabHidden("GLOBAL")).andReturn(Boolean.FALSE);
        EasyMock.replay(prefs);
        return prefs;
    }

    /**
     *
     */
    @Test
    public final void openDatabaseCausesListenerToPopulateViewMenu() {
        // The MenuProvidingAppPlugin adds itself as a DatabaseEvent Observer, and
        // populates the database-specific ApplicationMenu and global
        // ApplicationMenu when a database is opened.
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor(PLUGINDBNAME);
        Assert.assertNull(databaseDescriptor.getAttribute(AttributeIdentifier.ApplicationMenu));

        mOpenDatabaseList.addOpenedDatabase(databaseDescriptor);

        final ApplicationMenu databaseApplicationMenu = (ApplicationMenu) databaseDescriptor.getAttribute(AttributeIdentifier.ApplicationMenu);
        Assert.assertNotNull(databaseApplicationMenu);
        Assert.assertEquals(1, databaseApplicationMenu.getViewMenuTabIdentifiers().size());
        Assert.assertEquals("Application menu entry", databaseApplicationMenu.getViewMenuTabIdentifiers().get(0).getDisplayableName());

        EasyMock.verify(mPrefs);

        final JMenu jMenu = mViewMenu.getJMenu();
        Assert.assertTrue(jMenu.isEnabled());
        final Component[] components = jMenu.getMenuComponents();
        Assert.assertEquals(4, components.length);
        Assert.assertTrue(componentsContains(components, SystemTabIdentifiers.SQL.getDisplayableName()));
        Assert.assertTrue(componentsContains(components, SystemTabIdentifiers.CATEGORIES.getDisplayableName()));
        Assert.assertFalse(componentsContains(components, SystemTabIdentifiers.OVERVIEW.getDisplayableName())); // 'Cos it's permanent
        Assert.assertTrue(componentsContains(components, "Application menu entry"));
        Assert.assertTrue(componentsContains(components, "Global menu entry"));
        Assert.assertFalse(componentsContains(components, "Father Christmas")); // 'Cos he doesn't exist. Sorry, kids!

        Assert.assertEquals(1, mGlobalApplicationMenu.getViewMenuTabIdentifiers().size());
        Assert.assertEquals("Global menu entry", mGlobalApplicationMenu.getViewMenuTabIdentifiers().get(0).getDisplayableName());
    }

    private boolean componentsContains(
            final Component[] components,
            final String displayableName) {
        for (final Component component : components) {
            if (((JMenuItem) component).getText().equals(displayableName)) {
                return true;
            }
        }
        return false;
    }
}