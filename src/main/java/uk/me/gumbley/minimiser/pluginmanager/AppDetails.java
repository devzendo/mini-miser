package uk.me.gumbley.minimiser.pluginmanager;

/**
 * A singleton bean that is used everywhere to obtain the current
 * application name and version. Defaults to the name and version
 * of the framework until it is updated by the PluginManager
 * when the ApplicationPlugin has been loaded.
 *  
 * @author matt
 *
 */
public final class AppDetails {
    private static final String UNKNOWN = "unknown";
    
    private String mApplicationName = UNKNOWN;
    private String mApplicationVersion = UNKNOWN;
    
    /**
     * @param applicationName the applicationName to set
     */
    public synchronized void setApplicationName(final String applicationName) {
        if (applicationName == null || applicationName.length() == 0) {
            throw new IllegalArgumentException("Cannot set a null or empty application name");
        }
        mApplicationName = applicationName;
    }

    /**
     * @param applicationVersion the applicationVersion to set
     */
    public synchronized void setApplicationVersion(final String applicationVersion) {
        if (applicationVersion == null || applicationVersion.length() == 0) {
            throw new IllegalArgumentException("Cannot set a null or empty application version");
        }
        mApplicationVersion = applicationVersion;
    }

    /**
     * @return the application name, or "UNKNOWN" if not set.
     */
    public synchronized String getApplicationName() {
        return mApplicationName;
    }

    /**
     * @return the application version, or "UNKNOWN" if not set.
     */
    public synchronized String getApplicationVersion() {
        return mApplicationVersion;
    }
    
    /**
     * @return true iff the application version is set to
     * something
     */
    public synchronized boolean isApplicationVersionSet() {
        return !mApplicationVersion.equals(UNKNOWN);
    }
}
