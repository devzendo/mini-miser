package uk.me.gumbley.minimiser.gui.menu.helpers;

import org.apache.log4j.Logger;
import uk.me.gumbley.minimiser.gui.menu.Menu;
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;
import uk.me.gumbley.minimiser.prefs.Prefs;

/**
 * A helper that sets the view menu items hidden/not hidden in response to
 * prefs change events, and upon startup.
 * 
 * @author matt
 *
 */
public final class ViewMenuUpdater {
    private static final Logger LOGGER = Logger.getLogger(ViewMenuUpdater.class);
    private ViewMenuUpdater() {
        // no instances
    }
    
    /**
     * Update the view menu with hidden tabs from prefs
     * @param prefs the prefs
     * @param menu the menu
     */
    public static void updateViewMenuFromPrefsHiddenTabs(final Prefs prefs, final Menu menu) {
        LOGGER.debug("Setting hidden tabs");
        for (final TabIdentifier tabId : TabIdentifier.values()) {
            final boolean tabHidden = prefs.isTabHidden(tabId.toString());
            menu.setTabHidden(tabId.toString(), tabHidden);
        }
        LOGGER.debug("Rebuilding view menu");
        menu.rebuildViewMenu();
    }

}
