package uk.me.gumbley.minimiser.messagequeue;


/**
 * A simple message.
 * 
 * @author matt
 *
 */
public final class SimpleMessage implements Message {
    
    private final String messageSubject;
    private final Object messageContent;
    private final Importance messageImportance;

    /**
     * Create a message with a subject and content, of default MEDIUM
     * importance.
     * @param subject the message subject
     * @param content the message content 
     */
    public SimpleMessage(final String subject, final Object content) {
        this(subject, content, Importance.MEDIUM);
    }

    /**
     * Create a message with a subject, content, and importance.
     * @param subject the message subject
     * @param content the message content 
     * @param importance the message importance
     */
    public SimpleMessage(final String subject, final Object content, final Importance importance) {
        this.messageSubject = subject;
        this.messageContent = content;
        this.messageImportance = importance;
    }

    /**
     * {@inheritDoc}
     */
    public Object getMessageContent() {
        return messageContent;
    }

    /**
     * {@inheritDoc}
     */
    public Importance getMessageImportance() {
        return messageImportance;
    }

    /**
     * {@inheritDoc}
     */
    public String getSubject() {
        return messageSubject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
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
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final SimpleMessage other = (SimpleMessage) obj;
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
