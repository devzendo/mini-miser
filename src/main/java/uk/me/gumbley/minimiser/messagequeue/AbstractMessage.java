package uk.me.gumbley.minimiser.messagequeue;



/**
 * Stores all the content for messages.
 * 
 * @author matt
 *
 */
public abstract class AbstractMessage implements Message {
    
    private final String messageSubject;
    private final Object messageContent;
    private final Importance messageImportance;

    /**
     * Create a message with a subject, content, importance.
     * @param subject the message subject
     * @param content the message content 
     * @param importance the message importance
     */
    public AbstractMessage(final String subject, final Object content, final Importance importance) {
        this.messageSubject = subject;
        this.messageContent = content;
        this.messageImportance = importance;
    }

    /**
     * Create a message with a subject and content, of default MEDIUM
     * importance.
     * @param subject the message subject
     * @param content the message content 
     */
    public AbstractMessage(final String subject, final Object content) {
        this(subject, content, Importance.MEDIUM);
    }

    
    /**
     * {@inheritDoc}
     */
    public final Object getMessageContent() {
        return messageContent;
    }

    /**
     * {@inheritDoc}
     */
    public final Importance getMessageImportance() {
        return messageImportance;
    }

    /**
     * {@inheritDoc}
     */
    public final String getSubject() {
        return messageSubject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((messageContent == null) ? 0 : messageContent.hashCode());
        result = prime * result + ((messageImportance == null) ? 0 : messageImportance.hashCode());
        result = prime * result + ((messageSubject == null) ? 0 : messageSubject.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final AbstractMessage other = (AbstractMessage) obj;
        if (messageContent == null) {
            if (other.messageContent != null) {
                return false;
            }
        } else if (!messageContent.equals(other.messageContent)) {
            return false;
        }
        if (messageImportance == null) {
            if (other.messageImportance != null) {
                return false;
            }
        } else if (!messageImportance.equals(other.messageImportance)) {
            return false;
        }
        if (messageSubject == null) {
            if (other.messageSubject != null) {
                return false;
            }
        } else if (!messageSubject.equals(other.messageSubject)) {
            return false;
        }
        return true;
    }
}
