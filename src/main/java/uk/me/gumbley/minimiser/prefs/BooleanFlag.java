package uk.me.gumbley.minimiser.prefs;

/**
 * BooleanFlags are used to name the generalised Boolean Flag storage available
 * through Prefs.
 * <p>
 * They're effectively type-safe Strings. This is not an enum since other
 * plugins will probably want to contribute to the flags.
 * 
 * @author matt
 *
 */
public class BooleanFlag {
    private String flagName;
    
    /**
     * Construct a BooleanFlag given its name
     * @param name the name, which will be used as a key into the appropriate
     * section in Prefs
     */
    BooleanFlag(final String name) {
        flagName = name;
    }
    
    /**
     * @return the Boolean Flag's name
     */
    public final String getFlagName() {
        return flagName;
    }
    
    /**
     * {@inheritDoc}
     */
    public final String toString() {
        return flagName;
    }
}
