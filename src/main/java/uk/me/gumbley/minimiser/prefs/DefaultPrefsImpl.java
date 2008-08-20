package uk.me.gumbley.minimiser.prefs;

import uk.me.gumbley.commoncode.file.INIFile;

/**
 * Storage API for user preferences. Encapsulates the storage mechanism.
 * 
 * @author matt
 *
 */
public final class DefaultPrefsImpl implements Prefs {
    private String prefsFilePath;
    private INIFile iniFile;

    // The section names and preference items
    private static final String SECTION_UI = "ui";
    private static final String UI_GEOMETRY = "geometry";
    private static final String WIZARD_PANEL_SIZE = "wizard_panel_size";
    
    private static final String SECTION_WIZARD = "wizard";

    private static final String SECTION_RECENTFILES = "recentfiles";
    
    private static final String SECTION_OPENFILES = "openfiles";
    
    private static final String SECTION_LASTACTIVEFILE = "last_active_file";
    private static final String LAST_ACTIVE_FILE = "last";

    /**
     * Create a Prefs object backed by a file
     * @param prefsFile the file path
     */
    public DefaultPrefsImpl(final String prefsFile) {
        prefsFilePath = prefsFile;
        iniFile = new INIFile(prefsFilePath);
    }
    
    /**
     * {@inheritDoc}
     */
    public String getWindowGeometry(final String windowName) {
        return iniFile.getValue(SECTION_UI, formWindowGeometryKey(windowName), "");
    }

    private String formWindowGeometryKey(final String windowName) {
        return UI_GEOMETRY + "_" + windowName;
    }

    /**
     * {@inheritDoc}
     */
    public void setWindowGeometry(final String windowName, final String geometry) {
        iniFile.setValue(SECTION_UI, formWindowGeometryKey(windowName), geometry);
    }

    /**
     * {@inheritDoc}
     */
    public String getAbsolutePath() {
        return prefsFilePath;
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return prefsFilePath;
    }

    /**
     * {@inheritDoc}
     */
    public String getWizardPanelSize() {
        return iniFile.getValue(SECTION_WIZARD, WIZARD_PANEL_SIZE, "");
    }

    /**
     * {@inheritDoc}
     */
    public void setWizardPanelSize(final String size) {
        iniFile.setValue(SECTION_WIZARD, WIZARD_PANEL_SIZE, size);
    }

    /**
     * {@inheritDoc}
     */
    public String[] getRecentFiles() {
        return iniFile.getArray(SECTION_RECENTFILES);
    }

    /**
     * {@inheritDoc}
     */
    public void setRecentFiles(final String[] paths) {
        iniFile.setArray(SECTION_RECENTFILES, paths);
    }

    /**
     * {@inheritDoc}
     */
    public String[] getOpenFiles() {
        return iniFile.getArray(SECTION_OPENFILES);
    }

    /**
     * {@inheritDoc}
     */
    public void setOpenFiles(final String[] paths) {
        iniFile.setArray(SECTION_OPENFILES, paths);
    }

    /**
     * {@inheritDoc}
     */
    public String getLastActiveFile() {
        return iniFile.getValue(SECTION_LASTACTIVEFILE, LAST_ACTIVE_FILE);
    }

    /**
     * {@inheritDoc}
     */
    public void setLastActiveFile(final String name) {
        iniFile.setValue(SECTION_LASTACTIVEFILE, LAST_ACTIVE_FILE, name);
    }

    /**
     * {@inheritDoc}
     */
    public void clearLastActiveFile() {
        iniFile.removeValue(SECTION_LASTACTIVEFILE, LAST_ACTIVE_FILE);
    }
}
