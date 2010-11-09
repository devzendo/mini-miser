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

import org.devzendo.commoncode.patterns.observer.ObservableEvent;
import org.devzendo.minimiser.prefs.MiniMiserPrefs.PrefsSection;


/**
 * Prefs have changed, and this is the notification
 * you receive from the Prefs, if you're an observer of a change in a specific
 * section.
 * 
 * @author matt
 *
 */
public final class PrefsEvent implements ObservableEvent {
    private final PrefsSection prefsSection;

    /**
     * Create a PrefsEvent describing a change in a specific section.
     * @param section the section that has changed.
     */
    public PrefsEvent(final PrefsSection section) {
        prefsSection = section;
    }

    /**
     * Which section has changed in the Prefs?
     * @return the section that has changed.
     */
    public PrefsSection getPrefsSection() {
        return prefsSection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((prefsSection == null) ? 0 : prefsSection.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PrefsEvent other = (PrefsEvent) obj;
        if (prefsSection == null) {
            if (other.prefsSection != null) {
                return false;
            }
        } else if (!prefsSection.equals(other.prefsSection)) {
            return false;
        }
        return true;
    }
}
