package org.devzendo.minimiser.pluginmanager;



/**
 * The ApplicationPlugin has been loaded, and here are its details.
 * 
 * @author matt
 *
 */
public final class ApplicationPluginLoadedEvent extends PluginEvent {
    private final PluginDescriptor mPluginDescriptor;

    /**
     * @param pluginDescriptor the plugin descriptor of the application plugin
     */
    public ApplicationPluginLoadedEvent(final PluginDescriptor pluginDescriptor) {
        mPluginDescriptor = pluginDescriptor;
    }

    /**
     * @return the pluginDescriptor
     */
    public PluginDescriptor getPluginDescriptor() {
        return mPluginDescriptor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return mPluginDescriptor.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final ApplicationPluginLoadedEvent other = (ApplicationPluginLoadedEvent) obj;
        if (mPluginDescriptor == null) {
            if (other.mPluginDescriptor != null) {
                return false;
            }
        } else if (!mPluginDescriptor.equals(other.mPluginDescriptor)) {
            return false;
        }
        return true;
    }
}
