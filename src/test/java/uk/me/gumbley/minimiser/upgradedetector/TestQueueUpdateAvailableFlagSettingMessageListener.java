package uk.me.gumbley.minimiser.upgradedetector;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.messagequeue.MessageQueue;
import uk.me.gumbley.minimiser.messagequeue.MessageQueueBorderGuardFactory;
import uk.me.gumbley.minimiser.messagequeue.MessageQueueEvent;
import uk.me.gumbley.minimiser.messagequeue.MessageQueueModifiedEvent;
import uk.me.gumbley.minimiser.messagequeue.StubMessageQueuePrefs;
import uk.me.gumbley.minimiser.pluginmanager.AppDetails;
import uk.me.gumbley.minimiser.prefs.CoreBooleanFlags;
import uk.me.gumbley.minimiser.prefs.Prefs;


/**
 * When updated/fresh install is detected, and the 'check updates allowed'
 * flag is not set in prefs, post a message requesting the setting of the flag.
 * 
 * @author matt
 *
 */
public final class TestQueueUpdateAvailableFlagSettingMessageListener {
    private Prefs prefs;
    private MessageQueue messageQueue;
    private QueueUpdateAvailableFlagSettingMessageListener listener;
    private MessageQueueModifiedRecorder recorder;
    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        prefs = new StubMessageQueuePrefs();
        messageQueue = new MessageQueue(new MessageQueueBorderGuardFactory(prefs));
        recorder = new MessageQueueModifiedRecorder();
        messageQueue.addMessageQueueEventObserver(recorder);
        final AppDetails appDetails = new AppDetails();
        appDetails.setApplicationName("Test App");
        appDetails.setApplicationVersion("0.1.0");
        listener = new QueueUpdateAvailableFlagSettingMessageListener(prefs, messageQueue, appDetails);
    }
    
    private class MessageQueueModifiedRecorder implements Observer<MessageQueueEvent> {

        private int queueSize = 0;
        
        public final int getQueueSize() {
            return queueSize;
        }

        public void eventOccurred(final MessageQueueEvent observableEvent) {
            if (observableEvent instanceof MessageQueueModifiedEvent) {
                final MessageQueueModifiedEvent mqme = (MessageQueueModifiedEvent) observableEvent;
                queueSize = mqme.getNewQueueSize();
            }
        }
        
    }
    /**
     * Quite how the flag can be set on a fresh install is beyond me, but let's
     * test the case anyway
     */
    @SuppressWarnings("unchecked")
    @Test
    public void flagSetOnFreshInstallShouldYieldNoMessage() {
        prefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true);
        Assert.assertTrue(prefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED));
        
        listener.eventOccurred(new FreshInstallEvent("1.0.0"));
        
        Assert.assertEquals(0, recorder.getQueueSize());
    }
    

    /**
     * 
     */
    @SuppressWarnings("unchecked")
    @Test
    public void flagSetOnUpgradeShouldYieldNoMessage() {
        prefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true);
        Assert.assertTrue(prefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED));
        
        listener.eventOccurred(new SoftwareUpgradedEvent("1.0.0", "0.5.0"));
        
        Assert.assertEquals(0, recorder.getQueueSize());
    }
    
    /**
     * This is the usual fresh startup case
     */
    @SuppressWarnings("unchecked")
    @Test
    public void flagNotSetOnFreshInstallShouldYieldMessage() {
        Assert.assertFalse(prefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED));
        
        listener.eventOccurred(new FreshInstallEvent("1.0.0"));
        
        Assert.assertEquals(1, recorder.getQueueSize());
    }
    

    /**
     * 
     */
    @SuppressWarnings("unchecked")
    @Test
    public void flagNotSetOnUpgradeShouldYieldMessage() {
        Assert.assertFalse(prefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED));
        
        listener.eventOccurred(new SoftwareUpgradedEvent("1.0.0", "0.5.0"));
        
        Assert.assertEquals(1, recorder.getQueueSize());
    }
}
