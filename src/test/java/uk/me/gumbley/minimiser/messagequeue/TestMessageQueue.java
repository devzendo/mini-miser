package uk.me.gumbley.minimiser.messagequeue;

import java.io.IOException;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.gui.dialog.dstamessage.DSTAMessageId;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.prefs.Prefs;


/**
 * Tests the model behind the Message Queue.
 * @author matt
 *
 */
public final class TestMessageQueue extends LoggingTestCase {

    private Prefs prefs;
    private MessageQueue messageQueue;

    /**
     * @throws IOException on failure, never.
     * 
     */
    @Before
    public void getPrerequisites() throws IOException {
        prefs = new StubDSTAPrefs();
        messageQueue = new MessageQueue(prefs);
    }
    
    /**
     * 
     */
    @Test
    public void emptiness() {
        Assert.assertEquals(0, messageQueue.size());
        Assert.assertEquals(-1, messageQueue.getCurrentMessageIndex());
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    @Test(expected = IllegalArgumentException.class)
    public void addNullThrows() {
        final Observer<MessageQueueEvent> obs = EasyMock.createStrictMock(Observer.class);
        EasyMock.replay(obs);
        
        messageQueue.addMessageQueueEventObserver(obs);
        
        messageQueue.addMessage(null);
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    @Test
    public void addMessageGeneratesEvent() {
        final SimpleMessage message = new SimpleMessage("Subject", "Hello");

        final Observer<MessageQueueEvent> obs = EasyMock.createStrictMock(Observer.class);
        obs.eventOccurred(EasyMock.eq(new MessageAddedEvent(message)));
        obs.eventOccurred(EasyMock.eq(new MessageQueueModifiedEvent(1)));
        EasyMock.replay(obs);
        
        messageQueue.addMessageQueueEventObserver(obs);

        messageQueue.addMessage(message);
        EasyMock.verify(obs);
        Assert.assertEquals(0, messageQueue.getCurrentMessageIndex()); // switch to first on adding it
        Assert.assertEquals(1, messageQueue.size());
        
        Assert.assertEquals(message, messageQueue.getMessageByIndex(0));
    }
    

    /**
     * 
     */
    @SuppressWarnings("unchecked")
    @Test(expected = IllegalArgumentException.class)
    public void removeNullThrows() {
        final Observer<MessageQueueEvent> obs = EasyMock.createStrictMock(Observer.class);
        EasyMock.replay(obs);
        
        messageQueue.addMessageQueueEventObserver(obs);
        
        messageQueue.removeMessage(null);
    }
    
    /**
     * 
     */
    @Test(expected = IllegalStateException.class) 
    public void removeNonexistantThrows() {
        final SimpleMessage message = new SimpleMessage("Subject", "Hello");
        messageQueue.removeMessage(message);
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    @Test
    public void removeMessageGeneratesEvent() {
        final SimpleMessage message = new SimpleMessage("Subject", "Hello");
        messageQueue.addMessage(message);

        final Observer<MessageQueueEvent> obs = EasyMock.createStrictMock(Observer.class);
        obs.eventOccurred(EasyMock.eq(new MessageRemovedEvent(message)));
        obs.eventOccurred(EasyMock.eq(new MessageQueueModifiedEvent(0)));
        EasyMock.replay(obs);
        
        messageQueue.addMessageQueueEventObserver(obs);

        messageQueue.removeMessage(message);
        EasyMock.verify(obs);
        Assert.assertEquals(-1, messageQueue.getCurrentMessageIndex()); // -1 when last removed
        Assert.assertEquals(0, messageQueue.size());
    }
    
    
    /**
     * 
     */
    @Test
    public void addingSubsequentMessagesDoesntChangeCurrent() {
        final SimpleMessage message1 = new SimpleMessage("Subject", "Hello");
        messageQueue.addMessage(message1);

        final SimpleMessage message2 = new SimpleMessage("Object", "Polymorphic");
        messageQueue.addMessage(message2);
        
        Assert.assertEquals(0, messageQueue.getCurrentMessageIndex());
    }
    
    /**
     * 
     */
    @Test
    public void removingCurrentMessageSwitchesToNext() {
        final SimpleMessage message1 = new SimpleMessage("Subject", "Hello");
        messageQueue.addMessage(message1);

        final SimpleMessage message2 = new SimpleMessage("Object", "Polymorphic");
        messageQueue.addMessage(message2);
        
        messageQueue.removeMessage(message1);
        
        Assert.assertEquals(0, messageQueue.getCurrentMessageIndex());
    }

    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void negativeSetCurrentIndexDisallowed() {
        messageQueue.setCurrentMessageIndex(-1);
    }

    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void cantExceedSizeOfEmptyListWithSetCurrentIndex() {
        messageQueue.setCurrentMessageIndex(0);
    }

    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void cantExceedSizeOfPopulatedListWithSetCurrentIndex() {
        final SimpleMessage message1 = new SimpleMessage("Subject", "Hello");
        messageQueue.addMessage(message1);
        
        messageQueue.setCurrentMessageIndex(1);
    }

    /**
     * 
     */
    @Test
    public void removingCurrentThatsLastMessageSwitchesToNewLast() {
        final SimpleMessage message1 = new SimpleMessage("Subject", "Hello");
        messageQueue.addMessage(message1);
        Assert.assertEquals(0, messageQueue.getCurrentMessageIndex());
        
        final SimpleMessage message2 = new SimpleMessage("Object", "Polymorphic");
        messageQueue.addMessage(message2);
        Assert.assertEquals(0, messageQueue.getCurrentMessageIndex()); // doesn't move current

        final SimpleMessage message3 = new SimpleMessage("Three", "Bottles");
        messageQueue.addMessage(message3);
        Assert.assertEquals(0, messageQueue.getCurrentMessageIndex()); // doesn't move current

        messageQueue.setCurrentMessageIndex(2);
        
        Assert.assertEquals(2, messageQueue.getCurrentMessageIndex());
        
        messageQueue.removeMessage(message3);
        
        Assert.assertEquals(1, messageQueue.getCurrentMessageIndex());
    }
    
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    @Test
    public void nonBlockedSimpleDSTAMessageGetsSentOnAdd() {
        final SimpleDSTAMessage message = new SimpleDSTAMessage("subject", "content", DSTAMessageId.TEST);

        final Observer<MessageQueueEvent> obs = EasyMock.createStrictMock(Observer.class);
        obs.eventOccurred(EasyMock.eq(new MessageAddedEvent(message)));
        obs.eventOccurred(EasyMock.eq(new MessageQueueModifiedEvent(1)));
        EasyMock.replay(obs);

        messageQueue.addMessageQueueEventObserver(obs);

        messageQueue.addMessage(message);
        
        EasyMock.verify(obs);
    }
    
    @Test
    public void blockedSimpleDSTAMessageDoesNotGetSentOnAdd() {
        prefs.setDontShowThisAgainFlag(DSTAMessageId.TEST.toString());
        
        final SimpleDSTAMessage message = new SimpleDSTAMessage("subject", "content", DSTAMessageId.TEST);

        final Observer<MessageQueueEvent> obs = EasyMock.createStrictMock(Observer.class);
        EasyMock.replay(obs);

        messageQueue.addMessageQueueEventObserver(obs);

        messageQueue.addMessage(message);
        
        EasyMock.verify(obs);
    }
    
    @Test
    public void nonBlockedMessageIndicatesItIsNotBlocked() {
        Assert.assertFalse(messageQueue.isDontShowThisAgainFlagSet(DSTAMessageId.TEST));
    }

    @Test
    public void blockedMessageIndicatesItIsBlocked() {
        prefs.setDontShowThisAgainFlag(DSTAMessageId.TEST.toString());

        Assert.assertTrue(messageQueue.isDontShowThisAgainFlagSet(DSTAMessageId.TEST));
    }

    // WOZERE add dsta message, set its dsta flag, remove it, check prefs has flag stored
    @Test
    public void dstaFlagSetInPrefsWhenRemoved() {
        final SimpleDSTAMessage message = new SimpleDSTAMessage("subject", "content", DSTAMessageId.TEST);

        messageQueue.addMessage(message);

        message.setDontShowAgain(true); // they checked the DSTA box
        
        Assert.assertFalse(prefs.isDontShowThisAgainFlagSet(DSTAMessageId.TEST.toString()));
        
        messageQueue.removeMessage(message); // they removed it from the queue

        Assert.assertTrue(prefs.isDontShowThisAgainFlagSet(DSTAMessageId.TEST.toString()));
    }
}
