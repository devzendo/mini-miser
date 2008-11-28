package uk.me.gumbley.minimiser.messagequeue;



/**
 * A simple message.
 * 
 * @author matt
 *
 */
public final class SimpleMessage extends AbstractMessage {
    
    
    /**
     * @param subject the message subject
     * @param content the message content
     */
    public SimpleMessage(final String subject, final Object content) {
        super(subject, content);
    }

    /**
     * @param subject the message subject
     * @param content the message content
     * @param importance the importance of the message
     */
    public SimpleMessage(final String subject, final Object content, final Importance importance) {
        super(subject, content, importance);
    }
}
