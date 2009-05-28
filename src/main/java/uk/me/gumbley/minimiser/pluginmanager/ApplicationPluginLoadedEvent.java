package uk.me.gumbley.minimiser.pluginmanager;


/**
 * The ApplicationPlugin has been loaded, and here are its details.
 * 
 * @author matt
 *
 */
public final class ApplicationPluginLoadedEvent extends PluginEvent {
    private final String mName;
    private final String mVersion;

    /**
     * @param name the application plugin name
     * @param version the application plugin version
     */
    public ApplicationPluginLoadedEvent(final String name, final String version) {
        mName = name;
        mVersion = version;
    }

    /**
     * @return the application plugin name
     */
    public String getName() {
        return mName;
    }

    /**
     * @return the application plugin version
     */
    public String getVersion() {
        return mVersion;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((mName == null) ? 0 : mName.hashCode());
        result = prime * result + ((mVersion == null) ? 0 : mVersion.hashCode());
        return result;
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
        if (!ApplicationPluginLoadedEvent.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final ApplicationPluginLoadedEvent other = (ApplicationPluginLoadedEvent) obj;
        if (mName == null) {
            if (other.mName != null) {
                return false;
            }
        } else if (!mName.equals(other.mName)) {
            return false;
        }
        if (mVersion == null) {
            if (other.mVersion != null) {
                return false;
            }
        } else if (!mVersion.equals(other.mVersion)) {
            return false;
        }
        return true;
    }
}
