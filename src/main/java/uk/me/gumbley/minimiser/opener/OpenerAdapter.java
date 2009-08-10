package uk.me.gumbley.minimiser.opener;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;

/**
 * An Opener uses an OpenerAdapter to inform the user of:
 * <ul>
 * <li> progress during the open
 * <li> to request any password
 * <li> to request confirmation for migration
 * <li> to inform the user of any failures.
 * </ul>
 * The start and end of an open operation can also be signalled, e.g. for
 * setting and clearing the hourglass cursor.
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
        private final int enumValue;
        private final String name;
        private ProgressStage(final int val, final String psName) {
            enumValue = val;
            name = psName;
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
            return 4;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return name;
        }
        
        /**
         * The open operation is starting. Sent almost immediately to give some
         * immediate feedback.
         */
        public static final ProgressStage STARTING = new ProgressStage(0, "STARTING");
        /**
         * Sent immediately prior to opening the database. 
         */
        public static final ProgressStage OPENING = new ProgressStage(1, "OPENING");

        /**
         * Sent before the password is requested from the adapter. 
         */
        public static final ProgressStage PASSWORD_REQUIRED = new ProgressStage(2, "PASSWORD_REQUIRED"); 

        /**
         * Sent if the database requires migration and the user should be prompted by the adapter. 
         */
        public static final ProgressStage MIGRATION_REQUIRED = new ProgressStage(3, "MIGRATION_REQUIRED"); 
        /**
         * Sent during migration if the user allowed it. 
         */
        public static final ProgressStage MIGRATING = new ProgressStage(3, "MIGRATING"); 

        /**
         * Sent upon successful open. 
         */
        public static final ProgressStage OPENED = new ProgressStage(4, "OPENED");
        /**
         * The user cancelled the password entry on an encrypted database. 
         */
        public static final ProgressStage PASSWORD_CANCELLED = new ProgressStage(4, "PASSWORD_CANCELLED");
        /**
         * The database is not present. 
         */
        public static final ProgressStage NOT_PRESENT = new ProgressStage(4, "NOT_PRESENT");
        /**
         * Failed to open for a serious reason 
         */
        public static final ProgressStage OPEN_FAILED = new ProgressStage(4, "OPEN_FAILED");
    };

    /**
     * The opening operation is starting. Always called before any progress.
     */
    void startOpening();
    
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
    
    /**
     * Report to the user that the database could not be found.
     * @param exception the not found exception that has occurred
     */
    void databaseNotFound(DataAccessResourceFailureException exception);
    
    /**
     * Report to the user that a serious problem has occurred.
     * Note that this should be treated as a very bad problem (database
     * corruption?) - so log a problem report.
     * @param exception the data access exception that has occurred.
     */
    void seriousProblemOccurred(DataAccessException exception);

    /**
     * The opening operation has completed. Always called after all progress.
     */
    void stopOpening();
}
