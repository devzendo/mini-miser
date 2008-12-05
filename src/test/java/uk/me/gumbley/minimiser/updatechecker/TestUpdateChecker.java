package uk.me.gumbley.minimiser.updatechecker;

import org.junit.Before;
import uk.me.gumbley.minimiser.messagequeue.MessageQueue;
import uk.me.gumbley.minimiser.messagequeue.MessageQueueBorderGuardFactory;
import uk.me.gumbley.minimiser.messagequeue.StubMessageQueuePrefs;
import uk.me.gumbley.minimiser.prefs.Prefs;


/**
 * Tests that the update checker only fires when allowed to, and that it
 * performs correctly. 
 * @author matt
 *
 */
public final class TestUpdateChecker {
    private Prefs prefs;
    private MessageQueue messageQueue;
    private UpdateChecker updateChecker;
    private RemoteFileRetriever remoteFileRetriever;
    private ChangeLogTransformer changeLogTransformer;
    
    @Before
    public void getPrerequisites() {
        prefs = new StubMessageQueuePrefs();
        messageQueue = new MessageQueue(new MessageQueueBorderGuardFactory(prefs));
        remoteFileRetriever = new StubRemoteFileRetriever();
        changeLogTransformer = new NullChangeLogTransformer();

        updateChecker = new DefaultUpdateChecker(prefs, messageQueue, remoteFileRetriever, changeLogTransformer);
    }
}
