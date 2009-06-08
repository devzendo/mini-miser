package uk.me.gumbley.minimiser.wiring.lifecycle;

import org.junit.Assert;
import org.junit.Test;

import uk.me.gumbley.minimiser.gui.StubMainFrameTitle;
import uk.me.gumbley.minimiser.pluginmanager.DefaultPluginManager;
import uk.me.gumbley.minimiser.pluginmanager.DefaultPluginRegistry;
import uk.me.gumbley.minimiser.pluginmanager.PluginException;


/**
 * When the app plugin is loaded, does the title bar initialise?
 *  
 * @author matt
 *
 */
public final class TestMainFrameTitleInitialisingPluginLoadedObservingLifecycle {

    /**
     * @throws PluginException never
     */
    @Test
    public void loadingPluginsChangesTheTitle() throws PluginException {
        final DefaultPluginRegistry pluginRegistry = new DefaultPluginRegistry();
        final DefaultPluginManager pluginManager = new DefaultPluginManager(pluginRegistry);
        
        final StubMainFrameTitle mainFrameTitle = new StubMainFrameTitle();
        Assert.assertNull(mainFrameTitle.getApplicationName());
        
        final MainFrameTitleInitialisingPluginLoadedObservingLifecycle lifecycle =
            new MainFrameTitleInitialisingPluginLoadedObservingLifecycle(
                pluginManager, mainFrameTitle);
        Assert.assertNull(mainFrameTitle.getApplicationName()); // not yet, mungo!
        lifecycle.startup();
        
        pluginManager.loadPlugins("uk/me/gumbley/minimiser/pluginmanager/goodplugin.properties");
        
        Assert.assertEquals("Application", mainFrameTitle.getApplicationName());
    }
}
