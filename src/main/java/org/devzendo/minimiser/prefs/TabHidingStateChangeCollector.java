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

import java.util.HashMap;
import java.util.Map;

/**
 * Collects changes to the state of hidden tabs. Used as a
 * delegate by the ChangeCollectingPrefs.
 * 
 * SMELL: If you're wondering about the odd style of the code: why
 * the throwing of IllegalStateException if a hidden tab state has
 * not been read first, before setting/clearing - it's because the
 * list of tabs is not known by this class. It's only known by
 * client code (e.g. the AdvancedTab). That code will read all the
 * tabs, and possibly set/clear them. If I knew the names of all
 * the tabs, I could pre-read them in the CTOR, and not need this
 * check.
 * 
 * @author matt
 *
 */
public final class TabHidingStateChangeCollector {
    private final Map<String, Boolean> hiddenTabs;
    private final Map<String, Boolean> readHiddenTabs;
    private final MiniMiserPrefs mPrefs;

    /**
     * @param realPrefs the prefs that store the hidden tab state
     */
    public TabHidingStateChangeCollector(final MiniMiserPrefs realPrefs) {
        mPrefs = realPrefs;
        hiddenTabs = new HashMap<String, Boolean>();
        readHiddenTabs = new HashMap<String, Boolean>();
    }

    /**
     * {@inheritDoc}
     */
    public void setTabHidden(final String tabName) {
        if (readHiddenTabs.containsKey(tabName)) {
            hiddenTabs.put(tabName, Boolean.TRUE);
        } else {
            throw new IllegalStateException("Hidden state of tab " + tabName
                    + " has not been read");
        }
    }

    /**
     * {@inheritDoc}
     */
    public void clearTabHidden(final String tabName) {
        if (readHiddenTabs.containsKey(tabName)) {
            hiddenTabs.put(tabName, Boolean.FALSE);
        } else {
            throw new IllegalStateException("Hidden state of tab " + tabName
                    + " has not been read");
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isTabHidden(final String tabName) {
        final Boolean hidden = mPrefs.isTabHidden(tabName);
        readHiddenTabs.put(tabName, hidden);
        return hidden;
    }

    /**
     * Commit any changes to the underlying Prefs
     */
    public void commit() {
        for (final String tabName : readHiddenTabs.keySet()) {
            final Boolean originalHidden = readHiddenTabs.get(tabName);
            if (hiddenTabs.containsKey(tabName)) {
                final Boolean hidden = hiddenTabs.get(tabName);
                if (hidden != originalHidden) {
                    if (hidden) {
                        mPrefs.setTabHidden(tabName);
                    } else {
                        mPrefs.clearTabHidden(tabName);
                    }
                }
            }
        }
    }

}
