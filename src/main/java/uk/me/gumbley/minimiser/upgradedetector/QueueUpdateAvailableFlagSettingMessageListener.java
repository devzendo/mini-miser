package uk.me.gumbley.minimiser.upgradedetector;

import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.common.AppName;
import uk.me.gumbley.minimiser.messagequeue.BooleanFlagSettingMessage;
import uk.me.gumbley.minimiser.messagequeue.Message;
import uk.me.gumbley.minimiser.messagequeue.MessageQueue;
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
    private final Prefs prefs;
    private final MessageQueue messageQueue;

    /**
     * Construct the listener
     * @param preferences used to test for the 'check updates allowed' flag
     * before queueing the message
     * @param queue the message queue to queue the message on
     */
    public QueueUpdateAvailableFlagSettingMessageListener(final Prefs preferences, final MessageQueue queue) {
        this.prefs = preferences;
        this.messageQueue = queue;
    }

    /**
     * {@inheritDoc}
     */
    public void eventOccurred(final UpgradeEvent observableEvent) {
        if (prefs.isBooleanFlagSet(CoreBooleanFlags.UPDATE_CHECK_ALLOWED)) {
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
        final String content = AppName.getAppName() + " can check periodically for software updates.<br>"
            + "This requires an active Internet connection.<br>"
            + "Only the latest software details are obtained from our web site.<br>"
            + "No personal information is sent, other than your computer's IP address.<br>"
            + "Your IP address is logged by our ISP, but is not used by us to identify you.";
        final String checkboxText = "Allow periodic software update checks?";
        messageQueue.addMessage(new BooleanFlagSettingMessage(subject, content, importance, CoreBooleanFlags.UPDATE_CHECK_ALLOWED, checkboxText));
    }
}