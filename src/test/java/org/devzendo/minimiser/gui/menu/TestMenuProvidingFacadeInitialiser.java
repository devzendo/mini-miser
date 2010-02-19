package org.devzendo.minimiser.gui.menu;

import java.util.List;

import org.devzendo.minimiser.gui.dialog.problem.StubProblemReporter;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.OpenDatabaseList;
import org.devzendo.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;
import org.devzendo.minimiser.plugin.Plugin;
import org.devzendo.minimiser.plugin.facade.providemenu.MenuProviding;
import org.devzendo.minimiser.pluginmanager.DefaultPluginManager;
import org.devzendo.minimiser.pluginmanager.DefaultPluginRegistry;
import org.devzendo.minimiser.pluginmanager.PluginException;
import org.devzendo.minimiser.springloader.SimpleSpringLoader;
import org.devzendo.minimiser.springloader.SpringLoader;
import org.devzendo.minimiser.wiring.databaseeventlistener.ApplicationMenuCreatingDatabaseEventListener;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



/**
 * Tests that MenuProvidingFacades are correctly loaded and initialised.
 * @author matt
 *
 */
public final class TestMenuProvidingFacadeInitialiser {
    private static final String DATABASE = "db";
    private SpringLoader mSpringLoader;
    private DefaultPluginRegistry mPluginRegistry;
    private DefaultPluginManager mPluginManager;
    private OpenDatabaseList mOpenDatabaseList;
    private StubMenu mMenu;
    private ApplicationMenu mGlobalApplicationMenu;
    private StubProblemReporter mStubProblemReporter;
    private MenuProvidingFacadeAppPlugin mApplicationPlugin;

    /**
     * @throws PluginException never
     */
    @Before
    public void getPrerequisites() throws PluginException {
        mSpringLoader = new SimpleSpringLoader();
        mPluginRegistry = new DefaultPluginRegistry();
        mPluginManager = new DefaultPluginManager(mSpringLoader, mPluginRegistry);
        mPluginManager.loadPlugins("org/devzendo/minimiser/gui/menu/pluginwithmenuprovidingfacade.properties");
        mApplicationPlugin = (MenuProvidingFacadeAppPlugin) mPluginManager.getApplicationPlugin();

        // Ensure ApplicationMenu is present but empty on database open
        mOpenDatabaseList = new OpenDatabaseList();
        mOpenDatabaseList.addDatabaseEventObserver(new ApplicationMenuCreatingDatabaseEventListener());

        mMenu = new StubMenu();

        mGlobalApplicationMenu = new ApplicationMenu();

        mStubProblemReporter = new StubProblemReporter();
    }

    /**
     *
     */
    @Test
    public void pluginImplementingMenuProvidingIsCorrectlyLoaded() {
        final List<Plugin> plugins = mPluginManager.getPlugins();
        Assert.assertEquals(1, plugins.size());
        final List<MenuProviding> menuProvidingPlugins =
            mPluginManager.getPluginsImplementingFacade(MenuProviding.class);
        Assert.assertEquals(1, menuProvidingPlugins.size());
    }

    /**
     *
     */
    @Test
    public void menuProvidingPluginIsInitialisedCorrectly() {
        final List<MenuProviding> menuProvidingPlugins =
            mPluginManager.getPluginsImplementingFacade(MenuProviding.class);
        final StubMenuProvidingFacade stubMenuProvidingFacade =
            (StubMenuProvidingFacade) menuProvidingPlugins.get(0).getMenuProvidingFacade();

        // Pass the ODL, Menu, global ApplicationMenu to the plugin
        new MenuProvidingFacadeInitialiser(
            mPluginManager, mOpenDatabaseList, mMenu,
            mGlobalApplicationMenu, mStubProblemReporter).initialise();

        // Did the plugin get them?
        Assert.assertSame(mGlobalApplicationMenu, stubMenuProvidingFacade.getGlobalApplicationMenu());
        Assert.assertTrue(mGlobalApplicationMenu.getCustomMenus().size() > 0);

        // The StubMenuProvidingFacade adds itself as a DatabaseEvent Observer, and
        // populates the database-specific ApplicationMenu when a database is opened.
        final DatabaseDescriptor databaseDescriptor = new DatabaseDescriptor(DATABASE);
        mOpenDatabaseList.addOpenedDatabase(databaseDescriptor);

        // A blank ApplicationMenu will have been set by the
        // ApplicationMenuCreatingDatabaseEventListener...
        final ApplicationMenu databaseApplicationMenu =
            (ApplicationMenu) databaseDescriptor.getAttribute(AttributeIdentifier.ApplicationMenu);
        Assert.assertNotNull(databaseApplicationMenu);
        // ...but the StubMenuProvidingFacade should have populated it.

        Assert.assertTrue(databaseApplicationMenu.getCustomMenus().size() > 0);
    }

    /**
     *
     */
    @Test
    public void menuProvidingFacadeInitialiseIsCalledOnTheEventThread() {
        final StubMenuProvidingFacade stubMenuProvidingFacade =
            (StubMenuProvidingFacade) mPluginManager.getPluginsImplementingFacade(
                MenuProviding.class).get(0).getMenuProvidingFacade();

        // Pass the ODL, Menu, global ApplicationMenu to the plugin
        new MenuProvidingFacadeInitialiser(
            mPluginManager, mOpenDatabaseList, mMenu,
            mGlobalApplicationMenu, mStubProblemReporter).initialise();

        Assert.assertTrue(stubMenuProvidingFacade.initialisedOnEventThread());
    }

    /**
     *
     */
    @Test
    public void menuProvidingThatBlowsUpLoadingFacadeGetsProblemReported() {
        mApplicationPlugin.injectFacadeLoadFailure();

        // Pass the ODL, Menu, global ApplicationMenu to the plugin
        new MenuProvidingFacadeInitialiser(
            mPluginManager, mOpenDatabaseList, mMenu,
            mGlobalApplicationMenu, mStubProblemReporter).initialise();

        Assert.assertEquals("while initialising the application menu", mStubProblemReporter.getDoing());
    }

    /**
     *
     */
    @Test
    public void menuProvidingFacadeThatBlowsUpInFacadeGetsProblemReported() {
        final StubMenuProvidingFacade stubMenuProvidingFacade =
            (StubMenuProvidingFacade) mPluginManager.getPluginsImplementingFacade(
                MenuProviding.class).get(0).getMenuProvidingFacade();
        stubMenuProvidingFacade.injectFailure(new ArrayIndexOutOfBoundsException("bad index into some array"));

        // Pass the ODL, Menu, global ApplicationMenu to the plugin
        new MenuProvidingFacadeInitialiser(
            mPluginManager, mOpenDatabaseList, mMenu,
            mGlobalApplicationMenu, mStubProblemReporter).initialise();

        Assert.assertEquals("while initialising the application menu", mStubProblemReporter.getDoing());
    }
    // TODO do I need tests that ensure that only the ApplicationPlugin can implement
    // MenuProviding?
}
