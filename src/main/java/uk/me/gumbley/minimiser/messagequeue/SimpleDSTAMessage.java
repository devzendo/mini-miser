package uk.me.gumbley.minimiser.messagequeue;

import uk.me.gumbley.minimiser.gui.dialog.dstamessage.DSTAMessageId;

/**
 * A SimpleDSTAMessage is a message that presents a checkbox allowing the user
 * to indicate "Don't Show This Again".
 * <p>
 * This is the unobtrusive, message queue-based version of creating a
 * DSTAMessage which is an in-your-face dialog.
 * 
 * @author matt
 *
 */
public final class SimpleDSTAMessage extends AbstractMessage implements Message {

    private DSTAMessageId dstaMessageId;
    private boolean dontShowAgain;

    /**
     * @param subject the message subject
     * @param content the message content
     * @param messageId the DSTA message id
     */
    public SimpleDSTAMessage(final String subject, final Object content, final DSTAMessageId messageId) {
        super(subject, content, Importance.MEDIUM);
        this.dstaMessageId = messageId;
        dontShowAgain = false;
    }

    /**
     * @param subject the message subject
     * @param content the message content
     * @param importance the importance of the message
     */
    public SimpleDSTAMessage(final String subject, final Object content, final Importance importance, final DSTAMessageId messageId) {
        super(subject, content, importance);
        this.dstaMessageId = messageId;
    }
    
    /**
     * {@inheritDoc}
     */
    public void onRemoval() {
        if (dontShowAgain) {
            //DSTAMessageFactory.setDontShowThisAgain()
        }
    }

    /**
     * Has the user set the 'dont' show this again' flag already for this
     * message? This does NOT indicate that this has been stored in prefs,
     * just that it has been requested. It'll be written to prefs when the
     * message is removed.
     * @return the uncommitted DSTA state 
     */
    public final boolean dontShowAgain() {
        return dontShowAgain;
    }

    /**
     * Indicate that the user has a choice for the DSTA flag
     * @param dontShowAgain true if they don't want to see it again
     */
    public final void setDontShowAgain(boolean dontShowAgain) {
        this.dontShowAgain = dontShowAgain;
    }
}
