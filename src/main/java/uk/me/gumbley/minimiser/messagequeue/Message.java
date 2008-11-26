package uk.me.gumbley.minimiser.messagequeue;


/**
 * A non-persistent, asynchronously-delivered user message.
 * Messages have subjects, an indication of their importance, and content.
 * @author matt
 *
 */
public interface Message {
    
    /**
     * What is the subject of this message?
     * @return the message subject
     */
    String getSubject();
    
    enum Importance {
        /**
         * A message with low importance 
         */
        LOW,
        /**
         * A message with medium importance
         */
        MEDIUM,
        /**
         * A message with high importance
         */
        HIGH
    };
    
    /**
     * How important is this message?
     * @return the message importance
     */
    Importance getMessageImportance();
    
    /**
     * What is the content of this message?
     * @return an Object containing the content; may be a String, may be a
     * Component.
     */
    Object getMessageContent();
    
    /**
     * Called when the message queue removes this message, so it can perform
     * any final activities (e.g. storing DSTA flags, for SimpleDSTAMessages).
     */
    void onRemoval();
}
