package uk.me.gumbley.minimiser.wiring.menu;

import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.gui.menu.Menu;
import uk.me.gumbley.minimiser.gui.menu.MenuWiringAdapter;
import uk.me.gumbley.minimiser.gui.menu.helpers.ViewMenuHelper;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.prefs.PrefsEvent;

/**
 * Adapts between the prefs change events and the menu updating the view
 * menu.
 * @author matt
 *
 */
public final class MenuPrefsEventObserver implements MenuWiringAdapter, Observer<PrefsEvent> {
    private static final Logger LOGGER = Logger
            .getLogger(MenuPrefsEventObserver.class);

    private final Menu menu;
    private final Prefs prefs;

    /**
     * Construct the adapter given other system objects for interaction.
     * @param leMenu the menu
     * @param lePrefs the prefs
     */
    public MenuPrefsEventObserver(
            final Menu leMenu,
            final Prefs lePrefs) {
                this.menu = leMenu;
                this.prefs = lePrefs;
    }

    /**
     * {@inheritDoc}
     */
    public void connectWiring() {
        // prefs -> menu
        prefs.addChangeListener(this);
    }
    
    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final PrefsEvent observableEvent) {
        LOGGER.debug("Prefs event: " + observableEvent.getPrefsSection());
        if (observableEvent.getPrefsSection() != Prefs.PrefsSection.HIDDEN_TABS) {
            return;
        }
        ViewMenuHelper.updateViewMenuFromPrefsHiddenTabs(prefs, menu);
    }
}
