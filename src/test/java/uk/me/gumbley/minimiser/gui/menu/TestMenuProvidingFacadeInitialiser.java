package uk.me.gumbley.minimiser.gui.menu;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.plugin.Plugin;
import uk.me.gumbley.minimiser.plugin.facade.providemenu.MenuProviding;
import uk.me.gumbley.minimiser.plugin.facade.providemenu.MenuProvidingFacade;
import uk.me.gumbley.minimiser.pluginmanager.DefaultPluginManager;
import uk.me.gumbley.minimiser.pluginmanager.DefaultPluginRegistry;
import uk.me.gumbley.minimiser.pluginmanager.PluginException;
import uk.me.gumbley.minimiser.springloader.SimpleSpringLoader;
import uk.me.gumbley.minimiser.springloader.SpringLoader;
import uk.me.gumbley.minimiser.wiring.databaseeventlistener.ApplicationMenuCreatingDatabaseEventListener;


/**
 * Tests that MenuProvidingFacades are correctly loaded and initialised.
 * @author matt
 *
 */
public final class TestMenuProvidingFacadeInitialiser {
    private SpringLoader mSpringLoader;
    private DefaultPluginRegistry mPluginRegistry;
    private DefaultPluginManager mPluginManager;
    private OpenDatabaseList mOpenDatabaseList;
    private StubMenu mMenu;
    private ApplicationMenu mGlobalApplicationMenu;

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
    }

    /**
     *
     */
    @Test
    public void pluginImplementingMenuProvidingIsCorrectlyLoaded() {
        final List<Plugin> plugins = mPluginManager.getPlugins();
        Assert.assertEquals(1, plugins.size());
        final List<MenuProviding> menuProvidingPlugins = mPluginManager.getPluginsImplementingFacade(MenuProviding.class);
        Assert.assertEquals(1, menuProvidingPlugins.size());
    }

    @Test
    public void menuProvidingPluginIsInitialisedCorrectly() {
        final List<MenuProviding> menuProvidingPlugins = mPluginManager.getPluginsImplementingFacade(MenuProviding.class);
        final MenuProviding menuProviding = menuProvidingPlugins.get(0);
        final MenuProvidingFacade menuProvidingFacade = menuProviding.getMenuProvidingFacade();
        final StubMenuProvidingFacade stubMenuProvidingFacade = (StubMenuProvidingFacade) menuProvidingFacade;

        // Pass the ODL, Menu, global ApplicationMenu to the plugin
        new MenuProvidingFacadeLoader(mPluginManager, mOpenDatabaseList, mMenu).initialise();

        Assert.assertSame(mGlobalApplicationMenu, stubMenuProvidingFacade.getGlobalApplicationMenu());
        // TODO the OLD now has a listener

        // WOZERE add a database
        // get database descriptor
        // it has a populated application menu

    }

    // TODO need tests that ensure that only the ApplicationPlugin can implement
    // MenuProviding?
}
