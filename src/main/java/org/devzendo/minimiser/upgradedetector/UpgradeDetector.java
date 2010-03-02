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

package org.devzendo.minimiser.upgradedetector;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.prefs.Prefs;

/**
 * The UpgradeDetector detects whether the software has just beed installed,
 * or upgraded, based on a property in the prefs that records the current
 * software version.
 * <p>
 * Once a valid install/upgrade detection has been made, the current version
 * can be stored in prefs, so that the detector doesn't fire again.
 * 
 * @author matt
 *
 */
public final class UpgradeDetector {
    private static final Logger LOGGER = Logger
            .getLogger(UpgradeDetector.class);
    
    private final Prefs preferences;

    /**
     * Construct the UpgradeDetector given prefs
     * @param prefs the preferences
     */
    public UpgradeDetector(final Prefs prefs) {
        this.preferences = prefs;
    }

    /**
     * Given the current runtime version of the software, has there been a
     * fresh installation or change in version? (Not necessarily an upgrade;
     * a downgrade is permissible - the actual meaning of the difference of
     * version numbers between current and stored is not checked)
     * @param currentVersion the currently running software version
     * @return true iff a fresh install or version change
     */
    public boolean upgraded(final String currentVersion) {
        if (currentVersion == null) {
            final String warning = "Can't detect upgrade if given null version";
            LOGGER.warn(warning);
            throw new IllegalArgumentException(warning);
        }
        LOGGER.info("Running software version is '" + currentVersion + "'");
        final String storedSoftwareVersion = preferences.getCurrentSoftwareVersion();
        if (storedSoftwareVersion == null) {
            LOGGER.info("This is a fresh installation");
            return true;
        }
        LOGGER.info("Stored software version is '" + storedSoftwareVersion + "'");
        final boolean storedAndCurrentDifferent = !storedSoftwareVersion.equalsIgnoreCase(currentVersion);
        LOGGER.info(storedAndCurrentDifferent ? "Difference in stored/running version detected" : "No change in software version");
        return storedAndCurrentDifferent;
    }
    
    /**
     * Given the current runtime version of the softwre, is this a fresh
     * installation, NOT an upgrade?
     * @return true iff this is a fresh install
     */
    public boolean freshInstall() {
        return getStoredVersion() == null;
    }

    /**
     * Store the current (runtime) software version, so that future checks
     * with the same version do not detect a change, and that checks for a
     * fresh installation do not indicate true.
     * @param currentVersion the currently running version
     */
    public void storeCurrentVersion(final String currentVersion) {
        if (currentVersion == null) {
            final String warning = "Can't store null current software version";
            LOGGER.warn(warning);
            throw new IllegalArgumentException(warning);
        }
        LOGGER.info("Storing currently running software version '" + currentVersion + "'");
        preferences.setCurrentSoftwareVersion(currentVersion);
    }

    /**
     * Get the currently stored software version, which may be null on a fresh
     * installation.
     * @return the currently stored software version, or null if fresh.
     */
    public String getStoredVersion() {
        final String storedSoftwareVersion = preferences.getCurrentSoftwareVersion();
        if (storedSoftwareVersion == null) {
            LOGGER.info("This is a fresh installation");
            return null;
        } else {
            LOGGER.info("Stored software version is '" + storedSoftwareVersion + "'");
            return storedSoftwareVersion;
        }
    }
}
