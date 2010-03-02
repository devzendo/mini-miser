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

package org.devzendo.minimiser.messagequeue;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.prefs.BooleanFlag;
import org.devzendo.minimiser.prefs.Prefs;

/**
 * A simple factory for greating MessageQueueBorderGuards.
 * @author matt
 *
 */
public final class MessageQueueBorderGuardFactory {
    private static final Logger LOGGER = Logger
            .getLogger(MessageQueueBorderGuardFactory.class);
    private final Prefs prefs;

    /**
     * Create the factory, given its prerequisites
     * @param preferences the preferences, for the DSTA flags
     */
    public MessageQueueBorderGuardFactory(final Prefs preferences) {
        this.prefs = preferences;
    }
    
    /**
     * Given a message, create an appropriate MessageQueueBorderGuard that
     * can handle this message.
     * @param message a message of a certain type
     * @return the appropriate MessageQueueBorderGuard
     */
    public MessageQueueBorderGuard createBorderGuard(final Message message) {
        if (message instanceof SimpleMessage) {
            return new SimpleMessageHandler();
        } else if (message instanceof SimpleDSTAMessage) {
            return new SimpleDSTAMessageHandler();
        } else if (message instanceof BooleanFlagSettingMessage) {
            return new BooleanFlagSettingMessageHandler();
        } else {
            return new VetoEverythingMessageHandler();
        }
    }
    
    private class SimpleMessageHandler implements MessageQueueBorderGuard {
        /**
         * {@inheritDoc}
         */
        public boolean isAllowed(final Message message) {
            return true;
        }
        
        /**
         * {@inheritDoc}
         */
        public void prepareMessage(final Message message) {
            // do nothing
        }

        /**
         * {@inheritDoc}
         */
        public void processMessageRemoval(final Message message) {
            // do nothing
        }
    }

    private class SimpleDSTAMessageHandler implements MessageQueueBorderGuard {
        /**
         * {@inheritDoc}
         */
        public boolean isAllowed(final Message message) {
            final SimpleDSTAMessage simpleDSTAMessage = ((SimpleDSTAMessage) message);
            return !prefs.isDontShowThisAgainFlagSet(getMessageIdString(simpleDSTAMessage));
        }

        /**
         * {@inheritDoc}
         */
        public void prepareMessage(final Message message) {
            // do nothing, as messages marked DSTA won't make it past isAllowed
        }

        /**
         * {@inheritDoc}
         */
        public void processMessageRemoval(final Message message) {
            final SimpleDSTAMessage simpleDSTAMessage = ((SimpleDSTAMessage) message);
            if (simpleDSTAMessage.dontShowAgain()) {
                LOGGER.debug("Message '" + simpleDSTAMessage.getSubject() + "' is not being shown again");
                prefs.setDontShowThisAgainFlag(getMessageIdString(simpleDSTAMessage));
            }
        }
        
        private String getMessageIdString(final SimpleDSTAMessage simpleDSTAMessage) {
            return simpleDSTAMessage.getDstaMessageId().toString();
        }
    }

    private class BooleanFlagSettingMessageHandler implements MessageQueueBorderGuard {
        /**
         * {@inheritDoc}
         */
        public boolean isAllowed(final Message message) {
            return true;
        }
        
        /**
         * {@inheritDoc}
         */
        public void prepareMessage(final Message message) {
            final BooleanFlagSettingMessage booleanFlagSettingMessage = ((BooleanFlagSettingMessage) message);
            final BooleanFlag booleanFlagName = booleanFlagSettingMessage.getBooleanFlagName();
            final boolean booleanFlagValueFromPrefs = prefs.isBooleanFlagSet(booleanFlagName);
            LOGGER.debug("Preparing message for BooleanFlag " + booleanFlagName + " with initial setting " + booleanFlagValueFromPrefs);
            booleanFlagSettingMessage.setBooleanFlagValue(booleanFlagValueFromPrefs);
        }

        /**
         * {@inheritDoc}
         */
        public void processMessageRemoval(final Message message) {
            final BooleanFlagSettingMessage booleanFlagSettingMessage = ((BooleanFlagSettingMessage) message);
            final BooleanFlag booleanFlagName = booleanFlagSettingMessage.getBooleanFlagName();
            LOGGER.info("Setting BooleanFlag " + booleanFlagName + " to " + booleanFlagSettingMessage.isBooleanFlagSet());
            prefs.setBooleanFlag(booleanFlagName, booleanFlagSettingMessage.isBooleanFlagSet());
        }
    }

    private class VetoEverythingMessageHandler implements MessageQueueBorderGuard {
        /**
         * {@inheritDoc}
         */
        public boolean isAllowed(final Message message) {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        public void prepareMessage(final Message message) {
            // do nothing
        }

        public void processMessageRemoval(final Message message) {
            // do nothing
        }
    }

}
