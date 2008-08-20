package uk.me.gumbley.minimiser.gui.lifecycle;

import uk.me.gumbley.minimiser.prefs.Prefs;

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
}
