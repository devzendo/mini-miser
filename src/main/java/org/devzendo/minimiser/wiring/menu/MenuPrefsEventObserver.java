package org.devzendo.minimiser.wiring.menu;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.gui.menu.Menu;
import org.devzendo.minimiser.gui.menu.MenuWiringAdapter;
import org.devzendo.minimiser.gui.menu.helpers.ViewMenuHelper;
import org.devzendo.minimiser.prefs.Prefs;
import org.devzendo.minimiser.prefs.PrefsEvent;

import uk.me.gumbley.commoncode.patterns.observer.Observer;

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
