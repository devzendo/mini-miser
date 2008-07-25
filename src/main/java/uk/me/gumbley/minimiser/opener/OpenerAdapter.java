package uk.me.gumbley.minimiser.opener;

/**
 * An Opener uses an OpenerAdapter to inform the user of progress during the
 * open, and to request any password, confirmation for migration, and to
 * inform the user of any failures.
 *  
 * @author matt
 *
 */
public interface OpenerAdapter {

    /**
     * Sent by the progress notifications.
     * @author matt
     *
     */
    enum ProgressStage {
        /**
         * The open operation is starting. Sent almost immediately to give some
         * immediate feedback.
         */
        STARTING,
        /**
         * Sent immediately prior to opening the database. 
         */
        OPENING, 
        /**
         * Sent upon successful open. 
         */
        OPENED, 
        /**
         * Sent before the password is requested from the adapter. 
         */
        PASSWORD_REQUIRED, 
        /**
         * The user cancelled the password entry on an encrypted database. 
         */
        PASSWORD_CANCELLED, 
        /**
         * The database is not present. 
         */
        NOT_PRESENT 
    };
    
    /**
     * Report progress of the open operation to the user interface
     * @param progressStage the stage we have reached
     * @param description a short text to show the user
     */
    void reportProgress(ProgressStage progressStage, String description);
    
    /**
     * The database is encrypted, and the password must be prompted for and
     * returned.
     * @return the password, or null if the user cancels the password entry.
     */
    String requestPassword();
}
