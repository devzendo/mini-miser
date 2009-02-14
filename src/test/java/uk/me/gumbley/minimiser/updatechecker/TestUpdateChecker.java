package uk.me.gumbley.minimiser.updatechecker;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.messagequeue.Message;
import uk.me.gumbley.minimiser.messagequeue.MessageQueue;
import uk.me.gumbley.minimiser.messagequeue.MessageQueueBorderGuardFactory;
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
    private static final Logger LOGGER = Logger
            .getLogger(TestUpdateChecker.class);
    private static final String VERSION_1_0_0 = "1.0.0";
    private static final String VERSION_0_9_0 = "0.9.0";
    private static final String NOT_TODAYS_DATE = "22/02/1999";
    private static final String TODAYS_DATE = "08/12/2008";
    private Prefs prefs;
    private MessageQueue messageQueue;
    private UpdateChecker updateChecker;
    private StubRemoteFileRetriever remoteFileRetriever;
    private NullChangeLogTransformer changeLogTransformer;
    private Today today;
    private WorkerPool workerPool;
    
    /**
     * @throws IOException on failure
     * 
     */
    @Before
    public void getPrerequisites() throws IOException {
        prefs = TestPrefs.createUnitTestPrefsFile();
        messageQueue = new MessageQueue(new MessageQueueBorderGuardFactory(prefs));
        remoteFileRetriever = new StubRemoteFileRetriever();
        changeLogTransformer = new NullChangeLogTransformer();
        today = new StubToday(TODAYS_DATE);
        workerPool = new WorkerPool();
        
        updateChecker = new DefaultUpdateChecker(prefs, messageQueue, remoteFileRetriever, changeLogTransformer, today, workerPool);
    }

    /**
     * Since the update checker is asynchronous, and executes via a worker
     * pool, I can't just tag on a 'wait' task after running the update, hence
     * this decorator.
     * 
     * @author matt
     *
     */
    private final class WaitForFinishUpdateProgressAdapterDecorator implements UpdateProgressAdapter {
        private final UpdateProgressAdapter updateProgressAdapter;
        private CountDownLatch countDownLatch;

        public WaitForFinishUpdateProgressAdapterDecorator(final UpdateProgressAdapter decorated) {
            this.updateProgressAdapter = decorated;
            countDownLatch = new CountDownLatch(1);
        }

        /**
         * {@inheritDoc}
         */
        public void alreadyCheckedToday() {
            updateProgressAdapter.alreadyCheckedToday();
        }

        /**
         * {@inheritDoc}
         */
        public void checkStarted() {
            updateProgressAdapter.checkStarted();
        }

        /**
         * {@inheritDoc}
         */
        public void commsFailure(final IOException exception) {
            updateProgressAdapter.commsFailure(exception);
        }

        /**
         * {@inheritDoc}
         */
        public void noUpdateAvailable() {
            updateProgressAdapter.noUpdateAvailable();
        }

        /**
         * {@inheritDoc}
         */
        public void transformFailure(final IOException exception) {
            updateProgressAdapter.transformFailure(exception);
        }

        /**
         * {@inheritDoc}
         */
        public void transformFailure(final ParseException exception) {
            updateProgressAdapter.transformFailure(exception);
        }

        /**
         * {@inheritDoc}
         */
        public void updateAvailable() {
            updateProgressAdapter.updateAvailable();
            
        }

        /**
         * {@inheritDoc}
         */
        public void updateCheckDisallowed() {
            updateProgressAdapter.updateCheckDisallowed();
        }
        
        /**
         * {@inheritDoc}
         */
        public void finished() {
            updateProgressAdapter.finished();
            countDownLatch.countDown();
        }
        
        /**
         * Wait until the worker thread has called the finished() method on the
         * adapter.
         */
        public void waitForFinished() {
            try {
                countDownLatch.await();
            } catch (final InterruptedException e) {
                LOGGER.warn("Interrupted whilst waiting: " + e.getMessage());
            }
        }
    }
    
    private void startUpdateAndWait(final UpdateProgressAdapter adapter) {
        final WaitForFinishUpdateProgressAdapterDecorator decoratedAdapter =
            new WaitForFinishUpdateProgressAdapterDecorator(adapter);
        updateChecker.triggerUpdateCheck(decoratedAdapter);
        decoratedAdapter.waitForFinished();
    }
        
    /**
     * 
     */
    @Test
    public void updateCheckDisallowedDoesNotStartUpdate() {
        Assert.assertFalse(prefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED));
        
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
        prefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true);
        Assert.assertTrue(prefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED));
        prefs.setDateOfLastUpdateAvailableCheck(TODAYS_DATE);
        Assert.assertEquals(TODAYS_DATE, prefs.getDateOfLastUpdateAvailableCheck());

        final UpdateProgressAdapter adapter = EasyMock.createStrictMock(UpdateProgressAdapter.class);
        adapter.alreadyCheckedToday();
        adapter.finished();
        EasyMock.replay(adapter);

        startUpdateAndWait(adapter);
        
        EasyMock.verify(adapter);
        Assert.assertEquals(0, messageQueue.size());
    }
    
    /**
     * 
     */
    @Test(timeout = 3000)
    public void doCheckIfNoPreviousCheckDateStored() {
        prefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true);
        Assert.assertTrue(prefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED));
        Assert.assertEquals("", prefs.getDateOfLastUpdateAvailableCheck());

        remoteFileRetriever.injectCommsFailureOnVersionNumberRetrieval(); // to make test fail after we have verified correct behaviour for this test

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
        prefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true);
        Assert.assertTrue(prefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED));
        prefs.setDateOfLastUpdateAvailableCheck(NOT_TODAYS_DATE);
        Assert.assertEquals(NOT_TODAYS_DATE, prefs.getDateOfLastUpdateAvailableCheck());

        remoteFileRetriever.injectCommsFailureOnVersionNumberRetrieval(); // to make test fail after we have verified correct behaviour for this test

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
    public void commsFailureOnVersionNumberRetrievalReportsErrorAndNoLastCheckDateSet() {
        prefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true);
        prefs.setDateOfLastUpdateAvailableCheck(NOT_TODAYS_DATE);

        remoteFileRetriever.injectCommsFailureOnVersionNumberRetrieval();
        
        final UpdateProgressAdapter adapter = EasyMock.createStrictMock(UpdateProgressAdapter.class);
        adapter.checkStarted();
        adapter.commsFailure(EasyMock.isA(IOException.class));
        adapter.finished();
        EasyMock.replay(adapter);

        startUpdateAndWait(adapter);
        
        EasyMock.verify(adapter);
        
        Assert.assertEquals(NOT_TODAYS_DATE, prefs.getDateOfLastUpdateAvailableCheck());
        Assert.assertEquals(0, messageQueue.size());
    }

    /**
     * 
     */
    @Test(timeout = 3000)
    public void versionNumberSameLastCheckDateSetButNoMessage() {
        prefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true);
        prefs.setDateOfLastUpdateAvailableCheck(NOT_TODAYS_DATE);
        prefs.setLastRemoteUpdateVersion(VERSION_1_0_0);
        Assert.assertEquals(VERSION_1_0_0, prefs.getLastRemoteUpdateVersion());
        
        remoteFileRetriever.injectReturnedVersionNumber(VERSION_1_0_0);
        
        final UpdateProgressAdapter adapter = EasyMock.createStrictMock(UpdateProgressAdapter.class);
        adapter.checkStarted();
        adapter.noUpdateAvailable();
        adapter.finished();
        EasyMock.replay(adapter);

        startUpdateAndWait(adapter);
        
        EasyMock.verify(adapter);
        
        Assert.assertEquals(TODAYS_DATE, prefs.getDateOfLastUpdateAvailableCheck());
        Assert.assertEquals(VERSION_1_0_0, prefs.getLastRemoteUpdateVersion());
        Assert.assertEquals(0, messageQueue.size());
    }
    
    /**
     * 
     */
    @Test(timeout = 3000)
    public void commsFailureOnChangeLogRetrievalReportsErrorAndNoLastCheckDateSet() {
        prefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true);
        prefs.setDateOfLastUpdateAvailableCheck(NOT_TODAYS_DATE);

        Assert.assertEquals("", prefs.getLastRemoteUpdateVersion());
        
        remoteFileRetriever.injectReturnedVersionNumber(VERSION_1_0_0);
        remoteFileRetriever.injectCommsFailureOnChangeLogRetrieval();
        
        final UpdateProgressAdapter adapter = EasyMock.createStrictMock(UpdateProgressAdapter.class);
        adapter.checkStarted();
        adapter.commsFailure(EasyMock.isA(IOException.class));
        adapter.finished();
        EasyMock.replay(adapter);

        startUpdateAndWait(adapter);
        
        EasyMock.verify(adapter);
        
        Assert.assertEquals(NOT_TODAYS_DATE, prefs.getDateOfLastUpdateAvailableCheck());
        Assert.assertEquals(0, messageQueue.size());
    }
    
    /**
     * 
     */
    @Test(timeout = 3000)
    public void successfulUpdateCheckSendsMessageAndLastCheckDateAndVersionSet() {
        prefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true);
        prefs.setDateOfLastUpdateAvailableCheck(NOT_TODAYS_DATE);

        prefs.setLastRemoteUpdateVersion(VERSION_0_9_0);
        Assert.assertEquals(VERSION_0_9_0, prefs.getLastRemoteUpdateVersion());
    
        remoteFileRetriever.injectReturnedVersionNumber(VERSION_1_0_0);
        remoteFileRetriever.injectReturnedChangeLogContents("change log");
        
        final UpdateProgressAdapter adapter = EasyMock.createStrictMock(UpdateProgressAdapter.class);
        adapter.checkStarted();
        adapter.updateAvailable();
        adapter.finished();
        EasyMock.replay(adapter);

        startUpdateAndWait(adapter);
        
        EasyMock.verify(adapter);
        
        Assert.assertEquals(TODAYS_DATE, prefs.getDateOfLastUpdateAvailableCheck());
        Assert.assertEquals(VERSION_1_0_0, prefs.getLastRemoteUpdateVersion());
        Assert.assertEquals(1, messageQueue.size());
        final Message message = messageQueue.getMessageByIndex(0);
        Assert.assertEquals("change log", message.getMessageContent().toString());
    }

    /**
     * 
     */
    @Test(timeout = 3000)
    public void successfulUpdateCheckButTransformFailureDoesntSendMessageOrUpdateLastCheckDateAndVersion() {
        prefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true);
        prefs.setDateOfLastUpdateAvailableCheck(NOT_TODAYS_DATE);

        prefs.setLastRemoteUpdateVersion(VERSION_0_9_0);
        Assert.assertEquals(VERSION_0_9_0, prefs.getLastRemoteUpdateVersion());
    
        remoteFileRetriever.injectReturnedVersionNumber(VERSION_1_0_0);
        remoteFileRetriever.injectReturnedChangeLogContents("change log");
        changeLogTransformer.injectReadFailure();
        
        final UpdateProgressAdapter adapter = EasyMock.createStrictMock(UpdateProgressAdapter.class);
        adapter.checkStarted();
        adapter.transformFailure(EasyMock.isA(IOException.class));
        adapter.finished();
        EasyMock.replay(adapter);

        startUpdateAndWait(adapter);
        
        EasyMock.verify(adapter);

        Assert.assertEquals(NOT_TODAYS_DATE, prefs.getDateOfLastUpdateAvailableCheck());
        Assert.assertEquals(VERSION_0_9_0, prefs.getLastRemoteUpdateVersion());
        Assert.assertEquals(0, messageQueue.size());
    }

    /**
     * 
     */
    @Test(timeout = 3000)
    public void successfulUpdateCheckButTransformFailureDueToBadRemoteVersionDoesntSendMessageOrUpdateLastCheckDateAndVersion() {
        prefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true);
        prefs.setDateOfLastUpdateAvailableCheck(NOT_TODAYS_DATE);

        prefs.setLastRemoteUpdateVersion(VERSION_0_9_0);
        Assert.assertEquals(VERSION_0_9_0, prefs.getLastRemoteUpdateVersion());
    
        remoteFileRetriever.injectReturnedVersionNumber("this is not a valid version number");
        remoteFileRetriever.injectReturnedChangeLogContents("change log");
        changeLogTransformer.injectReadFailure();
        
        final UpdateProgressAdapter adapter = EasyMock.createStrictMock(UpdateProgressAdapter.class);
        adapter.checkStarted();
        adapter.transformFailure(EasyMock.isA(IOException.class));
        adapter.finished();
        EasyMock.replay(adapter);

        startUpdateAndWait(adapter);
        
        EasyMock.verify(adapter);

        Assert.assertEquals(NOT_TODAYS_DATE, prefs.getDateOfLastUpdateAvailableCheck());
        Assert.assertEquals(VERSION_0_9_0, prefs.getLastRemoteUpdateVersion());
        Assert.assertEquals(0, messageQueue.size());
    }

    /**
     * 
     */
    @Test
    public void updateCheckHandledOnSeparateThread() {
        Assert.assertFalse(prefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED));
        final AtomicBoolean onSeparateThread = new AtomicBoolean(false);
        final Thread mainThread = Thread.currentThread();
        
        final UpdateProgressAdapter adapter = new UpdateProgressAdapter() {
            public void alreadyCheckedToday() {
            }
            public void checkStarted() {
            }
            public void commsFailure(final IOException exception) {
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
