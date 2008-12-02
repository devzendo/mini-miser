package uk.me.gumbley.minimiser.messagequeue;

import org.apache.log4j.Logger;
import uk.me.gumbley.minimiser.prefs.Prefs;

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
        } else if (message instanceof UpdateCheckRequestMessage) {
            return new UpdateCheckRequestMessageHandler();
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

    private class UpdateCheckRequestMessageHandler implements MessageQueueBorderGuard {
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
            final UpdateCheckRequestMessage updateCheckRequestMessage = ((UpdateCheckRequestMessage) message);
            updateCheckRequestMessage.setCheckAllowed(prefs.isUpdateAvailableCheckAllowed());
        }

        /**
         * {@inheritDoc}
         */
        public void processMessageRemoval(final Message message) {
            final UpdateCheckRequestMessage updateCheckRequestMessage = ((UpdateCheckRequestMessage) message);
            LOGGER.info("Setting update check allowed flag to " + updateCheckRequestMessage.isCheckAllowed());
            prefs.setUpdateAvailableCheckAllowed(updateCheckRequestMessage.isCheckAllowed());
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
