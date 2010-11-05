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

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.devzendo.commongui.GUIUtils;

/**
 * A helper class for starting up the Prefs location and factory.
 * @author matt
 *
 */
public final class PrefsStartupHelper {
    private static final Logger LOGGER = Logger
            .getLogger(PrefsStartupHelper.class);
    private final PrefsFactory mPrefsFactory;
    private final PrefsLocation mPrefsLocation;

    /**
     * Create the prefs startup helper
     * @param prefsLocation the location of the prefs
     * @param prefsFactory the factory where the prefs object will be stored
     */
    public PrefsStartupHelper(final PrefsLocation prefsLocation, final PrefsFactory prefsFactory) {
        mPrefsLocation = prefsLocation;
        mPrefsFactory = prefsFactory;
    }
    
    /**
     * Initialise the prefs. If this fails, the user wil be notified, and the
     * program will exit. You can't continue without prefs.
     */
    public void initialisePrefs() {
        LOGGER.debug("Prefs directory is " + mPrefsLocation.getPrefsDir().getAbsolutePath());
        LOGGER.debug("Prefs file is " + mPrefsLocation.getPrefsFile().getAbsolutePath());
        if (!mPrefsLocation.prefsDirectoryExists()) {
            LOGGER.info(String.format("Prefs directory %s does not exist - creating it",
                mPrefsLocation.getPrefsDir().getAbsolutePath()));
            if (!mPrefsLocation.createPrefsDirectory()) {
                LOGGER.warn("Failed to create prefs directory");
                GUIUtils.runOnEventThread(new Runnable() {
                    public void run() {
                        showPrefsDirCreationFailureMessage();
                    }
                });
            } else {
                LOGGER.info("Created prefs directory OK");
            }
        }
        mPrefsFactory.setPrefs(mPrefsLocation.getPrefsFile().getAbsolutePath());
    }

    private void showPrefsDirCreationFailureMessage() {
        JOptionPane.showMessageDialog(null, 
            // NOTE user-centric message
            // I18N
            String.format("The '%s' folder cannot be created - the application cannot continue.\n"
                + "This folder would be used to remember your options and settings.\n\n"
                + "Failure to create this folder may be be due to security permissions, or a full disk.",
                mPrefsLocation.getPrefsDir().getAbsolutePath()),
            "Could not create settings folder",
            JOptionPane.ERROR_MESSAGE);
        
        System.exit(0);
    }
}
