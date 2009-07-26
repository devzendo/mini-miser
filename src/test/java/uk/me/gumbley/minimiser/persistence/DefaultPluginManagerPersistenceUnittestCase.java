package uk.me.gumbley.minimiser.persistence;

import uk.me.gumbley.minimiser.pluginmanager.PluginManager;
import uk.me.gumbley.minimiser.pluginmanager.PluginRegistry;
import uk.me.gumbley.minimiser.springloader.ApplicationContext;

/**
 * Persistent tests that need real plugins can subclass this
 * and define their plugins in their application context.
 * @author matt
 *
 */
@ApplicationContext("uk/me/gumbley/minimiser/persistence/DefaultPluginManagerTestCase.xml")
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
