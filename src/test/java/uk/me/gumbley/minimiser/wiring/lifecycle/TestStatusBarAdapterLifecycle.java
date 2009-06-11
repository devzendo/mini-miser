package uk.me.gumbley.minimiser.wiring.lifecycle;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.me.gumbley.minimiser.gui.HeadlessStatusBar;
import uk.me.gumbley.minimiser.lifecycle.LifecycleManager;
import uk.me.gumbley.minimiser.messagequeue.MessageQueue;
import uk.me.gumbley.minimiser.messagequeue.SimpleMessage;
import uk.me.gumbley.minimiser.messagequeue.StubMessageQueuePrefs;
import uk.me.gumbley.minimiser.prefs.PrefsFactory;
import uk.me.gumbley.minimiser.springloader.ApplicationContext;
import uk.me.gumbley.minimiser.springloader.SpringLoaderUnittestCase;

/**
 * Tests the wiring of the Status Bar to Message Queue adapters that are
 * controlled as a Lifecycle.
 * 
 * @author matt
 *
 */
@ApplicationContext("uk/me/gumbley/minimiser/wiring/lifecycle/StatusBarAdaptersLifecycleTestCase.xml")
public final class TestStatusBarAdapterLifecycle extends SpringLoaderUnittestCase {

    private LifecycleManager lifecycleManager;
    private HeadlessStatusBar headlessStatusBar;
    private MessageQueue messageQueue;

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        lifecycleManager = getSpringLoader().getBean("lifecycleManager", LifecycleManager.class);
        Assert.assertNotNull(lifecycleManager);
        
        headlessStatusBar = getSpringLoader().getBean("statusBar", HeadlessStatusBar.class);
        Assert.assertNotNull(headlessStatusBar);
        
        final PrefsFactory prefsFactory = getSpringLoader().getBean("&prefs", PrefsFactory.class);
        prefsFactory.setPrefs(new StubMessageQueuePrefs());

        messageQueue = getSpringLoader().getBean("messageQueue", MessageQueue.class);
        Assert.assertNotNull(messageQueue);
    }

    /**
     * 
     */
    @Test
    public void taskExecutedOnStartup() {
        final SimpleMessage simpleMessage = new SimpleMessage("subject", "content");

        Assert.assertFalse(headlessStatusBar.isMessageQueueIndicatorEnabled());
        messageQueue.addMessage(simpleMessage);
        Assert.assertFalse(headlessStatusBar.isMessageQueueIndicatorEnabled());
        messageQueue.removeMessage(simpleMessage);
        
        lifecycleManager.startup();
        
        Assert.assertFalse(headlessStatusBar.isMessageQueueIndicatorEnabled());
        messageQueue.addMessage(simpleMessage);
        Assert.assertTrue(headlessStatusBar.isMessageQueueIndicatorEnabled());
    }
    
    /**
     * 
     */
    @Test
    public void launchViewerButtonBoundOnStartup() {
        Assert.assertFalse(headlessStatusBar.isLaunchMessageQueueActionListenerSet());
        
        lifecycleManager.startup();
        
        Assert.assertTrue(headlessStatusBar.isLaunchMessageQueueActionListenerSet());
    }
}
