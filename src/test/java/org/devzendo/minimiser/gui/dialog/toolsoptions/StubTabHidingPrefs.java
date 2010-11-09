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

package org.devzendo.minimiser.gui.dialog.toolsoptions;

import java.util.HashMap;
import java.util.Map;

import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.prefs.BooleanFlag;
import org.devzendo.minimiser.prefs.MiniMiserPrefs;
import org.devzendo.minimiser.prefs.PrefsEvent;


/**
 * Prefs that store the hidden state of tabs in memory.
 * @author matt
 *
 */
public final class StubTabHidingPrefs implements MiniMiserPrefs {
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
    public void clearDontShowThisAgainFlag(final String messageId) {
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDontShowThisAgainFlagSet(final String messageId) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void setDontShowThisAgainFlag(final String messageId) {
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBooleanFlagSet(final BooleanFlag flagName) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void setBooleanFlag(final BooleanFlag flagName, final boolean allowed) {
    }

    /**
     * {@inheritDoc}
     */
    public String getDateOfLastUpdateAvailableCheck() {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    public void setDateOfLastUpdateAvailableCheck(final String ukFormatDateString) {
    }

    /**
     * {@inheritDoc}
     */
    public String getLastRemoteUpdateVersion() {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    public void setLastRemoteUpdateVersion(final String version) {
    }
}
