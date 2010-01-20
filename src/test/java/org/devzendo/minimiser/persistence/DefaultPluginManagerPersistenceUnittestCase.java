package org.devzendo.minimiser.persistence;

import org.devzendo.minimiser.pluginmanager.PluginManager;
import org.devzendo.minimiser.pluginmanager.PluginRegistry;
import org.devzendo.minimiser.springloader.ApplicationContext;


/**
 * Persistent tests that need real plugins can subclass this
 * and define their plugins in their application context.
 * @author matt
 *
 */
@ApplicationContext("org/devzendo/minimiser/persistence/DefaultPluginManagerTestCase.xml")
public abstract class DefaultPluginManagerPersistenceUnittestCase extends
        PersistenceUnittestCase {
    /**
     * @return the plugin manager
     */
    public final PluginManager getPluginManager() {
        return getSpringLoader().getBean("pluginManager", PluginManager.class);
    }

    /**
     * @return the plugin registry
     */
    public final PluginRegistry getPluginRegistry() {
        return getSpringLoader().getBean("pluginRegistry", PluginRegistry.class);
    }
}
