package uk.me.gumbley.minimiser.upgradedetector;

import org.apache.log4j.Logger;

import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.messagequeue.BooleanFlagSettingMessage;
import uk.me.gumbley.minimiser.messagequeue.Message;
import uk.me.gumbley.minimiser.messagequeue.MessageQueue;
import uk.me.gumbley.minimiser.pluginmanager.AppDetails;
import uk.me.gumbley.minimiser.prefs.CoreBooleanFlags;
import uk.me.gumbley.minimiser.prefs.Prefs;

/**
 * When updated/fresh install is detected, and the 'check updates allowed'
 * flag is not set in prefs, post a message requesting the setting of the flag.
 * 
 * @author matt
 *
 */
public final class QueueUpdateAvailableFlagSettingMessageListener implements Observer<UpgradeEvent> {
    private static final Logger LOGGER = Logger
            .getLogger(QueueUpdateAvailableFlagSettingMessageListener.class);
    private final Prefs mPrefs;
    private final MessageQueue mMessageQueue;
    private final AppDetails mAppDetails;

    /**
     * Construct the listener
     * @param preferences used to test for the 'check updates allowed' flag
     * before queueing the message
     * @param queue the message queue to queue the message on
     * @param appDetails the application name and version
     */
    public QueueUpdateAvailableFlagSettingMessageListener(
            final Prefs preferences,
            final MessageQueue queue,
            final AppDetails appDetails) {
        mPrefs = preferences;
        mMessageQueue = queue;
        mAppDetails = appDetails;
    }

    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final UpgradeEvent observableEvent) {
        if (mPrefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED)) {
            LOGGER.debug("No need to queue request to set the UPDATE_CHECK_ALLOWED flag - it is already set");
        } else {
            LOGGER.debug("The UPDATE_CHECK_ALLOWED flag is not set; queueing request for it to be set");
            if (observableEvent instanceof FreshInstallEvent) {
                queueMessage();
            } else if (observableEvent instanceof SoftwareUpgradedEvent) {
                queueMessage();
            }
        }
    }

    private void queueMessage() {
        final Message.Importance importance = Message.Importance.MEDIUM;
        final String subject = "Please decide whether you want update checks";
        final String content = mAppDetails.getApplicationName() + " can check periodically for software updates.<br>"
            + "This requires an active Internet connection.<br>"
            + "Only the latest software details are obtained from our web site.<br>"
            + "No personal information is sent, other than your computer's IP address.<br>"
            + "Your IP address is logged by our ISP, but is not used by us to identify you.";
        final String checkboxText = "Allow periodic software update checks?";
        mMessageQueue.addMessage(new BooleanFlagSettingMessage(subject, content, importance, CoreBooleanFlags.UPDATE_CHECK_ALLOWED, checkboxText));
    }
}
