package uk.me.gumbley.minimiser.messagequeue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.minimiser.gui.dialog.dstamessage.DSTAMessageId;
import uk.me.gumbley.minimiser.prefs.CoreBooleanFlags;


/**
 * Tests the MessageQueueBorderGuards
 * 
 * @author matt
 *
 */
public final class TestMessageQueueBorderGuard {
    
    private StubMessageQueuePrefs prefs;
    private MessageQueueBorderGuardFactory borderGuardFactory;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        prefs = new StubMessageQueuePrefs();
        borderGuardFactory = new MessageQueueBorderGuardFactory(prefs);
    }
    
    /**
     * 
     */
    @Test
    public void simpleMessageIsntVetoed() {
        final SimpleMessage message = new SimpleMessage("subject", "text");
        final MessageQueueBorderGuard borderGuard = borderGuardFactory.createBorderGuard(message);
        Assert.assertTrue(borderGuard.isAllowed(message));
    }
    
    /**
     * 
     */
    @Test
    public void simpleDSTAMessageThatIsntBlockedIsntVetoed() {
        final SimpleDSTAMessage message = new SimpleDSTAMessage("subject", "text", DSTAMessageId.TEST);
        final MessageQueueBorderGuard borderGuard = borderGuardFactory.createBorderGuard(message);
        Assert.assertTrue(borderGuard.isAllowed(message));
    }
    
    /**
     * 
     */
    @Test
    public void simpleDSTAMessageThatIsBlockedIsVetoed() {
        final SimpleDSTAMessage message = new SimpleDSTAMessage("subject", "text", DSTAMessageId.TEST);
        prefs.setDontShowThisAgainFlag(DSTAMessageId.TEST.toString());
        final MessageQueueBorderGuard borderGuard = borderGuardFactory.createBorderGuard(message);
        Assert.assertFalse(borderGuard.isAllowed(message));
    }
    
    /**
     * 
     */
    @Test
    public void simpleDSTAMessageThatIndicatesBlockageIsPersistedOnRemoval() {
        final SimpleDSTAMessage message = new SimpleDSTAMessage("subject", "text", DSTAMessageId.TEST);
        message.setDontShowAgain(true);
        Assert.assertFalse(prefs.isDontShowThisAgainFlagSet(message.getDstaMessageId().toString()));
        final MessageQueueBorderGuard borderGuard = borderGuardFactory.createBorderGuard(message);
        borderGuard.processMessageRemoval(message);
        Assert.assertTrue(prefs.isDontShowThisAgainFlagSet(message.getDstaMessageId().toString()));
    }
    
    /**
     * 
     */
    @Test
    public void updateCheckMessageIsPopulatedWithPersistentStateOfFlag() {
        prefs.setBooleanFlag(CoreBooleanFlags.UPDATE_CHECK_ALLOWED, true);
        final BooleanFlagSettingMessage message = new BooleanFlagSettingMessage(
            "allow updates check?", "could the app check for updates?", CoreBooleanFlags.UPDATE_CHECK_ALLOWED, "Do this?");
        Assert.assertFalse(message.isBooleanFlagSet());
        final MessageQueueBorderGuard borderGuard = borderGuardFactory.createBorderGuard(message);
        borderGuard.prepareMessage(message);
        Assert.assertTrue(message.isBooleanFlagSet());
    }
    
    /**
     * 
     */
    @Test
    public void updateCheckMessageThatChangesFlagChangesPersistentFlagOnRemoval() {
        final BooleanFlagSettingMessage message = new BooleanFlagSettingMessage(
            "allow updates check?", "could the app check for updates?", CoreBooleanFlags.UPDATE_CHECK_ALLOWED, "Do this?");
        message.setBooleanFlagValue(true);
        Assert.assertFalse(prefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED));
        final MessageQueueBorderGuard borderGuard = borderGuardFactory.createBorderGuard(message);
        borderGuard.processMessageRemoval(message);
        Assert.assertTrue(prefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED));
    }
}
