package uk.me.gumbley.minimiser;


/**
 * Holds the full set of standard application context files.
 * 
 * @author matt
 *
 */
public final class MiniMiserApplicationContexts {
    /**
     * No instances
     */
    private MiniMiserApplicationContexts() {
        // nothing
    }
    
    /**
     * @return an array of standard application contexts used by
     * the framework.
     */
    public static String[] getApplicationContexts() {
        return new String[] {
                "uk/me/gumbley/minimiser/MiniMiser.xml",
                "uk/me/gumbley/minimiser/Menu.xml"
        };
    }
}
