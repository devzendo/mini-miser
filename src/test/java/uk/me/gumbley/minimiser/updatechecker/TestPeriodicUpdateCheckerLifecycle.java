package uk.me.gumbley.minimiser.updatechecker;

import java.io.IOException;

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
import uk.me.gumbley.minimiser.util.Sleeper;
import uk.me.gumbley.minimiser.util.StubToday;
import uk.me.gumbley.minimiser.util.Today;
import uk.me.gumbley.minimiser.util.WorkerPool;
import uk.me.gumbley.minimiser.wiring.lifecycle.PeriodicUpdateCheckerLifecycle;

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

    private Prefs prefs;
    private MessageQueue messageQueue;
    private UpdateChecker updateChecker;
    private StubRemoteFileRetriever remoteFileRetriever;
    private NullChangeLogTransformer changeLogTransformer;
    private Today today;
    private WorkerPool workerPool;
    private Sleeper sleeper;

    /**
     * @throws IOException on failure
     * 
     */
    @Before
    public void getPrerequisites() throws IOException {
        prefs = TestPrefs.createUnitTestPrefsFile();
        messageQueue = new MessageQueue(
            new MessageQueueBorderGuardFactory(prefs));
        remoteFileRetriever = new StubRemoteFileRetriever();
        changeLogTransformer = new NullChangeLogTransformer();
        today = new StubToday(TODAYS_DATE);
        workerPool = new WorkerPool();
        updateChecker = new DefaultUpdateChecker(prefs, messageQueue,
                remoteFileRetriever, changeLogTransformer, today, workerPool);
        sleeper = new Sleeper(3600);
    }
    
    /**
     * 
     */
    @Test(timeout = 8000)
    public void updateCheckDisallowedDoesNotStartUpdate() {
        LOGGER.info("*** updateCheckDisallowedDoesNotStartUpdate");
        Assert.assertFalse(prefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED));
        
        final UpdateProgressAdapter adapter = EasyMock.createStrictMock(UpdateProgressAdapter.class);
        adapter.updateCheckDisallowed();
        adapter.finished();
        EasyMock.replay(adapter);

        final WaitForFinishUpdateProgressAdapterDecorator decoratedAdapter =
            new WaitForFinishUpdateProgressAdapterDecorator(adapter);
        final UpdateProgressAdapterFactory adapterFactory = new StubUpdateProgressAdapterFactory(decoratedAdapter);

        final PeriodicUpdateCheckerLifecycle periodicUpdateChecker =
            new PeriodicUpdateCheckerLifecycle(updateChecker, sleeper, adapterFactory);
        periodicUpdateChecker.startup();
        
        LOGGER.debug("sleeping");
        sleeper.sleep(1000 * 60 * 61); // 61 minutes
        LOGGER.debug("woken up");
        decoratedAdapter.waitForFinished();

        EasyMock.verify(adapter);
    }

    /**
     * 
     */
    @Test(timeout = 8000)
    public void updateCheckAllowedStartsUpdate() {
        LOGGER.info("*** updateCheckAllowedStartsUpdate");
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

        final WaitForFinishUpdateProgressAdapterDecorator decoratedAdapter =
            new WaitForFinishUpdateProgressAdapterDecorator(adapter);
        
        final UpdateProgressAdapterFactory adapterFactory = new StubUpdateProgressAdapterFactory(decoratedAdapter);

        final PeriodicUpdateCheckerLifecycle periodicUpdateChecker = new PeriodicUpdateCheckerLifecycle(updateChecker, sleeper, adapterFactory);
        periodicUpdateChecker.startup();
        
        LOGGER.debug("sleeping");
        sleeper.sleep(1000 * 60 * 61); // 61 minutes
        LOGGER.debug("woken up");
        decoratedAdapter.waitForFinished();

        EasyMock.verify(adapter);

        Assert.assertEquals(TODAYS_DATE, prefs.getDateOfLastUpdateAvailableCheck());
        Assert.assertEquals(VERSION_1_0_0, prefs.getLastRemoteUpdateVersion());
        Assert.assertEquals(1, messageQueue.size());
        final Message message = messageQueue.getMessageByIndex(0);
        Assert.assertEquals("change log", message.getMessageContent().toString());
    }
}
