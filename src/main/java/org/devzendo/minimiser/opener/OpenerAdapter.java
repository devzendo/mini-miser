/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devzendo.minimiser.opener;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
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
    /**
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
            return 6;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return name;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object obj) {
            if (null == obj) {
                return false;
            }
            if (!(obj instanceof ProgressStage)) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            final ProgressStage castObj = (ProgressStage) obj;
            return new EqualsBuilder()
                .append(this.name, castObj.name)
                .append(this.enumValue, castObj.enumValue)
                .isEquals();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            // pick 2 hard-coded, odd, >0 ints as args
            return new HashCodeBuilder(1, 31)
                .append(this.name)
                .append(this.enumValue)
                .toHashCode();
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
        public static final ProgressStage MIGRATING = new ProgressStage(4, "MIGRATING"); 
        
        /**
         * Sent after successful migration 
         */
        public static final ProgressStage MIGRATED = new ProgressStage(5, "MIGRATED"); 

        // End states ---------------------------------------------
        
        /**
         * Sent upon successful open. 
         */
        public static final ProgressStage OPENED = new ProgressStage(6, "OPENED");

        /**
         * The user cancelled the password entry on an encrypted database. 
         */
        public static final ProgressStage PASSWORD_CANCELLED = new ProgressStage(6, "PASSWORD_CANCELLED");
        
        /**
         * The user rejected the migration request on an old database. 
         */
        public static final ProgressStage MIGRATION_REJECTED = new ProgressStage(6, "MIGRATION_REJECTED");

        /**
         * The migration cannot be done as this database is at a
         * more recent version than the plugins support. After
         * receiving this ProgressStage, you will receive a
         * migrationNotPossible() call.
         */
        public static final ProgressStage MIGRATION_NOT_POSSIBLE = new ProgressStage(6, "MIGRATION_NOT_POSSIBLE");

        /**
         * The migration cannot be done as this database was
         * created by some other application (the application
         * plugin declared in the database does not match the
         * runtime application plugin). After
         * receiving this ProgressStage, you will receive a
         * createdByOtherApplication() call.
         */
        public static final ProgressStage OTHER_APPLICATION_DATABASE = new ProgressStage(6, "OTHER_APPLICATION_DATABASE");

        /**
         * The open cannot be done since there is no application
         * plugin available, so the opener cannot check whether
         * this database was created by that application. After
         * receiving this ProgressStage, you will receive a
         * noApplicationPluginAvailable call.
         */
        public static final ProgressStage NO_APPLICATION_PLUGIN = new ProgressStage(6, "NO_APPLICATION_PLUGIN");
        
        /**
         * The migration failed and its effects have been rolled
         * back (as far is as practical, given H2's auto-commit
         * of DML when DDL is executed - ignoring the context
         * of any outstanding transaction. After receiving this
         * ProgressStage, you will receive a migrationFailed()
         * call.
         */
        public static final ProgressStage MIGRATION_FAILED = new ProgressStage(6, "MIGRATION_FAILED");

        /**
         * The database is not present. 
         */
        public static final ProgressStage NOT_PRESENT = new ProgressStage(6, "NOT_PRESENT");
        
        /**
         * Failed to open for a serious reason 
         */
        public static final ProgressStage OPEN_FAILED = new ProgressStage(6, "OPEN_FAILED");
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
     * This is an old database, and must be migrated before it can
     * be used. Prompt the user to accept or reject the migration.
     * If they reject, the database cannot be opened. If they
     * accept, it will be migrated, and then opened.
     * @return true if the migration is to be accepted, false to
     * reject.
     */
    boolean requestMigration();

    /**
     * Report to the user the migration cannot be done as this
     * database is at a more recent version than the plugins
     * support.
     * TODO: possibly add the versions of the plugins that are
     * present in the database?
     */
    void migrationNotPossible();

    /**
     * The migration failed and its effects have been rolled
     * back (as far is as practical, given H2's auto-commit
     * of DML when DDL is executed - ignoring the context
     * of any outstanding transaction. 
     * @param exception the data access exception that has occurred.
     */
    void migrationFailed(DataAccessException exception);

    /**
     * The open failed as this database was created by some other
     * application.
     */
    void createdByOtherApplication();
    
    /**
     * The open failed since the check for creation by the current
     * application could not be done since there is no application
     * plugin loaded.
     */
    void noApplicationPluginAvailable();
    
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
