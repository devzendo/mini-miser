package org.devzendo.minimiser.plugin.facade.providemenu;

import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.devzendo.commongui.menu.MenuWiring;
import org.devzendo.commonspring.springloader.SimpleSpringLoader;
import org.devzendo.minimiser.gui.dialog.problem.StubProblemReporter;
import org.devzendo.minimiser.gui.menu.ApplicationMenu;
import org.devzendo.minimiser.gui.menu.FileMenu;
import org.devzendo.minimiser.gui.menu.HelpMenu;
import org.devzendo.minimiser.gui.menu.Menu;
import org.devzendo.minimiser.gui.menu.MenuFacadeImpl;
import org.devzendo.minimiser.gui.menu.MenuImpl;
import org.devzendo.minimiser.gui.menu.StubRecentFilesList;
import org.devzendo.minimiser.gui.menu.ToolsMenu;
import org.devzendo.minimiser.gui.menu.ViewMenu;
import org.devzendo.minimiser.gui.menu.ViewMenuHelper;
import org.devzendo.minimiser.gui.menu.WindowMenu;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.OpenDatabaseList;
import org.devzendo.minimiser.opentablist.OpenTabList;
import org.devzendo.minimiser.persistence.DefaultPluginManagerPersistenceUnittestCase;
import org.devzendo.minimiser.pluginmanager.PluginException;
import org.devzendo.minimiser.pluginmanager.PluginManager;
import org.devzendo.minimiser.pluginmanager.PluginRegistry;
import org.devzendo.minimiser.prefs.MiniMiserPrefs;
import org.devzendo.minimiser.recentlist.RecentFilesList;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * MenuProvidingFacades must be able to create other menus, not just populate the view menu.
 *
 * @author matt
 *
 */
public class TestMenuProvidingFacadeCanCreateOtherMenus extends
    DefaultPluginManagerPersistenceUnittestCase {
    private static final String PLUGINDBNAME = "pluginmenudb";

    private SimpleSpringLoader mSpringLoader;
    private MenuWiring mMenuWiring;
    private Menu mMenu;
    private OpenDatabaseList mOpenDatabaseList;
    private RecentFilesList mRecentFilesList;

    private FileMenu mFileMenu;
    private ToolsMenu mToolsMenu;
    private WindowMenu mWindowMenu;
    private HelpMenu mHelpMenu;

    private MiniMiserPrefs mPrefs;
    private ViewMenu mViewMenu;
    private ApplicationMenu mGlobalApplicationMenu;
    private MenuProvidingHelper mMenuProvidingHelper;

    /**
     * @throws PluginException
     *         never
     */
    @Before
    public final void getPrerequisites() throws PluginException {
        mSpringLoader = new SimpleSpringLoader();

        mOpenDatabaseList = new OpenDatabaseList();
        final OpenTabList mOpenTabList = new OpenTabList();
        final PluginManager pluginManager = getPluginManager();
        final PluginRegistry pluginRegistry = getPluginRegistry();
        mMenuProvidingHelper = new MenuProvidingHelper(pluginManager);
        final MenuProvidingAppPlugin menuProvidingAppPlugin = mMenuProvidingHelper.loadStubMenuProvidingPlugin();
        mPrefs = mMenuProvidingHelper.createMockPrefs();
        mRecentFilesList = new StubRecentFilesList();
        final ViewMenuHelper viewMenuHelper =
            new ViewMenuHelper(
                mOpenDatabaseList, mOpenTabList, mPrefs);
        mMenuWiring = viewMenuHelper.getMenuWiring();

        mMenu = new MenuImpl(mSpringLoader, mMenuWiring, viewMenuHelper.getApplicationMenuCombiner());

        // Menus needed by the MenuImpl initialise...
        mFileMenu = new FileMenu(mMenuWiring, mOpenDatabaseList, mRecentFilesList);
        mSpringLoader.setBean("fileMenu", mFileMenu);

        mViewMenu = viewMenuHelper.createViewMenu();
        mSpringLoader.setBean("viewMenu", mViewMenu);

        mToolsMenu = new ToolsMenu(mMenuWiring);
        mSpringLoader.setBean("toolsMenu", mToolsMenu);

        mWindowMenu = new WindowMenu(mMenuWiring, mOpenDatabaseList);
        mSpringLoader.setBean("windowMenu", mWindowMenu);

        mHelpMenu = new HelpMenu(mMenuWiring, pluginRegistry);
        mSpringLoader.setBean("helpMenu", mHelpMenu);

        mMenu.initialise();

        Assert.assertFalse(menuProvidingAppPlugin.isInitialised());
        viewMenuHelper.initialiseMenuProvidingPlugins(
            pluginManager,
            new MenuFacadeImpl(mMenu),
            new StubProblemReporter());
        Assert.assertTrue(menuProvidingAppPlugin.isInitialised());
        mGlobalApplicationMenu = viewMenuHelper.getGlobalApplicationMenu();
        Assert.assertEquals(0, mGlobalApplicationMenu.getViewMenuTabIdentifiers().size());
    }

    /**
     *
     */
    @Test
    public final void openDatabaseCausesListenerToPopulateCustomMenus() {
        // The MenuProvidingAppPlugin adds itself as a DatabaseEvent Observer, and
        // populates the database-specific ApplicationMenu and global
        // ApplicationMenu when a database is opened.
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor(PLUGINDBNAME);
        Assert.assertNull(databaseDescriptor.getApplicationMenu());

        mOpenDatabaseList.addOpenedDatabase(databaseDescriptor);

        final ApplicationMenu databaseApplicationMenu = databaseDescriptor.getApplicationMenu();
        Assert.assertNotNull(databaseApplicationMenu);
        final List<JMenu> databaseApplicationCustomMenus = databaseApplicationMenu.getCustomMenus();
        checkDatabaseCustomMenus(databaseApplicationCustomMenus);

        final List<JMenu> globalApplicationCustomMenus = mGlobalApplicationMenu.getCustomMenus();
        checkGlobalCustomMenus(globalApplicationCustomMenus);

        final JMenuBar menuBar = mMenu.getMenuBar();
        checkMenuBar(menuBar);

        EasyMock.verify(mPrefs);
    }

    private void checkMenuBar(final JMenuBar menuBar) {
        // System
        Assert.assertEquals("File", menuBar.getMenu(0).getText());
        Assert.assertEquals("View", menuBar.getMenu(1).getText());
        // Global Application Menu
        Assert.assertEquals("Custom 3", menuBar.getMenu(2).getText());
        Assert.assertEquals("Custom 4", menuBar.getMenu(3).getText());
        // Database Application Menu
        Assert.assertEquals("Custom 1", menuBar.getMenu(4).getText());
        Assert.assertEquals("Custom 2", menuBar.getMenu(5).getText());
        // System
        Assert.assertEquals("Tools", menuBar.getMenu(6).getText());
        Assert.assertEquals("Window", menuBar.getMenu(7).getText());
        Assert.assertEquals("Help", menuBar.getMenu(8).getText());
        Assert.assertEquals(9, menuBar.getMenuCount());
    }

    private void checkDatabaseCustomMenus(
            final List<JMenu> databaseApplicationCustomMenus) {
        Assert.assertEquals(2, databaseApplicationCustomMenus.size());
        final JMenu customMenu1 = databaseApplicationCustomMenus.get(0);
        Assert.assertEquals("Custom 1", customMenu1.getText());
        Assert.assertEquals(1, customMenu1.getMenuComponentCount());
        final JMenuItem jMenuItem1 = (JMenuItem) (customMenu1.getMenuComponent(0));
        Assert.assertEquals("Hello", jMenuItem1.getText());
        final JMenu customMenu2 = databaseApplicationCustomMenus.get(1);
        Assert.assertEquals("Custom 2", customMenu2.getText());
        Assert.assertEquals(1, customMenu2.getMenuComponentCount());
        final JMenuItem jMenuItem2 = (JMenuItem) (customMenu2.getMenuComponent(0));
        Assert.assertEquals("World", jMenuItem2.getText());
    }

    private void checkGlobalCustomMenus(
            final List<JMenu> globalApplicationCustomMenus) {
        Assert.assertEquals(2, globalApplicationCustomMenus.size());
        final JMenu customMenu3 = globalApplicationCustomMenus.get(0);
        Assert.assertEquals("Custom 3", customMenu3.getText());
        Assert.assertEquals(1, customMenu3.getMenuComponentCount());
        final JMenuItem jMenuItem3 = (JMenuItem) (customMenu3.getMenuComponent(0));
        Assert.assertEquals("Bonjour", jMenuItem3.getText());
        final JMenu customMenu4 = globalApplicationCustomMenus.get(1);
        Assert.assertEquals("Custom 4", customMenu4.getText());
        Assert.assertEquals(1, customMenu4.getMenuComponentCount());
        final JMenuItem jMenuItem4 = (JMenuItem) (customMenu4.getMenuComponent(0));
        Assert.assertEquals("Au Revoir", jMenuItem4.getText());
    }
}
