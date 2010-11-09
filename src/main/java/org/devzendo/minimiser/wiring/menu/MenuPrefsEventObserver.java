/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devzendo.minimiser.wiring.menu;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.gui.menu.Menu;
import org.devzendo.minimiser.gui.menu.MenuWiringAdapter;
import org.devzendo.minimiser.gui.menu.helpers.ViewMenuHelper;
import org.devzendo.minimiser.prefs.MiniMiserPrefs;
import org.devzendo.minimiser.prefs.PrefsEvent;


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
    private final MiniMiserPrefs prefs;

    /**
     * Construct the adapter given other system objects for interaction.
     * @param leMenu the menu
     * @param lePrefs the prefs
     */
    public MenuPrefsEventObserver(
            final Menu leMenu,
            final MiniMiserPrefs lePrefs) {
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
        if (observableEvent.getPrefsSection() != MiniMiserPrefs.PrefsSection.HIDDEN_TABS) {
            return;
        }
        ViewMenuHelper.updateViewMenuFromPrefsHiddenTabs(prefs, menu);
    }
}
