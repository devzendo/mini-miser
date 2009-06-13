package uk.me.gumbley.minimiser.pluginmanager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * The default implementation of the PluginRegistry.
 * 
 * @author matt
 *
 */
public final class DefaultPluginRegistry implements PluginRegistry {
    private final Set<PluginDescriptor> mPluginDescriptors;
    private ApplicationPluginDescriptor mApplicationPluginDescriptor;

    /**
     * Construct the default plugin registry
     */
    public DefaultPluginRegistry() {
        mPluginDescriptors = new HashSet<PluginDescriptor>();
    }

    /**
     * {@inheritDoc}
     */
    public ApplicationPluginDescriptor getApplicationPluginDescriptor() {
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
            if (mApplicationPluginDescriptor != null && !mApplicationPluginDescriptor.equals(pluginDescriptor)) {
                throw new IllegalStateException("Cannot add multiple application plugin descriptors");
            }
            mApplicationPluginDescriptor = (ApplicationPluginDescriptor) pluginDescriptor;
        }
        mPluginDescriptors.add(pluginDescriptor);
    }

    /**
     * {@inheritDoc}
     */
    public String getApplicationName() {
        if (mApplicationPluginDescriptor != null) {
            final String appName = mApplicationPluginDescriptor.getName();
            if (!StringUtils.isBlank(appName)) {
                return appName;
            }
        }
        return UNKNOWN_APPLICATION;
    }

    /**
     * {@inheritDoc}
     */
    public String getApplicationVersion() {
        if (mApplicationPluginDescriptor != null) {
            final String appVersion = mApplicationPluginDescriptor.getVersion();
            if (!StringUtils.isBlank(appVersion)) {
                return appVersion;
            }
        }
        return UNKNOWN_VERSION;
    }
}
