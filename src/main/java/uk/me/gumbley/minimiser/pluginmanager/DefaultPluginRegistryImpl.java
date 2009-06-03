package uk.me.gumbley.minimiser.pluginmanager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The default implementation of the PluginRegistry.
 * 
 * @author matt
 *
 */
public final class DefaultPluginRegistryImpl implements PluginRegistry {
    
    private final Set<PluginDescriptor> mPluginDescriptors;
    private PluginDescriptor mApplicationPluginDescriptor;

    /**
     * Construct the default plugin registry
     */
    public DefaultPluginRegistryImpl() {
        mPluginDescriptors = new HashSet<PluginDescriptor>();
    }

    /**
     * {@inheritDoc}
     */
    public PluginDescriptor getApplicationPluginDescriptor() {
        return mApplicationPluginDescriptor;
    }

    /**
     * {@inheritDoc}
     */
    public List<PluginDescriptor> getPluginDescriptors() {
        return new ArrayList<PluginDescriptor>(mPluginDescriptors);
    }

    /**
     * {@inheritDoc}
     */
    public void addPluginDescriptor(final PluginDescriptor pluginDescriptor) {
        if (pluginDescriptor.isApplication()) {
            mApplicationPluginDescriptor = pluginDescriptor;
        }
        mPluginDescriptors.add(pluginDescriptor);
    }
}
