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

import org.devzendo.commoncode.patterns.observer.Observer;

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
 * Tools->Options dialog are currently collected. All other calls will result in
 * an UnsupportedOperationException.
 * <p>
 * This class does not allow maintenance of ChangeListeners either.
 * 
 * @author matt
 * 
 */
public final class ChangeCollectingMiniMiserPrefs implements MiniMiserPrefs {
    private final TabHidingStateChangeCollector mTabHidingStateChangeCollector;

    /**
     * Construct a ChangeCollectingPrefs that's backed by a real Prefs
     * 
     * @param realPrefs
     *        the real Prefs that'll be updated when commit() is called.
     */
    public ChangeCollectingMiniMiserPrefs(final MiniMiserPrefs realPrefs) {
        mTabHidingStateChangeCollector = new TabHidingStateChangeCollector(realPrefs);
    }

    private void unsupported(final String methodName) {
        throw new UnsupportedOperationException(methodName
                + " is not supported");
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
        mTabHidingStateChangeCollector.setTabHidden(tabName);
    }

    /**
     * {@inheritDoc}
     */
    public void clearTabHidden(final String tabName) {
        mTabHidingStateChangeCollector.clearTabHidden(tabName);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isTabHidden(final String tabName) {
        return mTabHidingStateChangeCollector.isTabHidden(tabName);
    }

    /**
     * Commit any changes to the underlying Prefs
     */
    public void commit() {
        mTabHidingStateChangeCollector.commit();
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
    public boolean isBooleanFlagSet(final BooleanFlag flagName) {
        unsupported("isBooleanFlagSet");
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void setBooleanFlag(final BooleanFlag flagName, final boolean allowed) {
        unsupported("setBooleanFlag");
    }

    /**
     * {@inheritDoc}
     */
    public String getDateOfLastUpdateAvailableCheck() {
        unsupported("getDateOfLastUpdateAvailableCheck");
        return "";
    }

    /**
     * {@inheritDoc}
     */
    public void setDateOfLastUpdateAvailableCheck(
            final String ukFormatDateString) {
        unsupported("setDateOfLastUpdateAvailableCheck");
    }

    /**
     * {@inheritDoc}
     */
    public String getLastRemoteUpdateVersion() {
        unsupported("getLastRemoteUpdateVersion");
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void setLastRemoteUpdateVersion(final String version) {
        unsupported("setLastRemoteUpdateVersion");
    }
}
