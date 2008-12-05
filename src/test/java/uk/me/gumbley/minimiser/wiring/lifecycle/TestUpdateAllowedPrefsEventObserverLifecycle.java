package uk.me.gumbley.minimiser.wiring.lifecycle;

import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.commoncode.concurrency.ThreadUtils;
import uk.me.gumbley.minimiser.prefs.CoreBooleanFlags;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.prefs.TestPrefs;
import uk.me.gumbley.minimiser.updatechecker.StubUpdateChecker;
import uk.me.gumbley.minimiser.updatechecker.StubUpdateProgressAdapterFactory;
import uk.me.gumbley.minimiser.updatechecker.UpdateProgressAdapterFactory;


/**
 * Tests that setting the 'update check allowed' prefs flag triggers an update
 * check.
 * 
 * @author matt
 *
 */
public final class TestUpdateAllowedPrefsEventObserverLifecycle {
    private Prefs prefs;
    private File prefsFile;
    private StubUpdateChecker updateChecker;
    private UpdateAllowedPrefsEventObserverLifecycle observer;
    private UpdateProgressAdapterFactory updateProgressAdapterFactory;

    /**
     * @throws IOException on failure
     * 
     */
    @Before
    public void getPreRequisites() throws IOException {
        prefs = TestPrefs.createUnitTestPrefsFile();
        prefsFile = new File(prefs.getAbsolutePath());
        prefsFile.deleteOnExit();
        updateChecker = new StubUpdateChecker();
        updateProgressAdapterFactory = new StubUpdateProgressAdapterFactory();
        observer = new UpdateAllowedPrefsEventObserverLifecycle(updateChecker, prefs, updateProgressAdapterFactory);
    }
    
    /**
     * 
     */
    @Test(timeout = 3000)
    public void startedUpWithGoodConditionsTriggersUpdate() {
        observer.startup();
        
        prefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true); // I heard that...
        // asynchronously trigger....
        ThreadUtils.waitNoInterruption(250);
        
        Assert.assertTrue(updateChecker.updateTriggered());
    }
    
    /**
     * 
     */
    @Test(timeout = 3000)
    public void startedUpWithGoodConditionsButOtherSectionChangedDoesntTriggerUpdate() {
        observer.startup();
        
        prefs.clearTabHidden("notimportant"); // Will trigger an event... I heard that...
        // asynchronously trigger....
        ThreadUtils.waitNoInterruption(250);
        
        Assert.assertFalse(updateChecker.updateTriggered());
    }
    
    /**
     * 
     */
    @Test(timeout = 3000)
    public void notStartedUpWithGoodConditionsDoesntTriggerUpdate() {
        // do not startup!
     
        prefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true); // I heard that...
        // asynchronously trigger....
        ThreadUtils.waitNoInterruption(250);
        
        Assert.assertFalse(updateChecker.updateTriggered());
    }
    
    /**
     * 
     */
    @Test(timeout = 3000)
    public void startedUpWithBadConditionsDoesntTriggerUpdate() {
        observer.startup();
        
        prefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, false); // I heard that...
        // asynchronously trigger....
        ThreadUtils.waitNoInterruption(250);
        
        Assert.assertFalse(updateChecker.updateTriggered());
    }
    
    /**
     * 
     */
    @Test(timeout = 3000)
    public void notStartedUpWithBadConditionsDoesntTriggerUpdate() {
        // do not startup!
     
        prefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, false); // I heard that...
        // asynchronously trigger....
        ThreadUtils.waitNoInterruption(250);
        
        Assert.assertFalse(updateChecker.updateTriggered());
    }

    /**
     * 
     */
    @Test(timeout = 3000)
    public void startedUpWithNoPrefsChangeDoesntTriggerUpdate() {
        observer.startup();
        
        // asynchronously trigger....
        ThreadUtils.waitNoInterruption(250);
        
        Assert.assertFalse(updateChecker.updateTriggered());
    }
    
    /**
     * 
     */
    @Test(timeout = 3000)
    public void notStartedUpWithNoPrefsChangeDoesntTriggerUpdate() {
        // do not startup!
     
        // asynchronously trigger....
        ThreadUtils.waitNoInterruption(250);
        
        Assert.assertFalse(updateChecker.updateTriggered());
    }

}
