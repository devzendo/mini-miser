package org.devzendo.minimiser.plugin.facade.providemenu;

import java.util.List;

import org.devzendo.minimiser.gui.tab.SystemTabIdentifiers;
import org.devzendo.minimiser.gui.tab.TabIdentifier;
import org.devzendo.minimiser.plugin.Plugin;
import org.devzendo.minimiser.pluginmanager.PluginException;
import org.devzendo.minimiser.pluginmanager.PluginManager;
import org.devzendo.minimiser.prefs.MiniMiserPrefs;
import org.easymock.EasyMock;
import org.junit.Assert;

/**
 * Helper routines common to the MenuProviding tests.
 *
 * @author matt
 *
 */
public class MenuProvidingHelper {

    private final PluginManager mPluginManager;

    /**
     * @param pluginManager the Plugin Manager
     *
     */
    public MenuProvidingHelper(final PluginManager pluginManager) {
        mPluginManager = pluginManager;
    }

    /**
     * @return a mock Prefs suitable for menu tests
     */
    public MiniMiserPrefs createMockPrefs() {
        final MiniMiserPrefs prefs = EasyMock.createMock(MiniMiserPrefs.class);
        for (final TabIdentifier systemTabIdentifier : SystemTabIdentifiers.values()) {
            EasyMock.expect(prefs.isTabHidden(systemTabIdentifier.getTabName())).andReturn(Boolean.FALSE);
        }
        EasyMock.expect(prefs.isTabHidden("APPLICATION")).andReturn(Boolean.FALSE);
        EasyMock.expect(prefs.isTabHidden("GLOBAL")).andReturn(Boolean.FALSE);
        EasyMock.replay(prefs);
        return prefs;
    }

    /**
     * Load the plugins (specifically the stub MenuProvidingAppPlugin).
     * @return the stub MenuProvidingAppPlugin
     * @throws PluginException on load failure
     */
    public MenuProvidingAppPlugin loadStubMenuProvidingPlugin() throws PluginException {
        mPluginManager.loadPlugins(
            "org/devzendo/minimiser/plugin/facade/providemenu/providemenuplugin.properties");
        return getCheckedMenuProvidingAppPlugin();
    }

    /**
     * The Plugins have been loaded, return the stub for tests.
     *
     * @return the stub MenuProvidingAppPlugin
     */
    private MenuProvidingAppPlugin getCheckedMenuProvidingAppPlugin() {
        final List<Plugin> plugins = mPluginManager.getPlugins();
        Assert.assertEquals(1, plugins.size());
        final MenuProvidingAppPlugin menuProvidingAppPlugin = (MenuProvidingAppPlugin) plugins.get(0);
        Assert.assertNotNull(menuProvidingAppPlugin);
        final List<MenuProviding> menuProvidingPlugins = mPluginManager
                .getPluginsImplementingFacade(MenuProviding.class);
        Assert.assertEquals(1, menuProvidingPlugins.size());
        return menuProvidingAppPlugin;
    }
}
