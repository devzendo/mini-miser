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
import org.devzendo.minimiser.gui.menu.Menu;
import org.devzendo.minimiser.gui.menu.StubMenu;
import org.devzendo.minimiser.prefs.CoreBooleanFlags;
import org.devzendo.minimiser.prefs.MiniMiserPrefs;
import org.devzendo.minimiser.prefs.TestPrefs;
import org.devzendo.minimiser.updatechecker.StubUpdateChecker;
import org.devzendo.minimiser.updatechecker.UpdateProgressAdapter;
import org.devzendo.minimiser.updatechecker.UpdateProgressAdapterFactory;
import org.easymock.EasyMock;
import org.junit.After;
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
    private MiniMiserPrefs mPrefs;
    private File mPrefsFile;
    private StubUpdateChecker mUpdateChecker;
    private UpdateAllowedPrefsEventObserverLifecycle mObserver;
    private UpdateProgressAdapterFactory mUpdateProgressAdapterFactory;
    private Menu mMenu;

    /**
     * @throws IOException on failure
     *
     */
    @Before
    public void getPreRequisites() throws IOException {
        mPrefs = TestPrefs.createUnitTestPrefsFile();
        mPrefsFile = new File(mPrefs.getAbsolutePath());
        mPrefsFile.deleteOnExit();
        mUpdateChecker = new StubUpdateChecker();
        mMenu = new StubMenu();
        final UpdateProgressAdapter updateProgressAdapter = EasyMock.createMock(UpdateProgressAdapter.class);
        mUpdateProgressAdapterFactory = EasyMock.createMock(UpdateProgressAdapterFactory.class);
        EasyMock.expect(mUpdateProgressAdapterFactory.createBackgroundUpdateProgressAdapter()).andReturn(updateProgressAdapter).anyTimes();
        EasyMock.expect(mUpdateProgressAdapterFactory.createVisibleUpdateProgressAdapter()).andReturn(updateProgressAdapter).anyTimes();
        EasyMock.replay(updateProgressAdapter, mUpdateProgressAdapterFactory);
        mObserver = new UpdateAllowedPrefsEventObserverLifecycle(mUpdateChecker, mPrefs, mUpdateProgressAdapterFactory, mMenu);
    }
    
    /**
     * 
     */
    @After
    public void checkMocks() {
        EasyMock.verify(mUpdateProgressAdapterFactory);
    }
    
    /**
     *
     */
    @Test(timeout = 3000)
    public void startedUpWithGoodConditionsTriggersUpdate() {
        mObserver.startup();

        Assert.assertFalse(mMenu.isHelpCheckForUpdatesEnabled());
        mPrefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true); // I heard that...
        // asynchronously trigger....
        ThreadUtils.waitNoInterruption(250);

        Assert.assertTrue(mUpdateChecker.updateTriggered());
        Assert.assertTrue(mMenu.isHelpCheckForUpdatesEnabled());
    }

    /**
     *
     */
    @Test(timeout = 3000)
    public void startedUpWithGoodConditionsButOtherSectionChangedDoesntTriggerUpdate() {
        mObserver.startup();

        mPrefs.clearTabHidden("notimportant"); // Will trigger an event... I heard that...
        // asynchronously trigger....
        ThreadUtils.waitNoInterruption(250);

        Assert.assertFalse(mUpdateChecker.updateTriggered());
    }

    /**
     *
     */
    @Test// (timeout = 3000)
    public void notStartedUpWithGoodConditionsDoesntTriggerUpdate() {
        // do not startup!

        mPrefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true); // I heard that...
        // asynchronously trigger....
        ThreadUtils.waitNoInterruption(250);

        Assert.assertFalse(mUpdateChecker.updateTriggered());
    }

    /**
     *
     */
    @Test(timeout = 3000)
    public void startedUpWithBadConditionsDoesntTriggerUpdate() {
        mObserver.startup();

        mPrefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, false); // I heard that...
        // asynchronously trigger....
        ThreadUtils.waitNoInterruption(250);

        Assert.assertFalse(mUpdateChecker.updateTriggered());
    }

    /**
     *
     */
    @Test(timeout = 3000)
    public void notStartedUpWithBadConditionsDoesntTriggerUpdate() {
        // do not startup!

        Assert.assertFalse(mMenu.isHelpCheckForUpdatesEnabled());
        mPrefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, false); // I heard that...
        // asynchronously trigger....
        ThreadUtils.waitNoInterruption(250);

        Assert.assertFalse(mUpdateChecker.updateTriggered());
        Assert.assertFalse(mMenu.isHelpCheckForUpdatesEnabled());
    }

    /**
     *
     */
    @Test(timeout = 3000)
    public void startedUpWithNoPrefsChangeDoesntTriggerUpdate() {
        mObserver.startup();

        // asynchronously trigger....
        ThreadUtils.waitNoInterruption(250);

        Assert.assertFalse(mUpdateChecker.updateTriggered());
    }

    /**
     *
     */
    @Test(timeout = 3000)
    public void notStartedUpWithNoPrefsChangeDoesntTriggerUpdate() {
        // do not startup!

        // asynchronously trigger....
        ThreadUtils.waitNoInterruption(250);

        Assert.assertFalse(mUpdateChecker.updateTriggered());
    }
}
