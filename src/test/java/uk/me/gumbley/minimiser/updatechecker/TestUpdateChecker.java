package uk.me.gumbley.minimiser.updatechecker;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.messagequeue.Message;
import uk.me.gumbley.minimiser.messagequeue.MessageQueue;
import uk.me.gumbley.minimiser.messagequeue.MessageQueueBorderGuardFactory;
import uk.me.gumbley.minimiser.pluginmanager.ApplicationPluginDescriptor;
import uk.me.gumbley.minimiser.pluginmanager.DefaultPluginRegistry;
import uk.me.gumbley.minimiser.pluginmanager.DummyAppPluginRegistry;
import uk.me.gumbley.minimiser.pluginmanager.PluginRegistry;
import uk.me.gumbley.minimiser.prefs.CoreBooleanFlags;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.prefs.TestPrefs;
import uk.me.gumbley.minimiser.util.StubToday;
import uk.me.gumbley.minimiser.util.Today;
import uk.me.gumbley.minimiser.util.WorkerPool;


/**
 * Tests that the update checker only fires when allowed to, and that it
 * performs correctly. 
 * @author matt
 *
 */
public final class TestUpdateChecker extends LoggingTestCase {
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
    
    /**
     * @throws IOException on failure
     * 
     */
    @Before
    public void getPrerequisites() throws IOException {
        mPrefs = TestPrefs.createUnitTestPrefsFile();
        mMessageQueue = new MessageQueue(new MessageQueueBorderGuardFactory(mPrefs));
        mRemoteFileRetriever = new StubRemoteFileRetriever();
        mChangeLogTransformer = new NullChangeLogTransformer();
        mToday = new StubToday(TODAYS_DATE);
        mWorkerPool = new WorkerPool();
    }


    private void createUpdateCheckerWithAppDetails(final PluginRegistry pluginRegistry) {
        mUpdateChecker = new DefaultUpdateChecker(mPrefs,
            mMessageQueue, mRemoteFileRetriever,
            mChangeLogTransformer, mToday, mWorkerPool,
            pluginRegistry);
    }

    private void createUpdateChecker() {
        //final PluginRegistry pluginRegistry = new DummyAppPluginRegistry("Foo", "1.0.0-SNAPSHOT");
        final PluginRegistry pluginRegistry = new DefaultPluginRegistry();
        pluginRegistry.addPluginDescriptor(
            new ApplicationPluginDescriptor(
                true, "Foo", "1.0.0-SNAPSHOT", "1.0", 
                "http://localhost/", "", "", "", ""));
        createUpdateCheckerWithAppDetails(pluginRegistry);
    }
    
    private void startUpdateAndWait(final UpdateProgressAdapter adapter) {
        final WaitForFinishUpdateProgressAdapterDecorator decoratedAdapter =
            new WaitForFinishUpdateProgressAdapterDecorator(adapter);
        mUpdateChecker.triggerUpdateCheck(decoratedAdapter);
        decoratedAdapter.waitForFinished();
    }
        
    /**
     * 
     */
    @Test(timeout = 3000)
    public void updateCheckDisallowedDoesNotStartUpdate() {
        createUpdateChecker();
        Assert.assertFalse(mPrefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED));
        
        final UpdateProgressAdapter adapter = EasyMock.createStrictMock(UpdateProgressAdapter.class);
        adapter.updateCheckDisallowed();
        adapter.finished();
        EasyMock.replay(adapter);

        startUpdateAndWait(adapter);
        
        EasyMock.verify(adapter);
    }
    
    /**
     * 
     */
    @Test(timeout = 3000)
    public void onlyDoCheckOncePerDay() {
        createUpdateChecker();
        mPrefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true);
        Assert.assertTrue(mPrefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED));
        mPrefs.setDateOfLastUpdateAvailableCheck(TODAYS_DATE);
        Assert.assertEquals(TODAYS_DATE, mPrefs.getDateOfLastUpdateAvailableCheck());

        final UpdateProgressAdapter adapter = EasyMock.createStrictMock(UpdateProgressAdapter.class);
        adapter.alreadyCheckedToday();
        adapter.finished();
        EasyMock.replay(adapter);

        startUpdateAndWait(adapter);
        
        EasyMock.verify(adapter);
        Assert.assertEquals(0, mMessageQueue.size());
    }
    
    /**
     * 
     */
    @Test(timeout = 3000)
    public void doCheckIfNoPreviousCheckDateStored() {
        createUpdateChecker();
        mPrefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true);
        Assert.assertTrue(mPrefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED));
        Assert.assertEquals("", mPrefs.getDateOfLastUpdateAvailableCheck());

        mRemoteFileRetriever.injectCommsFailureOnVersionNumberRetrieval(); // to make test fail after we have verified correct behaviour for this test

        final UpdateProgressAdapter adapter = EasyMock.createStrictMock(UpdateProgressAdapter.class);
        adapter.checkStarted();
        adapter.commsFailure(EasyMock.isA(IOException.class)); // generated due to stopping test
        adapter.finished();
        EasyMock.replay(adapter);

        startUpdateAndWait(adapter);
        
        EasyMock.verify(adapter);
    }
    
    /**
     * 
     */
    @Test(timeout = 3000)
    public void doCheckIfPreviousCheckDateOtherThanTodayStored() {
        createUpdateChecker();
        mPrefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true);
        Assert.assertTrue(mPrefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED));
        mPrefs.setDateOfLastUpdateAvailableCheck(NOT_TODAYS_DATE);
        Assert.assertEquals(NOT_TODAYS_DATE, mPrefs.getDateOfLastUpdateAvailableCheck());

        mRemoteFileRetriever.injectCommsFailureOnVersionNumberRetrieval(); // to make test fail after we have verified correct behaviour for this test

        final UpdateProgressAdapter adapter = EasyMock.createStrictMock(UpdateProgressAdapter.class);
        adapter.checkStarted();
        adapter.commsFailure(EasyMock.isA(IOException.class)); // generated due to stopping test
        adapter.finished();
        EasyMock.replay(adapter);

        startUpdateAndWait(adapter);
        
        EasyMock.verify(adapter);
    }

    /**
     * 
     */
    @Test(timeout = 3000)
    public void ifApplicationPluginDoesNotExistUpdateCheckerReportsNoVersion() {
        final PluginRegistry pluginRegistry = new DefaultPluginRegistry();        
        createUpdateCheckerWithAppDetails(pluginRegistry);
        checkNoApplicationVersion();
    }
    
    /**
     * 
     */
    @Test(timeout = 3000)
    public void ifApplicationPluginHasNullUpdateBaseURLUpdateCheckerReportsNoURL() {
        final PluginRegistry pluginRegistry = new DefaultPluginRegistry();
        pluginRegistry.addPluginDescriptor(new ApplicationPluginDescriptor(true, "Foo", "1.0", "1.0", null, "", "", "", ""));
        createUpdateCheckerWithAppDetails(pluginRegistry);
        checkNoUpdateURL();
    }

    /**
     * 
     */
    @Test(timeout = 3000)
    public void ifApplicationPluginHasEmptyUpdateBaseURLUpdateCheckerReportsNoURL() {
        final PluginRegistry pluginRegistry = new DefaultPluginRegistry();
        pluginRegistry.addPluginDescriptor(new ApplicationPluginDescriptor(true, "Foo", "1.0", "1.0", "", "", "", "", ""));
        createUpdateCheckerWithAppDetails(pluginRegistry);
        checkNoUpdateURL();
    }

    /**
     * 
     */
    @Test(timeout = 3000)
    public void ifApplicationPluginReportsUnknownVersionUpdateCheckerReporteNoVersion() {
        final PluginRegistry pluginRegistry = new DummyAppPluginRegistry("Foo", PluginRegistry.UNKNOWN_VERSION);
        createUpdateCheckerWithAppDetails(pluginRegistry);
        checkNoApplicationVersion();
    }
    
    /**
     * 
     */
    @Test(timeout = 3000)
    public void ifApplicationPluginReportsEmptyVersionUpdateCheckerReportsNoVersion() {
        final PluginRegistry pluginRegistry = new DummyAppPluginRegistry("Foo", "");
        createUpdateCheckerWithAppDetails(pluginRegistry);
        checkNoApplicationVersion();
    }

    private void checkNoApplicationVersion() {
        mPrefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true);
        mPrefs.setDateOfLastUpdateAvailableCheck(NOT_TODAYS_DATE);
        mPrefs.setLastRemoteUpdateVersion(VERSION_0_9_0);
    
        final UpdateProgressAdapter adapter = EasyMock.createStrictMock(UpdateProgressAdapter.class);
        adapter.noApplicationVersionDeclared();
        adapter.finished();
        EasyMock.replay(adapter);

        startUpdateAndWait(adapter);
        
        EasyMock.verify(adapter);
        
        Assert.assertEquals(NOT_TODAYS_DATE, mPrefs.getDateOfLastUpdateAvailableCheck());
        Assert.assertEquals(VERSION_0_9_0, mPrefs.getLastRemoteUpdateVersion());
        Assert.assertEquals(0, mMessageQueue.size());
    }
    
    private void checkNoUpdateURL() {
        mPrefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true);
        mPrefs.setDateOfLastUpdateAvailableCheck(NOT_TODAYS_DATE);
        mPrefs.setLastRemoteUpdateVersion(VERSION_0_9_0);
    
        final UpdateProgressAdapter adapter = EasyMock.createStrictMock(UpdateProgressAdapter.class);
        adapter.noUpdateURLDeclared();
        adapter.finished();
        EasyMock.replay(adapter);

        startUpdateAndWait(adapter);
        
        EasyMock.verify(adapter);
        
        Assert.assertEquals(NOT_TODAYS_DATE, mPrefs.getDateOfLastUpdateAvailableCheck());
        Assert.assertEquals(VERSION_0_9_0, mPrefs.getLastRemoteUpdateVersion());
        Assert.assertEquals(0, mMessageQueue.size());
    }
    
    /**
     * 
     */
    @Test(timeout = 3000)
    public void commsFailureOnVersionNumberRetrievalReportsErrorAndNoLastCheckDateSet() {
        createUpdateChecker();
        mPrefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true);
        mPrefs.setDateOfLastUpdateAvailableCheck(NOT_TODAYS_DATE);

        mRemoteFileRetriever.injectCommsFailureOnVersionNumberRetrieval();
        
        final UpdateProgressAdapter adapter = EasyMock.createStrictMock(UpdateProgressAdapter.class);
        adapter.checkStarted();
        adapter.commsFailure(EasyMock.isA(IOException.class));
        adapter.finished();
        EasyMock.replay(adapter);

        startUpdateAndWait(adapter);
        
        EasyMock.verify(adapter);
        
        Assert.assertEquals(NOT_TODAYS_DATE, mPrefs.getDateOfLastUpdateAvailableCheck());
        Assert.assertEquals(0, mMessageQueue.size());
    }

    /**
     * 
     */
    @Test(timeout = 3000)
    public void versionNumberSameLastCheckDateSetButNoMessage() {
        createUpdateChecker();
        mPrefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true);
        mPrefs.setDateOfLastUpdateAvailableCheck(NOT_TODAYS_DATE);
        mPrefs.setLastRemoteUpdateVersion(VERSION_1_0_0);
        Assert.assertEquals(VERSION_1_0_0, mPrefs.getLastRemoteUpdateVersion());
        
        mRemoteFileRetriever.injectReturnedVersionNumber(VERSION_1_0_0);
        
        final UpdateProgressAdapter adapter = EasyMock.createStrictMock(UpdateProgressAdapter.class);
        adapter.checkStarted();
        adapter.noUpdateAvailable();
        adapter.finished();
        EasyMock.replay(adapter);

        startUpdateAndWait(adapter);
        
        EasyMock.verify(adapter);
        
        Assert.assertEquals(TODAYS_DATE, mPrefs.getDateOfLastUpdateAvailableCheck());
        Assert.assertEquals(VERSION_1_0_0, mPrefs.getLastRemoteUpdateVersion());
        Assert.assertEquals(0, mMessageQueue.size());
    }
    
    /**
     * 
     */
    @Test(timeout = 3000)
    public void commsFailureOnChangeLogRetrievalReportsErrorAndNoLastCheckDateSet() {
        createUpdateChecker();
        mPrefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true);
        mPrefs.setDateOfLastUpdateAvailableCheck(NOT_TODAYS_DATE);

        Assert.assertEquals("", mPrefs.getLastRemoteUpdateVersion());
        
        mRemoteFileRetriever.injectReturnedVersionNumber(VERSION_1_0_0);
        mRemoteFileRetriever.injectCommsFailureOnChangeLogRetrieval();
        
        final UpdateProgressAdapter adapter = EasyMock.createStrictMock(UpdateProgressAdapter.class);
        adapter.checkStarted();
        adapter.commsFailure(EasyMock.isA(IOException.class));
        adapter.finished();
        EasyMock.replay(adapter);

        startUpdateAndWait(adapter);
        
        EasyMock.verify(adapter);
        
        Assert.assertEquals(NOT_TODAYS_DATE, mPrefs.getDateOfLastUpdateAvailableCheck());
        Assert.assertEquals(0, mMessageQueue.size());
    }
    
    /**
     * 
     */
    @Test(timeout = 3000)
    public void successfulUpdateCheckSendsMessageAndLastCheckDateAndVersionSet() {
        createUpdateChecker();
        mPrefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true);
        mPrefs.setDateOfLastUpdateAvailableCheck(NOT_TODAYS_DATE);

        mPrefs.setLastRemoteUpdateVersion(VERSION_0_9_0);
        Assert.assertEquals(VERSION_0_9_0, mPrefs.getLastRemoteUpdateVersion());
    
        mRemoteFileRetriever.injectReturnedVersionNumber(VERSION_1_0_0);
        mRemoteFileRetriever.injectReturnedChangeLogContents("change log");
        
        final UpdateProgressAdapter adapter = EasyMock.createStrictMock(UpdateProgressAdapter.class);
        adapter.checkStarted();
        adapter.updateAvailable();
        adapter.finished();
        EasyMock.replay(adapter);

        startUpdateAndWait(adapter);
        
        EasyMock.verify(adapter);
        
        Assert.assertEquals("http://localhost/", mRemoteFileRetriever.getUpdateURL());
        Assert.assertEquals(TODAYS_DATE, mPrefs.getDateOfLastUpdateAvailableCheck());
        Assert.assertEquals(VERSION_1_0_0, mPrefs.getLastRemoteUpdateVersion());
        Assert.assertEquals(1, mMessageQueue.size());
        final Message message = mMessageQueue.getMessageByIndex(0);
        Assert.assertEquals("change log", message.getMessageContent().toString());
    }

    /**
     * 
     */
    @Test(timeout = 3000)
    public void successfulUpdateCheckButTransformFailureDoesntSendMessageOrUpdateLastCheckDateAndVersion() {
        createUpdateChecker();
        mPrefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true);
        mPrefs.setDateOfLastUpdateAvailableCheck(NOT_TODAYS_DATE);

        mPrefs.setLastRemoteUpdateVersion(VERSION_0_9_0);
        Assert.assertEquals(VERSION_0_9_0, mPrefs.getLastRemoteUpdateVersion());
    
        mRemoteFileRetriever.injectReturnedVersionNumber(VERSION_1_0_0);
        mRemoteFileRetriever.injectReturnedChangeLogContents("change log");
        mChangeLogTransformer.injectReadFailure();
        
        final UpdateProgressAdapter adapter = EasyMock.createStrictMock(UpdateProgressAdapter.class);
        adapter.checkStarted();
        adapter.transformFailure(EasyMock.isA(IOException.class));
        adapter.finished();
        EasyMock.replay(adapter);

        startUpdateAndWait(adapter);
        
        EasyMock.verify(adapter);

        Assert.assertEquals(NOT_TODAYS_DATE, mPrefs.getDateOfLastUpdateAvailableCheck());
        Assert.assertEquals(VERSION_0_9_0, mPrefs.getLastRemoteUpdateVersion());
        Assert.assertEquals(0, mMessageQueue.size());
    }

    /**
     * 
     */
    @Test(timeout = 3000)
    public void successfulUpdateCheckButTransformFailureDueToBadRemoteVersionDoesntSendMessageOrUpdateLastCheckDateAndVersion() {
        createUpdateChecker();
        mPrefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true);
        mPrefs.setDateOfLastUpdateAvailableCheck(NOT_TODAYS_DATE);

        mPrefs.setLastRemoteUpdateVersion(VERSION_0_9_0);
        Assert.assertEquals(VERSION_0_9_0, mPrefs.getLastRemoteUpdateVersion());
    
        mRemoteFileRetriever.injectReturnedVersionNumber("this is not a valid version number");
        mRemoteFileRetriever.injectReturnedChangeLogContents("change log");
        mChangeLogTransformer.injectReadFailure();
        
        final UpdateProgressAdapter adapter = EasyMock.createStrictMock(UpdateProgressAdapter.class);
        adapter.checkStarted();
        adapter.transformFailure(EasyMock.isA(IOException.class));
        adapter.finished();
        EasyMock.replay(adapter);

        startUpdateAndWait(adapter);
        
        EasyMock.verify(adapter);

        Assert.assertEquals(NOT_TODAYS_DATE, mPrefs.getDateOfLastUpdateAvailableCheck());
        Assert.assertEquals(VERSION_0_9_0, mPrefs.getLastRemoteUpdateVersion());
        Assert.assertEquals(0, mMessageQueue.size());
    }

    /**
     * 
     */
    @Test
    public void updateCheckHandledOnSeparateThread() {
        createUpdateChecker();
        Assert.assertFalse(mPrefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED));
        final AtomicBoolean onSeparateThread = new AtomicBoolean(false);
        final Thread mainThread = Thread.currentThread();
        
        final UpdateProgressAdapter adapter = new UpdateProgressAdapter() {
            public void alreadyCheckedToday() {
            }
            public void checkStarted() {
            }
            public void commsFailure(final IOException exception) {
            }
            public void noApplicationVersionDeclared() {
            }
            public void noUpdateURLDeclared() {
            }
            public void noUpdateAvailable() {
            }
            public void transformFailure(final IOException exception) {
            }
            public void transformFailure(final ParseException exception) {
            }
            public void updateAvailable() {
            }

            public void updateCheckDisallowed() {
                final boolean other = Thread.currentThread() != mainThread;
                onSeparateThread.set(other);
            }
            public void finished() {
            }
        };
        adapter.updateCheckDisallowed();

        startUpdateAndWait(adapter);
        
        Assert.assertTrue(onSeparateThread.get());
    }
}
