package uk.me.gumbley.minimiser.gui.lifecycle;

import uk.me.gumbley.minimiser.prefs.Prefs;

/**
 * Prefs that get/set the database list from memory.
 * @author matt
 *
 */
public class StubOpenListPrefs implements Prefs {
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

    public String[] getOpenFiles() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setOpenFiles(String[] paths) {
        // TODO Auto-generated method stub
        
    }
}
