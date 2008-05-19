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
     */
    public static String getVersion() {
        // TODO get from pom
        return "1.0.0-SNAPSHOT";
    }
}
