package uk.me.gumbley.minimiser.messagequeue;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;


/**
 * Tests the model behind the Message Queue.
 * @author matt
 *
 */
public final class TestMessageQueue extends LoggingTestCase {
    private MessageQueue messageQueue;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        messageQueue = new MessageQueue();
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
     * Records that onRemoval has been called.
     * @author matt
     *
     */
    private class RecordingRemovalMessage extends AbstractMessage {
        private boolean onRemovalCalled;
        public RecordingRemovalMessage(final String subject, final Object content) {
            super(subject, content);
            onRemovalCalled = false;
        }

        /**
         * {@inheritDoc}
         */
        public void onRemoval() {
            onRemovalCalled = true;
        }

        /**
         * @return true iff onRemoval called.
         */
        protected final boolean isOnRemovalCalled() {
            return onRemovalCalled;
        }
    }
    
    /**
     * 
     */
    @Test
    public void removeCallsOnRemoval() {
        final RecordingRemovalMessage rrm = new RecordingRemovalMessage("Subject", "Detail");
        Assert.assertFalse(rrm.isOnRemovalCalled());
        
        messageQueue.addMessage(rrm);
        Assert.assertFalse(rrm.isOnRemovalCalled());
        
        messageQueue.removeMessage(rrm);
        Assert.assertTrue(rrm.isOnRemovalCalled());
        
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
}
