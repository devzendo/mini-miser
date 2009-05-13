package uk.me.gumbley.minimiser.common;

/**
 * This is the name of the app, in case we need to change it.
 * 
 * @author matt
 */
public final class AppName {
    /**
     * Thou shalt not instantiate
     */
    private AppName() {
        // nothing
    }
    
    /**
     * @return the app name - now get this from the AppDetails
     * singleton
     * @deprecated
     */
    @Deprecated
    public static String getAppName() {
        // TODO i18n
        return "MiniMiser";
    }
}
