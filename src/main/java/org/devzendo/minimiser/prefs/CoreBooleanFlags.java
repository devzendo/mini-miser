package org.devzendo.minimiser.prefs;

/**
 * The BooleanFlags used by the core application.
 * @author matt
 *
 */
public final class CoreBooleanFlags {
    private CoreBooleanFlags() {
        // no instances
    }
    /**
     * Is a check for update availability allowed?
     */
    public static final BooleanFlag UPDATE_CHECK_ALLOWED = new BooleanFlag("update_check_allowed");
}