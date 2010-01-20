package org.devzendo.minimiser.wiring.lifecycle;

import java.io.File;
import java.io.IOException;

import org.devzendo.minimiser.prefs.Prefs;
import org.devzendo.minimiser.prefs.TestPrefs;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.me.gumbley.commoncode.concurrency.ThreadUtils;


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
