package org.devzendo.minimiser.gui.messagequeueviewer;

import org.devzendo.minimiser.gui.dialog.dstamessage.DSTAMessageId;
import org.devzendo.minimiser.messagequeue.SimpleDSTAMessage;
import org.devzendo.minimiser.messagequeue.SimpleMessage;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests creation of MessageRenderes
 * @author matt
 *
 */
public final class TestMessageRendererFactory {
    
    private MessageRendererFactory factory;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        factory = new MessageRendererFactory();
    }
    
    /**
     * 
     */
    @Test
    public void testSimpleRenderer() {
        Assert.assertTrue(factory.createRenderer(new SimpleMessage("foo", "bar")) instanceof SimpleMessageRenderer);
    }
    
    /**
     * 
     */
    @Test
    public void testSimpleDSTARenderer() {
        Assert.assertTrue(factory.createRenderer(new SimpleDSTAMessage("foo", "bar", DSTAMessageId.TEST)) instanceof SimpleDSTAMessageRenderer);
    }
    
}
