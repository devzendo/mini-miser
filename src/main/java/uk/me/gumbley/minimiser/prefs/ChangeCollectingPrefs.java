package uk.me.gumbley.minimiser.prefs;

import java.util.HashMap;
import java.util.Map;
import uk.me.gumbley.commoncode.patterns.observer.Observer;

/**
 * A Prefs decorator that collects <b>some</b> changes to the prefs made by its
 * method calls, and only passes them through to the underlying Prefs when its
 * commit() method is called.
 * <p>
 * Used by the Tools->Options dialog's OK button handling, to commit the changes
 * to the Prefs. If Cancel is pressed, no changes are made to the underlying
 * Prefs.
 * <p>
 * Note that only those areas of the Prefs that are currently modifiable by the
 * Tools->Options dialog are currently collected. All other calls will result
 * in an UnsupportedOperationException.
 * <p>
 * This class does not allow maintenance of ChangeListeners either.
 *  
 * @author matt
 *
 */
public final class ChangeCollectingPrefs implements Prefs {
    private final Prefs prefs; // the decorated Prefs
    private final Map<String, Boolean> hiddenTabs;
    private final Map<String, Boolean> readHiddenTabs;
    

    /**
     * Construct a ChangeCollectingPrefs that's backed by a real Prefs
     * @param realPrefs the real Prefs that'll be updated when commit() is
     * called.
     */
    public ChangeCollectingPrefs(final Prefs realPrefs) {
        prefs = realPrefs;
        hiddenTabs = new HashMap<String, Boolean>();
        readHiddenTabs = new HashMap<String, Boolean>();
    }

    private void unsupported(final String methodName) {
        throw new UnsupportedOperationException(methodName + " is not supported");
    }
    
    /**
     * {@inheritDoc}
     */
    public void addChangeListener(final Observer<PrefsEvent> observer) {
        unsupported("addChangeListener");
    }

    /**
     * {@inheritDoc}
     */
    public void clearLastActiveFile() {
        unsupported("clearLastActiveFile");
    }
    /**
     * {@inheritDoc}
     */
    public String getAbsolutePath() {
        unsupported("getAbsolutePath");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getLastActiveFile() {
        unsupported("getLastActiveFile");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getOpenFiles() {
        unsupported("getOpenFiles");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getOpenTabs(final String databaseName) {
        unsupported("getOpenTabs");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getRecentFiles() {
        unsupported("getRecentFiles");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getWindowGeometry(final String windowName) {
        unsupported("getWindowGeometry");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getWizardPanelSize() {
        unsupported("getWizardPanelSize");
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setLastActiveFile(final String databaseName) {
        unsupported("setLastActiveFile");
    }

    /**
     * {@inheritDoc}
     */
    public void setOpenFiles(final String[] paths) {
        unsupported("setOpenFiles");
    }

    /**
     * {@inheritDoc}
     */
    public void setOpenTabs(final String databaseName, final String[] tabNames) {
        unsupported("setOpenTabs");
    }

    /**
     * {@inheritDoc}
     */
    public String getActiveTab(final String databaseName) {
        unsupported("getActiveTab");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void setActiveTab(final String databaseName, final String tabName) {
        unsupported("setActiveTab");
    }

    /**
     * {@inheritDoc}
     */
    public void setRecentFiles(final String[] paths) {
        unsupported("setRecentFiles");
    }

    /**
     * {@inheritDoc}
     */
    public void setWindowGeometry(final String windowName, final String geometry) {
        unsupported("setWindowGeometry");
    }

    /**
     * {@inheritDoc}
     */
    public void setWizardPanelSize(final String size) {
        unsupported("setWizardPanelSize");
    }

    /**
     * {@inheritDoc}
     */
    public void setTabHidden(final String tabName) {
        if (readHiddenTabs.containsKey(tabName)) {
            hiddenTabs.put(tabName, Boolean.TRUE);
        } else {
            throw new IllegalStateException("Hidden state of tab " + tabName + " has not been read");
        }
    }    
    
    /**
     * {@inheritDoc}
     */
    public void clearTabHidden(final String tabName) {
        if (readHiddenTabs.containsKey(tabName)) {
            hiddenTabs.put(tabName, Boolean.FALSE);
        } else {
            throw new IllegalStateException("Hidden state of tab " + tabName + " has not been read");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isTabHidden(final String tabName) {
        final Boolean hidden = prefs.isTabHidden(tabName);
        readHiddenTabs.put(tabName, hidden);
        return hidden;
    }

    /**
     * Commit any changes to the underlying Prefs
     */
    public void commit() {
        for (final String tabName : readHiddenTabs.keySet()) {
            final Boolean originalHidden = readHiddenTabs.get(tabName);
            final Boolean hidden = hiddenTabs.get(tabName);
            if (hidden != originalHidden) {
                if (hidden) {
                    prefs.setTabHidden(tabName);
                } else {
                    prefs.clearTabHidden(tabName);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getCurrentSoftwareVersion() {
        unsupported("getCurrentSoftwareVersion");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void setCurrentSoftwareVersion(final String version) {
        unsupported("setCurrentSoftwareVersion");
    }

    /**
     * {@inheritDoc}
     */
    public void clearAllDontShowThisAgainFlags() {
        unsupported("clearAllDontShowThisAgainFlags");
    }

    /**
     * {@inheritDoc}
     */
    public void clearDontShowThisAgainFlag(final String messageId) {
        unsupported("clearDontShowThisAgain");
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDontShowThisAgainFlagSet(final String messageId) {
        unsupported("dontShowThisAgain");
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void setDontShowThisAgainFlag(final String messageId) {
        unsupported("setDontShowThisAgain");
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUpdateAvailableCheckAllowed() {
        unsupported("isUpdateAvailableCheckAllowed");
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void setUpdateAvailableCheckAllowed(final boolean allowed) {
        unsupported("setUpdateAvailableCheckAllowed");
    }
}
