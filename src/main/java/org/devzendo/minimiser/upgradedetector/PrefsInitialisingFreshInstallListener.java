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

package org.devzendo.minimiser.upgradedetector;

import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.prefs.MiniMiserPrefs;
import org.devzendo.minimiser.prefs.PrefsInitialiser;


/**
 * Performs initialisation of prefs, upon detection of a fresh installation.
 * 
 * @author matt
 *
 */
public final class PrefsInitialisingFreshInstallListener implements Observer<UpgradeEvent> {

    private final MiniMiserPrefs prefs;
    
    /**
     * Store the prefs for use if a fresh installation is detected.
     * @param preferences the prefs
     */
    public PrefsInitialisingFreshInstallListener(final MiniMiserPrefs preferences) {
        this.prefs = preferences;
    }

    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final UpgradeEvent observableEvent) {
        if (observableEvent instanceof FreshInstallEvent) {
            PrefsInitialiser.initialisePrefs(prefs);
        }
    }
}
