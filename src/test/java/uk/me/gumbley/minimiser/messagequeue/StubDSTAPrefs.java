package uk.me.gumbley.minimiser.messagequeue;

import java.util.HashSet;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.prefs.PrefsEvent;

/**
 * Prefs that get/set the "don't show this again" flags from memory.
 * @author matt
 *
 */
public final class StubDSTAPrefs implements Prefs {
    private HashSet<String> dstaFlags = new HashSet<String>();
    
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
        return new String[0];
    }

    /**
     * {@inheritDoc}
     */
    public void setOpenFiles(final String[] paths) {
    }

    /**
     * {@inheritDoc}
     */
    public String getLastActiveFile() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void setLastActiveFile(final String name) {
    }

    /**
     * {@inheritDoc}
     */
    public void clearLastActiveFile() {
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

    /**
     * {@inheritDoc}
     */
    public void clearAllDontShowThisAgainFlags() {
    }

    /**
     * {@inheritDoc}
     */
    public void clearDontShowThisAgain(final String messageId) {
        dstaFlags.remove(messageId);
    }

    /**
     * {@inheritDoc}
     */
    public boolean dontShowThisAgain(final String messageId) {
        return dstaFlags.contains(messageId);
    }

    /**
     * {@inheritDoc}
     */
    public void setDontShowThisAgain(final String messageId) {
        dstaFlags.add(messageId);
    }
}
