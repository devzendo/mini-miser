package org.devzendo.minimiser.wiring.lifecycle;

import org.devzendo.minimiser.gui.menu.Menu;
import org.devzendo.minimiser.lifecycle.Lifecycle;
import org.devzendo.minimiser.prefs.CoreBooleanFlags;
import org.devzendo.minimiser.prefs.Prefs;

import uk.me.gumbley.commoncode.gui.GUIUtils;

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
        GUIUtils.invokeLaterOnEventThread(new Runnable() {
            public void run() {
                mMenu.setHelpCheckForUpdatesEnabled(enabled);
            }
        });
    }
}
