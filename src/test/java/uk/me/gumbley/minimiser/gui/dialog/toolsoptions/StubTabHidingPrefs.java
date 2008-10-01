package uk.me.gumbley.minimiser.gui.dialog.toolsoptions;

import java.util.HashMap;
import java.util.Map;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.prefs.PrefsEvent;

/**
 * Prefs that store the hidden state of tabs in memory.
 * @author matt
 *
 */
public final class StubTabHidingPrefs implements Prefs {
    private final Map<String, Boolean> tabHiddenFlags = new HashMap<String, Boolean>();

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
        return "";
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
        tabHiddenFlags.remove(tabName);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isTabHidden(final String tabName) {
        final Boolean hidden = tabHiddenFlags.get(tabName);
        return hidden != null && hidden.booleanValue();
    }

    /**
     * {@inheritDoc}
     */
    public void setTabHidden(final String tabName) {
        tabHiddenFlags.put(tabName, Boolean.TRUE);
    }

    /**
     * {@inheritDoc}
     */
    public void addChangeListener(final Observer<PrefsEvent> observer) {
    }
}
