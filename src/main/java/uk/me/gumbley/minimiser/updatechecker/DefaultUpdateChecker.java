package uk.me.gumbley.minimiser.updatechecker;

import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import uk.me.gumbley.minimiser.messagequeue.Message;
import uk.me.gumbley.minimiser.messagequeue.MessageQueue;
import uk.me.gumbley.minimiser.messagequeue.SimpleMessage;
import uk.me.gumbley.minimiser.prefs.CoreBooleanFlags;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.util.Today;
import uk.me.gumbley.minimiser.version.AppVersion;

/**
 * Performs update checks asynchronously, honouring the 'update check allowed'
 * flag, only doing the check once daily, and storing the retrieved data. If
 * an update is available, posts a message. 
 * 
 * @author matt
 *
 */
public final class DefaultUpdateChecker implements UpdateChecker {
    private static final Logger LOGGER = Logger
            .getLogger(DefaultUpdateChecker.class);

    private static final String VERSION_NUMBER_FILE = "version.txt";
    private static final String CHANGE_LOG_FILE = "changes.txt";

    private final Prefs prefs;
    private final MessageQueue messageQueue;
    private final RemoteFileRetriever remoteFileRetriever;
    private final ChangeLogTransformer changeLogTransformer;

    private final Today todayGenerator;

    /**
     * @param preferences the prefs to check for 'update check allowed' flag
     * and in which to store the date of last update check, latest software
     * version
     * @param msgQueue the message queue on which to post any 'update available'
     * messages
     * @param retriever used to actually retrieve the latest software version
     * and change log from a remote ite.
     * @param logXform the change log transformer used to give the user the
     * salient parts from the change log.
     * @param today used to dind out today's date
     */
    public DefaultUpdateChecker(final Prefs preferences, final MessageQueue msgQueue,
            final RemoteFileRetriever retriever, final ChangeLogTransformer logXform,
            final Today today) {
        this.prefs = preferences;
        this.messageQueue = msgQueue;
        this.remoteFileRetriever = retriever;
        this.changeLogTransformer = logXform;
        this.todayGenerator = today;
    }

    /**
     * {@inheritDoc}
     */
    public void triggerUpdateCheck(final UpdateProgressAdapter progressAdapter) {
        LOGGER.info("Testing conditions for update check");
        if (!prefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED)) {
            LOGGER.error("Update checking has not been allowed in preferences");
            progressAdapter.updateCheckDisallowed();
            return;
        }
        
        if (prefs.getDateOfLastUpdateAvailableCheck().equals(todayGenerator.getUKDateString())) {
            LOGGER.info("An update check has already been done today; not repeating it");
            progressAdapter.alreadyCheckedToday();
            return;
        }
        
        LOGGER.info("Starting updates check");
        progressAdapter.checkStarted();
        

        final String remoteVersionNumber;
        try {
            remoteVersionNumber = remoteFileRetriever.getFileContents(VERSION_NUMBER_FILE);
        } catch (final IOException e) {
            LOGGER.warn("Could not retrieve latest version number: " + e.getMessage());
            progressAdapter.commsFailure(e);
            return;
        }
        if (remoteVersionNumber.equals(prefs.getLastRemoteUpdateVersion())) {
            LOGGER.info("Remote and local versions match; no update available");
            progressAdapter.noUpdateAvailable();
            lastSuccessfulUpdateWasToday();
            return;
        }
        
        final File remoteChangeLogTempFile;
        try {
            remoteChangeLogTempFile = remoteFileRetriever.saveFileContents(CHANGE_LOG_FILE);
        } catch (final IOException e) {
            LOGGER.warn("Could not retrieve change log: " + e.getMessage());
            progressAdapter.commsFailure(e);
            return;
        }
        
        final String changeLogContents;
        try {
            changeLogContents = changeLogTransformer.readFileSubsection(AppVersion.getVersion(), remoteVersionNumber, remoteChangeLogTempFile);
        } catch (final IOException e) {
            LOGGER.warn("Could not transform chage log: " + e.getMessage());
            progressAdapter.transformFailure(e);
            return;
        }
        
        final SimpleMessage message = new SimpleMessage("Update to version " + remoteVersionNumber + " available",
            changeLogContents, Message.Importance.MEDIUM);
        messageQueue.addMessage(message);
        lastSuccessfulUpdateWasToday();
        prefs.setLastRemoteUpdateVersion(remoteVersionNumber);
        
        if (!remoteChangeLogTempFile.delete()) {
            LOGGER.warn("Could not delete temporary copy of remote changelog " + remoteChangeLogTempFile.getAbsolutePath());
        }
    }

    private void lastSuccessfulUpdateWasToday() {
        prefs.setDateOfLastUpdateAvailableCheck(todayGenerator.getUKDateString());
    }
}
