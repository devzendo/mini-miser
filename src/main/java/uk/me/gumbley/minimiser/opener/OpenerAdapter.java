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
        STARTING, // start 0
        /**
         * Sent immediately prior to opening the database. 
         */
        OPENING, // 1

        /**
         * Sent before the password is requested from the adapter. 
         */
        PASSWORD_REQUIRED, // 2 

        /**
         * Sent upon successful open. 
         */
        OPENED, // 3
        /**
         * The user cancelled the password entry on an encrypted database. 
         */
        PASSWORD_CANCELLED, // 3
        /**
         * The database is not present. 
         */
        NOT_PRESENT, // 3
        /**
         * Failed to open for a serious reason 
         */
        OPEN_FAILED // 3
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
