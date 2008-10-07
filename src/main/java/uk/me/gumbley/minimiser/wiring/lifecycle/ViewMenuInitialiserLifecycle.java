package uk.me.gumbley.minimiser.wiring.lifecycle;

import uk.me.gumbley.minimiser.gui.menu.Menu;
import uk.me.gumbley.minimiser.gui.menu.helpers.ViewMenuHelper;
import uk.me.gumbley.minimiser.lifecycle.Lifecycle;
import uk.me.gumbley.minimiser.prefs.Prefs;

/**
 * A Lifecycle that reads the current hidden tab settings from prefs, and
 * initialises the view menu correctly on sartup.
 * 
 * @author matt
 *
 */
public final class ViewMenuInitialiserLifecycle implements Lifecycle {
    private Prefs prefs;
    private Menu menu;

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
