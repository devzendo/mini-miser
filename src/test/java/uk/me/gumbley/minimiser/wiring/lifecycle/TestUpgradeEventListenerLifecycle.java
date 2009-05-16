package uk.me.gumbley.minimiser.wiring.lifecycle;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;
import uk.me.gumbley.minimiser.lifecycle.LifecycleManager;
import uk.me.gumbley.minimiser.pluginmanager.AppDetails;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.prefs.PrefsFactory;
import uk.me.gumbley.minimiser.prefs.TestPrefs;
import uk.me.gumbley.minimiser.springloader.ApplicationContext;
import uk.me.gumbley.minimiser.springloader.SpringLoaderUnittestCase;
import uk.me.gumbley.minimiser.upgradedetector.FreshInstallEvent;
import uk.me.gumbley.minimiser.upgradedetector.StubRecordingUpgradeListener;
import uk.me.gumbley.minimiser.upgradedetector.UpgradeEvent;

/**
 * Tests the UpgradeEventListenerManager that's controlled as a Lifecycle.
 * 
 * @author matt
 *
 */
@ApplicationContext("uk/me/gumbley/minimiser/wiring/lifecycle/UpgradeEventListenerLifecycleTestCase.xml")
public final class TestUpgradeEventListenerLifecycle extends SpringLoaderUnittestCase {

    private Prefs mPrefs;
    private File mPrefsFile;
    private LifecycleManager mLifecycleManager;
    private StubRecordingUpgradeListener mStubUpgradeListener;
    private AppDetails mAppDetails;

    /**
     * @throws IOException on prefs creation failure
     * 
     */
    @Before
    public void getPrerequisites() throws IOException {
        mPrefs = TestPrefs.createUnitTestPrefsFile();
        mPrefsFile = new File(mPrefs.getAbsolutePath());
        mPrefsFile.deleteOnExit();
        final PrefsFactory prefsFactory = getSpringLoader().getBean("&prefs", PrefsFactory.class);
        prefsFactory.setPrefs(mPrefs);

        mAppDetails = getSpringLoader().getBean("appDetails", AppDetails.class);
        mAppDetails.setApplicationName("foo");
        mAppDetails.setApplicationVersion("1.0.0-SNAPSHOT");
        
        mLifecycleManager = getSpringLoader().getBean("lifecycleManager", LifecycleManager.class);
        Assert.assertNotNull(mLifecycleManager);

        mStubUpgradeListener = getSpringLoader().getBean("stub", StubRecordingUpgradeListener.class);
        Assert.assertNotNull(mStubUpgradeListener);
    }

    /**
     * Not an exhaustive test of upgrade installations, we're just checking that
     * the wiring from lifecycle is good and triggers the detector/manager.
     */
    @Test
    public void eventsPropagatedAfterStartup() {
        Assert.assertNull(mStubUpgradeListener.getObservedEvent());
        
        mLifecycleManager.startup();
        
        final UpgradeEvent upgradeEvent = mStubUpgradeListener.getObservedEvent();
        Assert.assertNotNull(upgradeEvent);
        Assert.assertTrue(upgradeEvent instanceof FreshInstallEvent);
        final FreshInstallEvent freshInstallEvent = (FreshInstallEvent) upgradeEvent;
        final String runningVersion = freshInstallEvent.getRunningVersion();
        Assert.assertNotNull(runningVersion);
        Assert.assertEquals(mAppDetails.getApplicationVersion(), runningVersion);
    }
    
    /**
     * Use this test to check that all prefs that should be initialised to
     * non-defaults are done on a fresh install.
     */
    @Test
    public void prefsInitialisingFreshInstallListener() {
        // SQL tab should be hidden on fresh install
        Assert.assertFalse(mPrefs.isTabHidden(TabIdentifier.SQL.toString()));
        
        mLifecycleManager.startup();
        
        Assert.assertTrue(mPrefs.isTabHidden(TabIdentifier.SQL.toString()));
    }
}
