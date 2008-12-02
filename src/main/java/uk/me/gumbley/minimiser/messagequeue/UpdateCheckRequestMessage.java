package uk.me.gumbley.minimiser.messagequeue;



/**
 * A message requesting permission for update checks. 
 * 
 * @author matt
 *
 */
public final class UpdateCheckRequestMessage extends AbstractMessage {
    private boolean checkAllowed = false;

    /**
     * @param subject the message subject
     * @param content the message content
     */
    public UpdateCheckRequestMessage(final String subject, final Object content) {
        super(subject, content);
    }

    /**
     * @param subject the message subject
     * @param content the message content
     * @param importance the importance of the message
     */
    public UpdateCheckRequestMessage(final String subject, final Object content, final Importance importance) {
        super(subject, content, importance);
    }

    /**
     * Store the user's choce for the updates allowed flag as uncommitted.  
     * @param allowed true iff checks allowed
     */
    public void setCheckAllowed(final boolean allowed) {
        this.checkAllowed = allowed;
    }

    /**
     * Obtain the user's choice for the updates allowed flag
     * @return true iff updates allowed
     */
    public boolean isCheckAllowed() {
        return checkAllowed;
    }
}
