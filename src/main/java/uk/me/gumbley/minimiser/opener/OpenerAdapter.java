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
     * Sent by the progress notifications, this denotes a stage in the open
     * operation.
     * @author matt
     *
     */
    public final class ProgressStage {
        private int enumValue;
        private ProgressStage(final int val) {
            enumValue = val;
        }
        
        /**
         * What is the value of this stage in the overall progress?
         * Progress is measured as starting from 0, and ending at getMaximumValue().
         * @return the value of the stage
         */
        public int getValue() {
            return enumValue;
        }

        /**
         * How many steps are there in total?
         * @return the maximum value of a ProgressStage 
         */
        public int getMaximumValue() {
            return 3;
        }
        
        /**
         * The open operation is starting. Sent almost immediately to give some
         * immediate feedback.
         */
        public static final ProgressStage STARTING = new ProgressStage(0);
        /**
         * Sent immediately prior to opening the database. 
         */
        public static final ProgressStage OPENING = new ProgressStage(1);

        /**
         * Sent before the password is requested from the adapter. 
         */
        public static final ProgressStage PASSWORD_REQUIRED = new ProgressStage(2); 

        /**
         * Sent upon successful open. 
         */
        public static final ProgressStage OPENED = new ProgressStage(3);
        /**
         * The user cancelled the password entry on an encrypted database. 
         */
        public static final ProgressStage PASSWORD_CANCELLED = new ProgressStage(3);
        /**
         * The database is not present. 
         */
        public static final ProgressStage NOT_PRESENT = new ProgressStage(3);
        /**
         * Failed to open for a serious reason 
         */
        public static final ProgressStage OPEN_FAILED = new ProgressStage(3);
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
