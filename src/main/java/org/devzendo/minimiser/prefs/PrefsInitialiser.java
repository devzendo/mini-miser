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

package org.devzendo.minimiser.prefs;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.gui.tab.SystemTabIdentifiers;

/**
 * Initisalise prefs to any non-default values. For example, a boolean flag
 * will be initialised to false, but that may not be what is wanted initially.
 * e.g. the "show SQL view" flag - this should be true (i.e. do not show) on
 * a fresh install.
 * <p>
 * This code is triggered by an UpgradeDetector that's wired into the
 * Lifecycle. It is triggered on a fresh installation.
 *
 * @author matt
 *
 */
public final class PrefsInitialiser {
    private static final Logger LOGGER = Logger
            .getLogger(PrefsInitialiser.class);
    /**
     * No instances; this is a toolkit.
     */
    private PrefsInitialiser() {
    }

    /**
     * Initialise the incoming prefs object to its non-default settings.
     * @param prefs a preferences object
     * @return the same object, modified.
     */
    public static MiniMiserPrefs initialisePrefs(final MiniMiserPrefs prefs) {
        LOGGER.info("Initialising non-default prefs");
        prefs.setTabHidden(SystemTabIdentifiers.SQL.getTabName());

        return prefs;
    }
}
