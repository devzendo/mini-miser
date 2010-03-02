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

package org.devzendo.minimiser.updatechecker;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.logging.LoggingTestCase;
import org.devzendo.minimiser.messagequeue.Message;
import org.devzendo.minimiser.messagequeue.MessageQueue;
import org.devzendo.minimiser.messagequeue.MessageQueueBorderGuardFactory;
import org.devzendo.minimiser.pluginmanager.DummyAppPluginRegistry;
import org.devzendo.minimiser.pluginmanager.PluginRegistry;
import org.devzendo.minimiser.prefs.CoreBooleanFlags;
import org.devzendo.minimiser.prefs.Prefs;
import org.devzendo.minimiser.prefs.TestPrefs;
import org.devzendo.minimiser.util.Sleeper;
import org.devzendo.minimiser.util.StubToday;
import org.devzendo.minimiser.util.Today;
import org.devzendo.minimiser.util.WorkerPool;
import org.devzendo.minimiser.wiring.lifecycle.PeriodicUpdateCheckerLifecycle;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests that the Lifecycle that periodically checks for updates
 * being available actually triggers update checks.
 * <p>
 * Not testing the Lifecycleness here - manually starting it.
 * 
 * @author matt
 * 
 */
public final class TestPeriodicUpdateCheckerLifecycle extends LoggingTestCase {
    private static final Logger LOGGER = Logger
            .getLogger(TestPeriodicUpdateCheckerLifecycle.class);

    private static final String VERSION_1_0_0 = "1.0.0";
    private static final String VERSION_0_9_0 = "0.9.0";
    private static final String NOT_TODAYS_DATE = "22/02/1999";
    private static final String TODAYS_DATE = "08/12/2008";

    private Prefs mPrefs;
    private MessageQueue mMessageQueue;
    private UpdateChecker mUpdateChecker;
    private StubRemoteFileRetriever mRemoteFileRetriever;
    private NullChangeLogTransformer mChangeLogTransformer;
    private Today mToday;
    private WorkerPool mWorkerPool;
    private Sleeper mSleeper;
    private PluginRegistry mPluginRegistry;

    /**
     * @throws IOException on failure
     * 
     */
    @Before
    public void getPrerequisites() throws IOException {
        mPrefs = TestPrefs.createUnitTestPrefsFile();
        mMessageQueue = new MessageQueue(
            new MessageQueueBorderGuardFactory(mPrefs));
        mRemoteFileRetriever = new StubRemoteFileRetriever();
        mChangeLogTransformer = new NullChangeLogTransformer();
        mToday = new StubToday(TODAYS_DATE);
        mWorkerPool = new WorkerPool();
        mPluginRegistry = new DummyAppPluginRegistry("Foo", VERSION_1_0_0);
        mUpdateChecker = new DefaultUpdateChecker(mPrefs, mMessageQueue,
                mRemoteFileRetriever, mChangeLogTransformer, mToday,
                mWorkerPool, mPluginRegistry);
        mSleeper = new Sleeper(3600);
    }
    
    /**
     * 
     */
    @Test(timeout = 8000)
    public void updateCheckDisallowedDoesNotStartUpdate() {
        Assert.assertFalse(mPrefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED));
        
        final UpdateProgressAdapter adapter = EasyMock.createStrictMock(UpdateProgressAdapter.class);
        adapter.updateCheckDisallowed();
        adapter.finished();
        EasyMock.replay(adapter);

        final WaitForFinishUpdateProgressAdapterDecorator decoratedAdapter =
            new WaitForFinishUpdateProgressAdapterDecorator(adapter);
        final UpdateProgressAdapterFactory adapterFactory = new StubUpdateProgressAdapterFactory(decoratedAdapter);

        final PeriodicUpdateCheckerLifecycle periodicUpdateChecker =
            new PeriodicUpdateCheckerLifecycle(mUpdateChecker, mSleeper, adapterFactory);
        periodicUpdateChecker.startup();
        
        LOGGER.debug("sleeping");
        mSleeper.sleep(1000 * 60 * 61); // 61 minutes
        LOGGER.debug("woken up");
        decoratedAdapter.waitForFinished();

        EasyMock.verify(adapter);
    }

    /**
     * 
     */
    @Test(timeout = 8000)
    public void updateCheckAllowedStartsUpdate() {
        mPrefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true);
        mPrefs.setDateOfLastUpdateAvailableCheck(NOT_TODAYS_DATE);

        mPrefs.setLastRemoteUpdateVersion(VERSION_0_9_0);
        Assert.assertEquals(VERSION_0_9_0, mPrefs.getLastRemoteUpdateVersion());
    
        mRemoteFileRetriever.injectReturnedVersionNumber(VERSION_1_0_0);
        mRemoteFileRetriever.injectReturnedChangeLogContents("change log");
        
        final UpdateProgressAdapter adapter =
            EasyMock.createStrictMock(UpdateProgressAdapter.class);
        adapter.checkStarted();
        adapter.updateAvailable();
        adapter.finished();
        EasyMock.replay(adapter);

        final WaitForFinishUpdateProgressAdapterDecorator decoratedAdapter =
            new WaitForFinishUpdateProgressAdapterDecorator(adapter);
        
        final UpdateProgressAdapterFactory adapterFactory =
            new StubUpdateProgressAdapterFactory(decoratedAdapter);

        final PeriodicUpdateCheckerLifecycle periodicUpdateChecker =
            new PeriodicUpdateCheckerLifecycle(mUpdateChecker, mSleeper, adapterFactory);
        periodicUpdateChecker.startup();
        
        LOGGER.debug("sleeping");
        mSleeper.sleep(1000 * 60 * 61); // 61 minutes
        LOGGER.debug("woken up");
        decoratedAdapter.waitForFinished();

        EasyMock.verify(adapter);

        Assert.assertEquals(TODAYS_DATE, mPrefs.getDateOfLastUpdateAvailableCheck());
        Assert.assertEquals(VERSION_1_0_0, mPrefs.getLastRemoteUpdateVersion());
        Assert.assertEquals(1, mMessageQueue.size());
        final Message message = mMessageQueue.getMessageByIndex(0);
        Assert.assertEquals("change log", message.getMessageContent().toString());
    }
}
