package uk.me.gumbley.minimiser.wiring.lifecycle;

import uk.me.gumbley.minimiser.gui.menu.Menu;
import uk.me.gumbley.minimiser.lifecycle.Lifecycle;
import uk.me.gumbley.minimiser.prefs.CoreBooleanFlags;
import uk.me.gumbley.minimiser.prefs.Prefs;

/**
 * A Lifecycle that sets the initial state of the Help | Check for
 * updtes menu item, according to whether the relevant flag in
 * prefs is set or not.
 * @author matt
 *
 */
public final class HelpCheckForUpdatesMenuInitialiserLifecycle implements Lifecycle {
    private final Menu mMenu;
    private final Prefs mPrefs;

    /**
     * Construct the initialiser
     * @param prefs the prefs to get the setting from on startup
     * @param menu the menu to initialise on startup
     */
    public HelpCheckForUpdatesMenuInitialiserLifecycle(
            final Prefs prefs,
            final Menu menu) {
        this.mPrefs = prefs;
        this.mMenu = menu;
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        final boolean enabled = mPrefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED);
        mMenu.setHelpCheckForUpdatesEnabled(enabled);
    }
}
