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

package org.devzendo.minimiser.wiring.lifecycle;

import org.devzendo.commoncode.gui.GUIUtils;
import org.devzendo.minimiser.gui.wizard.MiniMiserWizardPage;
import org.devzendo.minimiser.lifecycle.Lifecycle;
import org.devzendo.minimiser.prefs.Prefs;


/**
 * Initialises the MiniMiserWizardPane toolkit. This needs to have
 * the left-hand graphic set, and the size used for the main
 * panel cached, as it's jarring to create the panel with a
 * filechooser every time, and have it resize to contain it.
 * 
 * @author matt
 *
 */
public final class WizardPageInitialisingLifecycle implements Lifecycle {

    private final Prefs mPrefs;

    /**
     * @param prefs the preferences
     */
    public WizardPageInitialisingLifecycle(final Prefs prefs) {
        mPrefs = prefs;
    }
    
    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        // nothing
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                MiniMiserWizardPage.setLHGraphic();
                MiniMiserWizardPage.getPanelDimension(mPrefs);
            }
        });
    }
}
