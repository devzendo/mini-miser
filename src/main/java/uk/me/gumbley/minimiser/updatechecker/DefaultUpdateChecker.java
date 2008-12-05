package uk.me.gumbley.minimiser.updatechecker;

import uk.me.gumbley.minimiser.messagequeue.MessageQueue;
import uk.me.gumbley.minimiser.prefs.Prefs;

/**
 * Performs update checks asynchronously, honouring the 'update check allowed'
 * flag, only doing the check once daily, and storing the retrieved data. If
 * an update is available, posts a message. 
 * 
 * @author matt
 *
 */
public final class DefaultUpdateChecker implements UpdateChecker {

    private final Prefs prefs;
    private final MessageQueue messageQueue;
    private final RemoteFileRetriever remoteFileRetriever;
    private final ChangeLogTransformer changeLogTransformer;

    /**
     * @param preferencess the prefs to check for 'update check allowed' flag
     * and in which to store the date of last update check, latest software
     * version
     * @param msgQueue the message queue on which to post any 'update available'
     * messages
     * @param retriever used to actually retrieve the latest software version
     * and change log from a remote ite.
     * @param logXform the change log transformer used to give the user the
     * salient parts from the change log.
     */
    public DefaultUpdateChecker(final Prefs preferencess, final MessageQueue msgQueue,
            final RemoteFileRetriever retriever, final ChangeLogTransformer logXform) {
        this.prefs = preferencess;
        this.messageQueue = msgQueue;
        this.remoteFileRetriever = retriever;
        this.changeLogTransformer = logXform;
    }

    /**
     * {@inheritDoc}
     */
    public void triggerUpdateCheck(final UpdateProgressAdapter progressAdapter) {
        // TODO Auto-generated method stub
        
    }
}
