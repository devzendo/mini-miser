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

import java.io.File;
import java.io.IOException;

import org.devzendo.commoncode.concurrency.ThreadUtils;
import org.devzendo.minimiser.prefs.Prefs;
import org.devzendo.minimiser.prefs.TestPrefs;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



/**
 * Tests that the wizard page size details are computed and
 * cached on startup.
 * 
 * @author matt
 *
 */
public final class TestWizardPageInitialisingLifecycle {
    private Prefs prefs;
    private File prefsFile;
    private WizardPageInitialisingLifecycle mWizardPageInitialisingLifecycle;

    /**
     * @throws IOException on failure
     * 
     */
    @Before
    public void getPreRequisites() throws IOException {
        prefs = TestPrefs.createUnitTestPrefsFile();
        prefsFile = new File(prefs.getAbsolutePath());
        prefsFile.deleteOnExit();
        mWizardPageInitialisingLifecycle = new WizardPageInitialisingLifecycle(prefs);
    }
    
    /**
     * 
     */
    @Test(timeout = 5000)
    public void wizardDetailsStoredOnStartup() {
        Assert.assertEquals("", prefs.getWizardPanelSize());
        
        mWizardPageInitialisingLifecycle.startup();
        ThreadUtils.waitNoInterruption(1000);

        Assert.assertTrue(prefs.getWizardPanelSize().length() > 0);
    }
}
