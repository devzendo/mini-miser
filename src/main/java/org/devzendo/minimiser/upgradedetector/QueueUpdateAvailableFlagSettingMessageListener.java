/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devzendo.minimiser.upgradedetector;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.messagequeue.BooleanFlagSettingMessage;
import org.devzendo.minimiser.messagequeue.Message;
import org.devzendo.minimiser.messagequeue.MessageQueue;
import org.devzendo.minimiser.pluginmanager.PluginRegistry;
import org.devzendo.minimiser.prefs.CoreBooleanFlags;
import org.devzendo.minimiser.prefs.MiniMiserPrefs;


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
    private final MiniMiserPrefs mPrefs;
    private final MessageQueue mMessageQueue;
    private final PluginRegistry mPluginRegistry;

    /**
     * Construct the listener
     * @param preferences used to test for the 'check updates allowed' flag
     * before queueing the message
     * @param queue the message queue to queue the message on
     * @param pluginRegistry the plugin registry
     */
    public QueueUpdateAvailableFlagSettingMessageListener(
            final MiniMiserPrefs preferences,
            final MessageQueue queue,
            final PluginRegistry pluginRegistry) {
        mPrefs = preferences;
        mMessageQueue = queue;
        mPluginRegistry = pluginRegistry;
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
        final String content = mPluginRegistry.getApplicationName() + " can check periodically for software updates.<br>"
            + "This requires an active Internet connection.<br>"
            + "Only the latest software details are obtained from our web site.<br>"
            + "No personal information is sent, other than your computer's IP address.<br>"
            + "Your IP address is logged by our ISP, but is not used by us to identify you.";
        final String checkboxText = "Allow periodic software update checks?";
        mMessageQueue.addMessage(new BooleanFlagSettingMessage(subject, content, importance, CoreBooleanFlags.UPDATE_CHECK_ALLOWED, checkboxText));
    }
}
