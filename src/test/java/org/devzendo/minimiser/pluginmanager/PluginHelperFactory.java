package org.devzendo.minimiser.pluginmanager;

import org.devzendo.minimiser.MiniMiserApplicationContexts;
import org.devzendo.minimiser.springloader.SpringLoader;
import org.devzendo.minimiser.springloader.SpringLoaderFactory;

/**
 * A factory for PluginHelpers, allows creation of correctly-
 * initialised helpers, using dummy, or real PluginManager, with
 * or without a correctly-initialised SpringLoader.
 * 
 * @author matt
 *
 */
public final class PluginHelperFactory {

    /**
     * Create a PluginHelper using the DummyPluginManager
     * @return the PluginHelper
     */
    public static PluginHelper createPluginHelperWithDummyPluginManager() {
        final PluginManager pluginManager = new DummyAppPluginManager();
        return new PluginHelper(pluginManager);
    }

    /**
     * Create a PluginHelper using the DefaultPluginManager and no
     * SpringLoader.
     * @return the PluginHelper
     */
    public static PluginHelper createPluginHelper() {
        final PluginRegistry pluginRegistry = new DefaultPluginRegistry();
        final PluginManager pluginManager = new DefaultPluginManager(null, pluginRegistry);
        return new PluginHelper(pluginManager);
    }

    /**
     * Create a PluginHelper using the DefaultPluginManager and a
     * SpringLoader pre-loaded with the standard framework
     * application contexts.
     * @return the PluginHelper
     */
    public static PluginHelper createMiniMiserPluginHelper() {
        final SpringLoader springLoader = SpringLoaderFactory.initialise(MiniMiserApplicationContexts.getApplicationContexts());
        final PluginManager pluginManager = springLoader.getBean("pluginManager", PluginManager.class);
        return new PluginHelper(pluginManager);
    }
}
