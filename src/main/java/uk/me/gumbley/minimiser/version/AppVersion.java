package uk.me.gumbley.minimiser.version;

/**
 * Obtain the verison of the app
 * 
 * @author matt
 */
public final class AppVersion {
    /**
     * Thous shalt not instantiate
     */
    private AppVersion() {
        // nothing
    }

    /**
     * @return the version of the app
     * @deprecated use AppDetails - getApplicationVersion instead
     */
    @Deprecated
    public static String getVersion() {
        return "1.0.0-SNAPSHOT";
    }
}
