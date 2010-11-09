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

import org.devzendo.commongui.GUIUtils;
import org.devzendo.minimiser.gui.menu.Menu;
import org.devzendo.minimiser.lifecycle.Lifecycle;
import org.devzendo.minimiser.prefs.CoreBooleanFlags;
import org.devzendo.minimiser.prefs.MiniMiserPrefs;


/**
 * A Lifecycle that sets the initial state of the Help | Check for
 * updtes menu item, according to whether the relevant flag in
 * prefs is set or not.
 * @author matt
 *
 */
public final class HelpCheckForUpdatesMenuInitialiserLifecycle implements Lifecycle {
    private final Menu mMenu;
    private final MiniMiserPrefs mPrefs;

    /**
     * Construct the initialiser
     * @param prefs the prefs to get the setting from on startup
     * @param menu the menu to initialise on startup
     */
    public HelpCheckForUpdatesMenuInitialiserLifecycle(
            final MiniMiserPrefs prefs,
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
