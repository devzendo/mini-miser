package org.devzendo.minimiser;


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
                "org/devzendo/minimiser/MiniMiser.xml",
                "org/devzendo/minimiser/Menu.xml"
        };
    }
}
