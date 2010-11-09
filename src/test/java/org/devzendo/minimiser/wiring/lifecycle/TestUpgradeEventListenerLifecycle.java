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

import org.devzendo.commonspring.springloader.ApplicationContext;
import org.devzendo.commonspring.springloader.SpringLoaderUnittestCase;
import org.devzendo.minimiser.gui.tab.SystemTabIdentifiers;
import org.devzendo.minimiser.lifecycle.LifecycleManager;
import org.devzendo.minimiser.pluginmanager.PluginRegistry;
import org.devzendo.minimiser.prefs.MiniMiserPrefs;
import org.devzendo.minimiser.prefs.PrefsFactory;
import org.devzendo.minimiser.prefs.TestPrefs;
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

    private MiniMiserPrefs mPrefs;
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
        Assert.assertFalse(mPrefs.isTabHidden(SystemTabIdentifiers.SQL.getTabName()));

        mLifecycleManager.startup();

        Assert.assertTrue(mPrefs.isTabHidden(SystemTabIdentifiers.SQL.getTabName()));
    }
}
