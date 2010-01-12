package uk.me.gumbley.minimiser.wiring.lifecycle;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.me.gumbley.minimiser.gui.dialog.problem.StubProblemReporter;
import uk.me.gumbley.minimiser.gui.menu.ApplicationMenu;
import uk.me.gumbley.minimiser.gui.menu.MenuProvidingFacadeInitialiser;
import uk.me.gumbley.minimiser.gui.menu.StubMenu;
import uk.me.gumbley.minimiser.gui.menu.StubMenuProvidingFacade;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.plugin.facade.providemenu.MenuProviding;
import uk.me.gumbley.minimiser.pluginmanager.DefaultPluginManager;
import uk.me.gumbley.minimiser.pluginmanager.DefaultPluginRegistry;
import uk.me.gumbley.minimiser.pluginmanager.PluginException;
import uk.me.gumbley.minimiser.springloader.SimpleSpringLoader;
import uk.me.gumbley.minimiser.springloader.SpringLoader;
import uk.me.gumbley.minimiser.wiring.databaseeventlistener.ApplicationMenuCreatingDatabaseEventListener;


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
        mPluginManager.loadPlugins("uk/me/gumbley/minimiser/gui/menu/pluginwithmenuprovidingfacade.properties");

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
