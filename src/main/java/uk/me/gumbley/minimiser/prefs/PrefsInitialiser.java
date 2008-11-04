package uk.me.gumbley.minimiser.prefs;

import org.apache.log4j.Logger;
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;

/**
 * Initisalise prefs to any non-default values. For example, a boolean flag
 * will be initialised to false, but that may not be what is wanted initially.
 * e.g. the "show SQL view" flag - this should be true (i.e. do not show) on
 * a fresh install.
 * <p>
 * This code is triggered by an UpgradeDetector that's wired into the
 * Lifecycle. It is triggered on a fresh installation.
 * 
 * @author matt
 *
 */
public final class PrefsInitialiser {
    private static final Logger LOGGER = Logger
            .getLogger(PrefsInitialiser.class);
    /**
     * No instances; this is a toolkit. 
     */
    private PrefsInitialiser() {
    }
    
    /**
     * Initialise the incoming prefs object to its non-default settings.
     * @param prefs a preferences object
     * @return the same object, modified.
     */
    public static Prefs initialisePrefs(final Prefs prefs) {
        LOGGER.info("Initialising non-default prefs");
        prefs.setTabHidden(TabIdentifier.SQL.toString());
        
        return prefs;
    }
}
