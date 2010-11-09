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

package org.devzendo.minimiser.messagequeue;

import java.util.HashSet;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.prefs.BooleanFlag;
import org.devzendo.minimiser.prefs.MiniMiserPrefs;
import org.devzendo.minimiser.prefs.PrefsEvent;


/**
 * Prefs that get/set the "don't show this again" flags and
 * "upgrade check" flag from memory.
 * @author matt
 *
 */
public final class StubMessageQueuePrefs implements MiniMiserPrefs {
    private static final Logger LOGGER = Logger.getLogger(StubMessageQueuePrefs.class);
    private HashSet<String> dstaFlags = new HashSet<String>();
    private HashSet<BooleanFlag> setBooleanFlags = new HashSet<BooleanFlag>();
    
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
    public void clearDontShowThisAgainFlag(final String messageId) {
        LOGGER.debug("Clearing DSTA flag " + messageId);
        dstaFlags.remove(messageId);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDontShowThisAgainFlagSet(final String messageId) {
        final boolean contained = dstaFlags.contains(messageId);
        LOGGER.debug("Value of DSTA flag " + messageId + " is " + contained);
        return contained;
    }

    /**
     * {@inheritDoc}
     */
    public void setDontShowThisAgainFlag(final String messageId) {
        LOGGER.debug("Setting DSTA flag " + messageId);
        dstaFlags.add(messageId);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBooleanFlagSet(final BooleanFlag flagName) {
        return setBooleanFlags.contains(flagName);
    }

    /**
     * {@inheritDoc}
     */
    public void setBooleanFlag(final BooleanFlag flagName, final boolean allowed) {
        if (allowed) {
            setBooleanFlags.add(flagName);
        } else {
            setBooleanFlags.remove(flagName);
        }
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
