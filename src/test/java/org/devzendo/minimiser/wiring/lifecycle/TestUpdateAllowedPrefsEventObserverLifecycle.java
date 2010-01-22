package org.devzendo.minimiser.wiring.lifecycle;

import java.io.File;
import java.io.IOException;

import org.devzendo.commoncode.concurrency.ThreadUtils;
import org.devzendo.minimiser.gui.menu.Menu;
import org.devzendo.minimiser.gui.menu.StubMenu;
import org.devzendo.minimiser.prefs.CoreBooleanFlags;
import org.devzendo.minimiser.prefs.Prefs;
import org.devzendo.minimiser.prefs.TestPrefs;
import org.devzendo.minimiser.updatechecker.StubUpdateChecker;
import org.devzendo.minimiser.updatechecker.StubUpdateProgressAdapter;
import org.devzendo.minimiser.updatechecker.StubUpdateProgressAdapterFactory;
import org.devzendo.minimiser.updatechecker.UpdateProgressAdapterFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



/**
 * Tests that setting the 'update check allowed' prefs flag
 * triggers an update check.
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
    private Menu mMenu;

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
        mMenu = new StubMenu();
        updateProgressAdapterFactory = new StubUpdateProgressAdapterFactory(
            new StubUpdateProgressAdapter());
        observer = new UpdateAllowedPrefsEventObserverLifecycle(updateChecker, prefs, updateProgressAdapterFactory, mMenu);
    }

    /**
     *
     */
    @Test(timeout = 3000)
    public void startedUpWithGoodConditionsTriggersUpdate() {
        observer.startup();

        Assert.assertFalse(mMenu.isHelpCheckForUpdatesEnabled());
        prefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true); // I heard that...
        // asynchronously trigger....
        ThreadUtils.waitNoInterruption(250);

        Assert.assertTrue(updateChecker.updateTriggered());
        Assert.assertTrue(mMenu.isHelpCheckForUpdatesEnabled());
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
    @Test// (timeout = 3000)
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

        Assert.assertFalse(mMenu.isHelpCheckForUpdatesEnabled());
        prefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, false); // I heard that...
        // asynchronously trigger....
        ThreadUtils.waitNoInterruption(250);

        Assert.assertFalse(updateChecker.updateTriggered());
        Assert.assertFalse(mMenu.isHelpCheckForUpdatesEnabled());
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
