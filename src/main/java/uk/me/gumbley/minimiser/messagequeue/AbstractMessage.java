package uk.me.gumbley.minimiser.messagequeue;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;



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
        return new HashCodeBuilder(1, 31)
            .append(messageContent)
            .append(messageImportance)
            .append(messageSubject)
            .toHashCode();
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
        return new EqualsBuilder()
            .append(this.messageContent, other.messageContent)
            .append(this.messageImportance, other.messageImportance)
            .append(this.messageSubject, other.messageSubject).isEquals();
    }
}
