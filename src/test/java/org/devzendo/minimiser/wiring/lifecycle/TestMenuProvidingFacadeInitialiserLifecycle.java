package org.devzendo.minimiser.wiring.lifecycle;

import java.util.List;

import org.devzendo.minimiser.gui.dialog.problem.StubProblemReporter;
import org.devzendo.minimiser.gui.menu.ApplicationMenu;
import org.devzendo.minimiser.gui.menu.MenuProvidingFacadeInitialiser;
import org.devzendo.minimiser.gui.menu.StubMenu;
import org.devzendo.minimiser.gui.menu.StubMenuProvidingFacade;
import org.devzendo.minimiser.openlist.OpenDatabaseList;
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
 * The MenuProvidingFacadeInitialiserLifecycle does a basic test to check that a
 * MenuProvidingFacade has modified the ApplicationMenu.
 *
 * @author matt
 *
 */
public final class TestMenuProvidingFacadeInitialiserLifecycle {
    private SpringLoader mSpringLoader;
    private DefaultPluginRegistry mPluginRegistry;
    private DefaultPluginManager mPluginManager;
    private OpenDatabaseList mOpenDatabaseList;
    private StubMenu mMenu;
    private ApplicationMenu mGlobalApplicationMenu;
    private StubProblemReporter mStubProblemReporter;
    private MenuProvidingFacadeInitialiser mMenuProvidingFacadeInitialiser;

    /**
     * @throws PluginException never
     */
    @Before
    public void getPrerequisites() throws PluginException {
        mSpringLoader = new SimpleSpringLoader();
        mPluginRegistry = new DefaultPluginRegistry();
        mPluginManager = new DefaultPluginManager(mSpringLoader, mPluginRegistry);
        mPluginManager.loadPlugins("org/devzendo/minimiser/gui/menu/pluginwithmenuprovidingfacade.properties");

        // Ensure ApplicationMenu is present but empty on database open
        mOpenDatabaseList = new OpenDatabaseList();
        mOpenDatabaseList.addDatabaseEventObserver(new ApplicationMenuCreatingDatabaseEventListener());

        mMenu = new StubMenu();

        mGlobalApplicationMenu = new ApplicationMenu();

        mStubProblemReporter = new StubProblemReporter();

        mMenuProvidingFacadeInitialiser = new MenuProvidingFacadeInitialiser(
            mPluginManager, mOpenDatabaseList, mMenu,
            mGlobalApplicationMenu, mStubProblemReporter);

    }

    /**
     *
     */
    @Test
    public void globalApplicationMenuIsPopulatedAfterStartup() {
        final List<MenuProviding> menuProvidingPlugins =
            mPluginManager.getPluginsImplementingFacade(MenuProviding.class);
        final StubMenuProvidingFacade stubMenuProvidingFacade =
            (StubMenuProvidingFacade) menuProvidingPlugins.get(0).getMenuProvidingFacade();

        // Start lifecycle (manually)
        final MenuProvidingFacadeInitialiserLifecycle lifecycle =
            new MenuProvidingFacadeInitialiserLifecycle(mMenuProvidingFacadeInitialiser);
        lifecycle.startup();

        // Did the plugin get them?
        Assert.assertSame(mGlobalApplicationMenu, stubMenuProvidingFacade.getGlobalApplicationMenu());
        Assert.assertTrue(mGlobalApplicationMenu.getCustomMenus().size() > 0);
    }
}
