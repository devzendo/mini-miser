package org.devzendo.minimiser.prefs;

import org.devzendo.commoncode.file.INIFile;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.commoncode.patterns.observer.ObserverList;

/**
 * Storage API for user preferences. Encapsulates the storage mechanism.
 * 
 * @author matt
 *
 */
public final class DefaultPrefsImpl implements Prefs {
    private String prefsFilePath;
    private INIFile iniFile;
    private final ObserverList<PrefsEvent> observerList;


    // The section names and preference items
    private static final String SECTION_UI = "ui";
    private static final String UI_GEOMETRY = "geometry";
    private static final String WIZARD_PANEL_SIZE = "wizard_panel_size";
    
    private static final String SECTION_WIZARD = "wizard";

    private static final String SECTION_RECENTFILES = "recentfiles";
    
    private static final String SECTION_OPENFILES = "openfiles";
    
    private static final String SECTION_LASTACTIVEFILE = "last_active_file";
    private static final String LAST_ACTIVE_FILE = "last";

    private static final String SECTION_OPENTABS_PREFIX = "opentabs_";

    private static final String SECTION_ACTIVETABS = "activetabs";

    private static final String SECTION_HIDDENTABS_PREFIX = "hiddentabs";

    private static final String SECTION_VERSIONS = "versions";
    private static final String CURRENT_SOFTWARE_VERSION = "application";
    
    private static final String SECTION_DONT_SHOW_THIS_AGAIN = "dsta";

    private static final String SECTION_BOOLEAN_FLAGS = "boolean_flags";

    private static final String SECTION_UPDATE_CHECKER = "update_checker";
    private static final String DATE_OF_UPDATE_AVAILABILITY_CHECK = "last_check_date";
    private static final String REMOTE_VERSION = "remote_version";

    /**
     * Create a Prefs object backed by a file
     * @param prefsFile the file path
     */
    public DefaultPrefsImpl(final String prefsFile) {
        prefsFilePath = prefsFile;
        iniFile = new INIFile(prefsFilePath);
        observerList = new ObserverList<PrefsEvent>();
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

    /**
     * {@inheritDoc}
     */
    public String[] getOpenTabs(final String databaseName) {
        return iniFile.getArray(SECTION_OPENTABS_PREFIX + databaseName);
    }

    /**
     * {@inheritDoc}
     */
    public void setOpenTabs(final String databaseName, final String[] tabNames) {
        iniFile.setArray(SECTION_OPENTABS_PREFIX + databaseName, tabNames);
    }


    /**
     * {@inheritDoc}
     */
    public String getActiveTab(final String databaseName) {
        return iniFile.getValue(SECTION_ACTIVETABS, databaseName);
    }

    /**
     * {@inheritDoc}
     */
    public void setActiveTab(final String databaseName, final String tabName) {
        iniFile.setValue(SECTION_ACTIVETABS, databaseName, tabName);
    }

    /**
     * {@inheritDoc}
     */
    public void clearTabHidden(final String tabName) {
        iniFile.removeValue(SECTION_HIDDENTABS_PREFIX, tabName);
        observerList.eventOccurred(new PrefsEvent(PrefsSection.HIDDEN_TABS));
    }

    /**
     * {@inheritDoc}
     */
    public boolean isTabHidden(final String tabName) {
        return iniFile.getBooleanValue(SECTION_HIDDENTABS_PREFIX, tabName);
    }

    /**
     * {@inheritDoc}
     */
    public void setTabHidden(final String tabName) {
        iniFile.setBooleanValue(SECTION_HIDDENTABS_PREFIX, tabName, true);
        observerList.eventOccurred(new PrefsEvent(PrefsSection.HIDDEN_TABS));
    }

    /**
     * {@inheritDoc}
     */
    public void addChangeListener(final Observer<PrefsEvent> observer) {
        observerList.addObserver(observer);
    }

    /**
     * {@inheritDoc}
     */
    public String getCurrentSoftwareVersion() {
        return iniFile.getValue(SECTION_VERSIONS, CURRENT_SOFTWARE_VERSION);
    }

    /**
     * {@inheritDoc}
     */
    public void setCurrentSoftwareVersion(final String version) {
        iniFile.setValue(SECTION_VERSIONS, CURRENT_SOFTWARE_VERSION, version);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDontShowThisAgainFlagSet(final String messageId) {
        return iniFile.getBooleanValue(SECTION_DONT_SHOW_THIS_AGAIN, messageId);
    }

    /**
     * {@inheritDoc}
     */
    public void setDontShowThisAgainFlag(final String messageId) {
        iniFile.setBooleanValue(SECTION_DONT_SHOW_THIS_AGAIN, messageId, true);
    }

    /**
     * {@inheritDoc}
     */
    public void clearDontShowThisAgainFlag(final String messageId) {
        iniFile.setBooleanValue(SECTION_DONT_SHOW_THIS_AGAIN, messageId, false);
    }

    /**
     * {@inheritDoc}
     */
    public void clearAllDontShowThisAgainFlags() {
        iniFile.removeSection(SECTION_DONT_SHOW_THIS_AGAIN);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBooleanFlagSet(final BooleanFlag flagName) {
        return iniFile.getBooleanValue(SECTION_BOOLEAN_FLAGS, flagName.getFlagName());
    }

    /**
     * {@inheritDoc}
     */
    public void setBooleanFlag(final BooleanFlag flagName, final boolean value) {
        iniFile.setBooleanValue(SECTION_BOOLEAN_FLAGS, flagName.getFlagName(), value);
        observerList.eventOccurred(new PrefsEvent(PrefsSection.BOOLEAN_FLAGS));
    }

    /**
     * {@inheritDoc}
     */
    public String getDateOfLastUpdateAvailableCheck() {
        return iniFile.getValue(SECTION_UPDATE_CHECKER, DATE_OF_UPDATE_AVAILABILITY_CHECK, "");
    }

    /**
     * {@inheritDoc}
     */
    public void setDateOfLastUpdateAvailableCheck(final String ukFormatDateString) {
        iniFile.setValue(SECTION_UPDATE_CHECKER, DATE_OF_UPDATE_AVAILABILITY_CHECK, ukFormatDateString);
    }

    /**
     * {@inheritDoc}
     */
    public String getLastRemoteUpdateVersion() {
        return iniFile.getValue(SECTION_UPDATE_CHECKER, REMOTE_VERSION, "");
    }

    /**
     * {@inheritDoc}
     */
    public void setLastRemoteUpdateVersion(final String version) {
        iniFile.setValue(SECTION_UPDATE_CHECKER, REMOTE_VERSION, version);
    }
}
