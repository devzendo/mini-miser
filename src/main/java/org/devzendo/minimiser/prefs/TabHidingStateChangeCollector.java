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
    private final Prefs mPrefs;

    /**
     * @param realPrefs the prefs that store the hidden tab state
     */
    public TabHidingStateChangeCollector(final Prefs realPrefs) {
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
