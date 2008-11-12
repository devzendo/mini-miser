package uk.me.gumbley.minimiser.messagequeue;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.logging.LoggingTestCase;
import uk.me.gumbley.minimiser.openlist.DatabaseEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseOpenedEvent;


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
        Assert.assertNull(messageQueue.getCurrentMessage());
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
        Assert.assertNull(messageQueue.getCurrentMessage()); // this is under user control
        Assert.assertEquals(1, messageQueue.size());
        
        Assert.assertEquals(message, messageQueue.getMessage(0));
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
        Assert.assertNull(messageQueue.getCurrentMessage()); // this is under user control
        Assert.assertEquals(0, messageQueue.size());
    }
}
