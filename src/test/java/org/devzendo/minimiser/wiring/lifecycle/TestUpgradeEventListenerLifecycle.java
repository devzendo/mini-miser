package org.devzendo.minimiser.wiring.lifecycle;

import java.io.File;
import java.io.IOException;

import org.devzendo.minimiser.gui.tab.TabIdentifier;
import org.devzendo.minimiser.lifecycle.LifecycleManager;
import org.devzendo.minimiser.pluginmanager.PluginRegistry;
import org.devzendo.minimiser.prefs.Prefs;
import org.devzendo.minimiser.prefs.PrefsFactory;
import org.devzendo.minimiser.prefs.TestPrefs;
import org.devzendo.minimiser.springloader.ApplicationContext;
import org.devzendo.minimiser.springloader.SpringLoaderUnittestCase;
import org.devzendo.minimiser.upgradedetector.FreshInstallEvent;
import org.devzendo.minimiser.upgradedetector.StubRecordingUpgradeListener;
import org.devzendo.minimiser.upgradedetector.UpgradeEvent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests the UpgradeEventListenerManager that's controlled as a Lifecycle.
 *
 * @author matt
 *
 */
@ApplicationContext("org/devzendo/minimiser/wiring/lifecycle/UpgradeEventListenerLifecycleTestCase.xml")
public final class TestUpgradeEventListenerLifecycle extends SpringLoaderUnittestCase {

    private Prefs mPrefs;
    private File mPrefsFile;
    private LifecycleManager mLifecycleManager;
    private StubRecordingUpgradeListener mStubUpgradeListener;
    private PluginRegistry mPluginRegistry;

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

        mPluginRegistry = getSpringLoader().getBean("pluginRegistry", PluginRegistry.class);

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
        Assert.assertEquals(mPluginRegistry.getApplicationVersion(), runningVersion);
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
