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

package org.devzendo.minimiser.gui.menu.helpers;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.gui.menu.Menu;
import org.devzendo.minimiser.gui.tab.SystemTabIdentifiers;
import org.devzendo.minimiser.gui.tab.TabIdentifier;
import org.devzendo.minimiser.prefs.MiniMiserPrefs;


/**
 * A helper that:
 * <ol>
 * <li> sets the view menu items hidden/not hidden in response to
 * prefs change events, and upon startup.
 * </ol>
 *
 * @author matt
 *
 */
public final class ViewMenuHelper {
    private static final Logger LOGGER = Logger.getLogger(ViewMenuHelper.class);
    private ViewMenuHelper() {
        // no instances
    }

    /**
     * Update the view menu with hidden tabs from prefs
     * @param prefs the prefs
     * @param menu the menu
     */
    public static void updateViewMenuFromPrefsHiddenTabs(final MiniMiserPrefs prefs, final Menu menu) {
        LOGGER.debug("Setting hidden tabs");
        for (final TabIdentifier tabId : SystemTabIdentifiers.values()) {
            final boolean tabHidden = prefs.isTabHidden(tabId.getTabName());
            menu.setTabHidden(tabId.getTabName(), tabHidden);
        }
        LOGGER.debug("Rebuilding view menu");
        menu.rebuildViewMenu();
    }
}
