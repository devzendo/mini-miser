package org.devzendo.minimiser.wiring.lifecycle;

import org.devzendo.minimiser.gui.menu.Menu;
import org.devzendo.minimiser.gui.menu.helpers.ViewMenuHelper;
import org.devzendo.minimiser.lifecycle.Lifecycle;
import org.devzendo.minimiser.prefs.Prefs;

/**
 * A Lifecycle that reads the current hidden tab settings from
 * prefs, and initialises the view menu correctly on startup.
 *
 * @author matt
 *
 */
public final class ViewMenuInitialiserLifecycle implements Lifecycle {
    private final Prefs prefs;
    private final Menu menu;

    /**
     * Construct using a menu and prefs
     * @param p the prefs
     * @param m the menu
     */
    public ViewMenuInitialiserLifecycle(final Prefs p, final Menu m) {
        prefs = p;
        menu = m;
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
        ViewMenuHelper.updateViewMenuFromPrefsHiddenTabs(prefs, menu);
    }
}
