package uk.me.gumbley.minimiser.updatechecker;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import uk.me.gumbley.minimiser.gui.dialog.problem.ProblemDialogHelper;
import uk.me.gumbley.minimiser.messagequeue.Message;
import uk.me.gumbley.minimiser.messagequeue.MessageQueue;
import uk.me.gumbley.minimiser.messagequeue.SimpleMessage;
import uk.me.gumbley.minimiser.pluginmanager.PluginRegistry;
import uk.me.gumbley.minimiser.prefs.CoreBooleanFlags;
import uk.me.gumbley.minimiser.prefs.Prefs;
import uk.me.gumbley.minimiser.util.Today;
import uk.me.gumbley.minimiser.util.WorkerPool;

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

    private final Prefs mPrefs;
    private final MessageQueue mMessageQueue;
    private final RemoteFileRetriever mRemoteFileRetriever;
    private final ChangeLogTransformer mChangeLogTransformer;
    private final Today mTodayGenerator;
    private final WorkerPool mWorkerPool;
    private final PluginRegistry mPluginRegistry;

    /**
     * @param preferences the prefs to check for 'update check allowed' flag
     * and in which to store the date of last update check, latest software
     * version
     * @param msgQueue the message queue on which to post any 'update available'
     * messages
     * @param retriever used to actually retrieve the latest software version
     * and change log from a remote site.
     * @param logXform the change log transformer used to give the user the
     * salient parts from the change log.
     * @param today used to find out today's date
     * @param pool the worker pool, upon which requests to do the update will
     * be queued
     * @param pluginRegistry used to find out the application's version
     */
    public DefaultUpdateChecker(final Prefs preferences, final MessageQueue msgQueue,
            final RemoteFileRetriever retriever, final ChangeLogTransformer logXform,
            final Today today, final WorkerPool pool,
            final PluginRegistry pluginRegistry) {
        mPrefs = preferences;
        mMessageQueue = msgQueue;
        mRemoteFileRetriever = retriever;
        mChangeLogTransformer = logXform;
        mTodayGenerator = today;
        mWorkerPool = pool;
        mPluginRegistry = pluginRegistry;
    }

    /**
     * {@inheritDoc}
     */
    public void triggerUpdateCheck(final UpdateProgressAdapter progressAdapter) {
        mWorkerPool.submit(new Runnable() {
            public void run() {
                // This synchronization ensures that only one
                // update check is performed simultaneously.
                synchronized (DefaultUpdateChecker.this) {
                    try {
                        try {
                            executeUpdateCheck(progressAdapter);
                        } finally {
                            LOGGER.debug("Indicating finished to progress adapter");
                            progressAdapter.finished();
                        }
                    } catch (final Exception e) {
                        ProblemDialogHelper.reportProblem("while performing an update check", e);
                    }
                }
            }
        });
    }

    private void executeUpdateCheck(final UpdateProgressAdapter progressAdapter) {
        if (!initialConditionsOk(progressAdapter)) {
            LOGGER.debug("Initial conditions not OK");
            return;
        }

        LOGGER.info("Starting update check");
        progressAdapter.checkStarted();
        
        // Get the remote version number
        final String remoteVersionNumber;
        try {
            LOGGER.info("Retrieving " + VERSION_NUMBER_FILE + "...");
            remoteVersionNumber = mRemoteFileRetriever.getFileContents(VERSION_NUMBER_FILE);
            LOGGER.info("... retrieved");
        } catch (final IOException e) {
            LOGGER.warn("Could not retrieve latest version number: " + e.getMessage());
            progressAdapter.commsFailure(e);
            return;
        }
        LOGGER.info("Remote version number is " + remoteVersionNumber);
        final String lastRemoteUpdateVersion = mPrefs.getLastRemoteUpdateVersion();
        LOGGER.info("Last remote update version number is " + lastRemoteUpdateVersion);
        if (remoteVersionNumber.equals(lastRemoteUpdateVersion)) {
            LOGGER.info("Remote and local versions match; no update available");
            progressAdapter.noUpdateAvailable();
            lastSuccessfulUpdateWasToday();
            return;
        }
        
        // There's an update available. Get the change log into a
        // temp file.
        File remoteChangeLogTempFile = null;
        try {
            LOGGER.info("Retrieving " + CHANGE_LOG_FILE + "...");
            remoteChangeLogTempFile = mRemoteFileRetriever.saveFileContents(CHANGE_LOG_FILE);
            LOGGER.info("... retrieved");
            
            processDownloadedChangeLog(progressAdapter, remoteVersionNumber,
                remoteChangeLogTempFile);
            
        } catch (final IOException e) {
            LOGGER.warn("Could not retrieve change log: " + e.getMessage());
            progressAdapter.commsFailure(e);
            return;
        } finally {
            if (remoteChangeLogTempFile != null) {
                // Now we don't need the downloaded changelog any more
                if (!remoteChangeLogTempFile.delete()) {
                    LOGGER.warn("Could not delete temporary copy of remote changelog " + remoteChangeLogTempFile.getAbsolutePath());
                }
            }
        }
    }

    private void processDownloadedChangeLog(
            final UpdateProgressAdapter progressAdapter,
            final String remoteVersionNumber,
            final File remoteChangeLogTempFile) {
        // Check for valid version numbers
        final ComparableVersion thisVersion;
        final ComparableVersion remoteVersion;
        try {
            thisVersion = new ComparableVersion(mPluginRegistry.getApplicationVersion());
            remoteVersion = new ComparableVersion(remoteVersionNumber);
        } catch (final IllegalArgumentException iae) {
            LOGGER.warn("Could not transform change log due to bad version numbers [runtime '"
                + mPluginRegistry.getApplicationVersion() + "'] [remote '"
                + remoteVersionNumber + "']: " + iae.getMessage());
            progressAdapter.transformFailure(new IOException(iae.getMessage()));
            return;
        }

        // Transform the relevant section of the change log into a
        // form suitable for a Message to the user.
        final String changeLogContents;
        try {
            LOGGER.debug("Transforming change log into a message");
            changeLogContents = mChangeLogTransformer.readFileSubsection(thisVersion, remoteVersion, remoteChangeLogTempFile);
        } catch (final IOException e) {
            LOGGER.warn("Could not read change log: " + e.getMessage());
            progressAdapter.transformFailure(e);
            return;
        } catch (final ParseException e) {
            LOGGER.warn("Could not transform change log: " + e.getMessage());
            progressAdapter.transformFailure(e);
            return;
        }

        // All done; post the message.
        LOGGER.info("An update to " + remoteVersionNumber + " is available");
        progressAdapter.updateAvailable();
        final SimpleMessage message = new SimpleMessage("Update to version " + remoteVersionNumber + " available",
            changeLogContents, Message.Importance.MEDIUM);
        // TODO add in the downloads page URL to the message with
        // a hypertext link
        // TODO PLUGIN - allow the download page and version
        // check URLs to be specified in plugin descriptors
        mMessageQueue.addMessage(message);
        lastSuccessfulUpdateWasToday();
        mPrefs.setLastRemoteUpdateVersion(remoteVersionNumber);
    }

    private boolean initialConditionsOk(final UpdateProgressAdapter progressAdapter) {
        // Handle initial conditions that must be correct for the update
        // check to proceed.
        LOGGER.info("Testing conditions for update check");
        if (!mPrefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED)) {
            LOGGER.error("Update checking has not been allowed in preferences");
            progressAdapter.updateCheckDisallowed();
            return false;
        }
        
        if (mPrefs.getDateOfLastUpdateAvailableCheck().equals(mTodayGenerator.getUKDateString())) {
            LOGGER.info("An update check has already been done today; not repeating it");
            progressAdapter.alreadyCheckedToday();
            return false;
        }
        
        if (mPluginRegistry.getApplicationVersion().equals(PluginRegistry.UNKNOWN_VERSION)) {
            LOGGER.error("Update checking cannot continue since the application plugin has not declared its version");
            progressAdapter.noApplicationVersionDeclared();
            return false;
        }
        return true;
    }

    private void lastSuccessfulUpdateWasToday() {
        mPrefs.setDateOfLastUpdateAvailableCheck(mTodayGenerator.getUKDateString());
    }
}
