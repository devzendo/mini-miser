package uk.me.gumbley.minimiser.wiring.lifecycle;

import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.prefs.PrefsEvent;

/**
 * Prefs that get/set the database list from memory.
 * @author matt
 *
 */
public final class StubOpenListPrefs implements Prefs {
    private String[] openFiles = new String[0];
    private String lastActiveFile = null;
    
    /**
     * {@inheritDoc}
     */
    public String getAbsolutePath() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getRecentFiles() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getWindowGeometry(final String windowName) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getWizardPanelSize() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void setRecentFiles(final String[] paths) {
    }

    /**
     * {@inheritDoc}
     */
    public void setWindowGeometry(final String windowName, final String geometry) {
    }

    /**
     * {@inheritDoc}
     */
    public void setWizardPanelSize(final String size) {
    }

    /**
     * {@inheritDoc}
     */
    public String[] getOpenFiles() {
        return openFiles;
    }

    /**
     * {@inheritDoc}
     */
    public void setOpenFiles(final String[] paths) {
        openFiles = paths == null ? new String[0] : paths;
        
    }

    /**
     * {@inheritDoc}
     */
    public String getLastActiveFile() {
        return lastActiveFile;
    }

    /**
     * {@inheritDoc}
     */
    public void setLastActiveFile(final String name) {
        lastActiveFile = name;
    }

    /**
     * {@inheritDoc}
     */
    public void clearLastActiveFile() {
        lastActiveFile = null;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getOpenTabs(final String databaseName) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void setOpenTabs(final String databaseName, final String[] tabNames) {
    }

    /**
     * {@inheritDoc}
     */
    public void clearTabHidden(final String tabName) {
        
    }

    /**
     * {@inheritDoc}
     */
    public boolean isTabHidden(final String tabName) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void setTabHidden(final String tabName) {
    }

    /**
     * {@inheritDoc}
     */
    public void addChangeListener(final Observer<PrefsEvent> observer) {
    }

    /**
     * {@inheritDoc}
     */
    public String getActiveTab(final String databaseName) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void setActiveTab(final String databaseName, final String tabName) {
    }

    /**
     * {@inheritDoc}
     */
    public String getCurrentSoftwareVersion() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void setCurrentSoftwareVersion(final String version) {
    }
}
