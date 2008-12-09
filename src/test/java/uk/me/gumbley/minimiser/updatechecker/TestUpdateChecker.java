package uk.me.gumbley.minimiser.updatechecker;

import java.io.IOException;
import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.commoncode.concurrency.ThreadUtils;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.messagequeue.Message;
import uk.me.gumbley.minimiser.messagequeue.MessageQueue;
import uk.me.gumbley.minimiser.messagequeue.MessageQueueBorderGuardFactory;
import uk.me.gumbley.minimiser.prefs.CoreBooleanFlags;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.prefs.TestPrefs;
import uk.me.gumbley.minimiser.util.StubToday;
import uk.me.gumbley.minimiser.util.Today;


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
    private static final Logger LOGGER = Logger
            .getLogger(TestUpdateChecker.class);
    private Prefs prefs;
    private MessageQueue messageQueue;
    private UpdateChecker updateChecker;
    private StubRemoteFileRetriever remoteFileRetriever;
    private ChangeLogTransformer changeLogTransformer;
    private Today today;
    
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

        updateChecker = new DefaultUpdateChecker(prefs, messageQueue, remoteFileRetriever, changeLogTransformer, today);
    }
    
    /**
     * 
     */
    @Test
    public void updateCheckDisallowedDoesNotStartUpdate() {
        Assert.assertFalse(prefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED));
        
        final UpdateProgressAdapter adapter = EasyMock.createStrictMock(UpdateProgressAdapter.class);
        adapter.updateCheckDisallowed();
        EasyMock.replay(adapter);

        updateChecker.triggerUpdateCheck(adapter);
        
        ThreadUtils.waitNoInterruption(250);
        
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
        EasyMock.replay(adapter);

        updateChecker.triggerUpdateCheck(adapter);
        
        ThreadUtils.waitNoInterruption(250);
        
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
        EasyMock.replay(adapter);

        updateChecker.triggerUpdateCheck(adapter);
        
        ThreadUtils.waitNoInterruption(250);
        
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
        EasyMock.replay(adapter);

        updateChecker.triggerUpdateCheck(adapter);
        
        ThreadUtils.waitNoInterruption(250);
        
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
        EasyMock.replay(adapter);

        updateChecker.triggerUpdateCheck(adapter);
        
        ThreadUtils.waitNoInterruption(250);
        
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
        EasyMock.replay(adapter);

        updateChecker.triggerUpdateCheck(adapter);
        
        ThreadUtils.waitNoInterruption(250);
        
        EasyMock.verify(adapter);
        
        Assert.assertEquals(TODAYS_DATE, prefs.getDateOfLastUpdateAvailableCheck());
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
        EasyMock.replay(adapter);

        updateChecker.triggerUpdateCheck(adapter);
        
        ThreadUtils.waitNoInterruption(250);
        
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
        EasyMock.replay(adapter);

        updateChecker.triggerUpdateCheck(adapter);
        
        ThreadUtils.waitNoInterruption(250);
        
        EasyMock.verify(adapter);
        
        Assert.assertEquals(TODAYS_DATE, prefs.getDateOfLastUpdateAvailableCheck());
        Assert.assertEquals(VERSION_1_0_0, prefs.getLastRemoteUpdateVersion());
        Assert.assertEquals(1, messageQueue.size());
        final Message message = messageQueue.getMessageByIndex(0);
        Assert.assertEquals("change log", message.getMessageContent().toString());
    }
    
    // TODO download checksums for remote files and verify them
    // TODO a test that downloads successfully, but fails to transform throwing
    // an IOException so we indicate the failure via the adapter
}
