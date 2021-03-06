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

package org.devzendo.minimiser.wiring.lifecycle;

import org.devzendo.commonapp.lifecycle.Lifecycle;
import org.devzendo.minimiser.gui.menu.Menu;
import org.devzendo.minimiser.gui.menu.helpers.ViewMenuHelper;
import org.devzendo.minimiser.prefs.MiniMiserPrefs;

/**
 * A Lifecycle that reads the current hidden tab settings from
 * prefs, and initialises the view menu correctly on startup.
 *
 * @author matt
 *
 */
public final class ViewMenuInitialiserLifecycle implements Lifecycle {
    private final MiniMiserPrefs prefs;
    private final Menu menu;

    /**
     * Construct using a menu and prefs
     * @param p the prefs
     * @param m the menu
     */
    public ViewMenuInitialiserLifecycle(final MiniMiserPrefs p, final Menu m) {
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
