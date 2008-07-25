package uk.me.gumbley.minimiser.opener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataAccessResourceFailureException;

import uk.me.gumbley.minimiser.opener.OpenerAdapter.ProgressStage;
import uk.me.gumbley.minimiser.persistence.AccessFactory;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabase;
import uk.me.gumbley.minimiser.persistence.PersistenceUnittestCase;


/**
 * Tests the Opener.
 * @author matt
 *
 */
public final class TestOpener extends PersistenceUnittestCase {
    private static final Logger LOGGER = Logger.getLogger(TestOpener.class);
    
    private AccessFactory accessFactory;
    private Opener opener;
    private ProgressRecorder progressRecorder;
    
    /**
     * To record receipt of progress notifications
     * @author matt
     *
     */
    private final class ProgressRecorder {
        private Map<ProgressStage, List<String>> progressReceived = new HashMap<ProgressStage, List<String>>();
        private Set<ProgressStage> assertedStages = new HashSet<ProgressStage>();
        /**
         * Gimme! 
         */
        public ProgressRecorder() {
        }
        
        /**
         * Record progress
         * @param progressStage the stage
         * @param description the description
         */
        public void receiveProgress(final ProgressStage progressStage, final String description) {
            LOGGER.info("Reached stage '" + progressStage + "' text '" + description + "'");
            final List<String> descriptions;
            if (progressReceived.containsKey(progressStage)) {
                descriptions = progressReceived.get(progressStage);
            } else {
                descriptions = new ArrayList<String>();
                progressReceived.put(progressStage, descriptions);
            }
            descriptions.add(description);
        }
        
        /**
         * Obtain the received descriptions for a given stage
         * @param progressStage the stage
         * @return a List of description Strings
         */
        public List<String> getDescriptions(final ProgressStage progressStage) {
            return progressReceived.get(progressStage);
        }
        
        /**
         * We should have received any notifications for the given stage
         * @param progressStage the stage to query
         */
        public void assertProgressWasReceived(final ProgressStage progressStage) {
            assertedStages.add(progressStage);
            Assert.assertTrue(progressReceived.containsKey(progressStage));
        }
        
        /**
         * Assert that all received stages were asserted. 
         */
        public void assertAllProgressReceivedWasAsserted() {
            for (final ProgressStage progressStage : progressReceived.keySet()) {
                Assert.assertTrue("ProgressStage " + progressStage
                        + " was received but was not asserted for",
                        assertedStages.contains(progressStage));
            }
        }
    }

    /**
     * Grab the configured AccessFactory from the Spring App Context, and pass
     * it to a new Opener.
     * TODO not in a unit test!
     */
    @Before
    public void getPrerequisites() {
        accessFactory = getSpringLoader().getBean("accessFactory", AccessFactory.class);
        opener = new Opener(accessFactory);
        progressRecorder = new ProgressRecorder();
    }

    /**
     * Don't allow any progress reports to have their detection missed - their
     * presence should each be asserted.
     */
    @After
    public void checkForUnexpectedProgressReports() {
        progressRecorder.assertAllProgressReceivedWasAsserted();
    }
    
    /**
     * 
     */
    @Test(timeout = 8000)
    public void progressNotificationsOnPlainOpen() {
        LOGGER.info("** progressNotificationsOnPlainOpen");
        final String dbName = "progressplain";
        createDatabaseWithPluggableBehaviourBeforeDeletion(accessFactory, dbName, "", new RunOnCreatedDb() {
            public void runOnCreatedDb(final String dbName, final String dbPassword, final String dbDirPlusDbName) {
                
                final OpenerAdapter openerAdapter = new OpenerAdapter() {

                    public void reportProgress(final ProgressStage progressStage, final String description) {
                        progressRecorder.receiveProgress(progressStage, description);
                    }

                    public String requestPassword() {
                        Assert.fail("Not an encrypted db; password should not have been prompted for");
                        return null;
                    }
                };
                
                // Note that the password isn't passed into the opener, since
                // it's prompted for (and provided by the OpenerAdapter). It's
                // used by createDatabaseWithPluggableBehaviourBeforeDeletion
                // since we may be creating an encrypted database for tests.
                final MiniMiserDatabase database = opener.openDatabase(dbName, dbDirPlusDbName, openerAdapter);
                try {
                    progressRecorder.assertProgressWasReceived(OpenerAdapter.ProgressStage.STARTING);
                    
                    progressRecorder.assertProgressWasReceived(OpenerAdapter.ProgressStage.OPENING);
                    // First time round (which is all we'll have here), expect this.
                    Assert.assertEquals("Opening database", progressRecorder.getDescriptions(OpenerAdapter.ProgressStage.OPENING).get(0));
                    
                    progressRecorder.assertProgressWasReceived(OpenerAdapter.ProgressStage.OPENED);
                    
                    Assert.assertNotNull(database);

                } finally {
                    if (database != null) {
                        database.close();
                    }
                }
            }
        });
    }
    
    /**
     * 
     */
    @Test(timeout = 8000)
    public void progressNotificationsAndCorrectPasswordEnteredOnEncryptedOpen() {
        LOGGER.info("** progressNotificationsAndCorrectPasswordEnteredOnEncryptedOpen");
        final String dbName = "progressenc";
        final String dbPassword = "Squeamish Ossifrage";
        createDatabaseWithPluggableBehaviourBeforeDeletion(accessFactory, dbName, dbPassword, new RunOnCreatedDb() {
            public void runOnCreatedDb(final String dbName, final String dbPassword, final String dbDirPlusDbName) {
                

                final OpenerAdapter openerAdapter = new OpenerAdapter() {

                    public void reportProgress(final ProgressStage progressStage, final String description) {
                        progressRecorder.receiveProgress(progressStage, description);
                    }

                    public String requestPassword() {
                        return dbPassword;
                    }
                };
                
                // Note that the password isn't passed into the opener, since
                // it's prompted for (and provided by the OpenerAdapter). It's
                // used by createDatabaseWithPluggableBehaviourBeforeDeletion
                // since we may be creating an encrypted database for tests.
                final MiniMiserDatabase database = opener.openDatabase(dbName, dbDirPlusDbName, openerAdapter);
                try {
                    progressRecorder.assertProgressWasReceived(OpenerAdapter.ProgressStage.STARTING);
                    
                    progressRecorder.assertProgressWasReceived(OpenerAdapter.ProgressStage.OPENING);

                    Assert.assertEquals(2, progressRecorder.getDescriptions(OpenerAdapter.ProgressStage.OPENING).size());
                    // First time round, expect this.
                    Assert.assertEquals("Opening database", progressRecorder.getDescriptions(OpenerAdapter.ProgressStage.OPENING).get(0));
                    // Then, after the password entry...
                    Assert.assertEquals("Trying to open database", progressRecorder.getDescriptions(OpenerAdapter.ProgressStage.OPENING).get(1));
                    
                    progressRecorder.assertProgressWasReceived(OpenerAdapter.ProgressStage.PASSWORD_REQUIRED);

                    progressRecorder.assertProgressWasReceived(OpenerAdapter.ProgressStage.OPENED);
                    
                    Assert.assertNotNull(database);

                } finally {
                    if (database != null) {
                        database.close();
                    }
                }
            }
        });
    }
    
    /**
     *
     */
    @Test(timeout = 8000)
    public void progressNotificationsAndPasswordEntryAbandonedOnEncryptedOpen() {
        LOGGER.info("** progressNotificationsAndPasswordEntryAbandonedOnEncryptedOpen");
        final String dbName = "progressencabandon";
        final String dbPassword = "Squeamish Ossifrage";
        createDatabaseWithPluggableBehaviourBeforeDeletion(accessFactory, dbName, dbPassword, new RunOnCreatedDb() {
            public void runOnCreatedDb(final String dbName, final String dbPassword, final String dbDirPlusDbName) {
                
                final OpenerAdapter openerAdapter = new OpenerAdapter() {

                    public void reportProgress(final ProgressStage progressStage, final String description) {
                        progressRecorder.receiveProgress(progressStage, description);
                    }

                    public String requestPassword() {
                        return "";
                    }
                };
                
                // Note that the password isn't passed into the opener, since
                // it's prompted for (and provided by the OpenerAdapter). It's
                // used by createDatabaseWithPluggableBehaviourBeforeDeletion
                // since we may be creating an encrypted database for tests.
                final MiniMiserDatabase database = opener.openDatabase(dbName, dbDirPlusDbName, openerAdapter);
                try {
                    progressRecorder.assertProgressWasReceived(OpenerAdapter.ProgressStage.STARTING);
                    
                    progressRecorder.assertProgressWasReceived(OpenerAdapter.ProgressStage.OPENING);

                    Assert.assertEquals(1, progressRecorder.getDescriptions(OpenerAdapter.ProgressStage.OPENING).size());
                    // First time round, expect this.
                    Assert.assertEquals("Opening database", progressRecorder.getDescriptions(OpenerAdapter.ProgressStage.OPENING).get(0));
                    // We shouldn't get a "Trying to open database"
                    
                    progressRecorder.assertProgressWasReceived(OpenerAdapter.ProgressStage.PASSWORD_REQUIRED);

                    progressRecorder.assertProgressWasReceived(OpenerAdapter.ProgressStage.PASSWORD_CANCELLED);

                    Assert.assertNull(database);
                } finally {
                    if (database != null) {
                        database.close();
                    }
                }
            }
        });
    }
    
    /**
     * 
     */
    @Test(timeout = 8000)
    public void progressNotificationsAndCorrectPasswordEnteredSecondTimeOnEncryptedOpen() {
        LOGGER.info("** progressNotificationsAndCorrectPasswordSecondTimeEnteredOnEncryptedOpen");
        final String dbName = "progressenc";
        final String[] dbPasswordAttempts = new String[] {"Squeamish Ossifrage", "Tantric Obstacles"};
        final AtomicInteger passwordAttempt = new AtomicInteger(1);
        createDatabaseWithPluggableBehaviourBeforeDeletion(accessFactory, dbName, dbPasswordAttempts[0], new RunOnCreatedDb() {
            public void runOnCreatedDb(final String dbName, final String dbPassword, final String dbDirPlusDbName) {
                

                final OpenerAdapter openerAdapter = new OpenerAdapter() {

                    public void reportProgress(final ProgressStage progressStage, final String description) {
                        progressRecorder.receiveProgress(progressStage, description);
                    }

                    public String requestPassword() {
                        final int index = passwordAttempt.getAndDecrement();
                        final String password = dbPasswordAttempts[index];
                        LOGGER.info("Trying password '" + password + "'");
                        return password;
                    }
                };
                
                // Note that the password isn't passed into the opener, since
                // it's prompted for (and provided by the OpenerAdapter). It's
                // used by createDatabaseWithPluggableBehaviourBeforeDeletion
                // since we may be creating an encrypted database for tests.
                final MiniMiserDatabase database = opener.openDatabase(dbName, dbDirPlusDbName, openerAdapter);
                try {
                    progressRecorder.assertProgressWasReceived(OpenerAdapter.ProgressStage.STARTING);
                    
                    progressRecorder.assertProgressWasReceived(OpenerAdapter.ProgressStage.OPENING);

                    Assert.assertEquals(3, progressRecorder.getDescriptions(OpenerAdapter.ProgressStage.OPENING).size());
                    // First time round, expect this.
                    Assert.assertEquals("Opening database", progressRecorder.getDescriptions(OpenerAdapter.ProgressStage.OPENING).get(0));
                    // Then, after the password entry...
                    Assert.assertEquals("Trying to open database", progressRecorder.getDescriptions(OpenerAdapter.ProgressStage.OPENING).get(1));
                    // Then, after this fails the first time, another password entry...
                    Assert.assertEquals("Trying to open database", progressRecorder.getDescriptions(OpenerAdapter.ProgressStage.OPENING).get(2));
                    
                    Assert.assertEquals(2, progressRecorder.getDescriptions(OpenerAdapter.ProgressStage.PASSWORD_REQUIRED).size());
                    progressRecorder.assertProgressWasReceived(OpenerAdapter.ProgressStage.PASSWORD_REQUIRED);

                    progressRecorder.assertProgressWasReceived(OpenerAdapter.ProgressStage.OPENED);
                    
                    Assert.assertNotNull(database);
                } finally {
                    if (database != null) {
                        database.close();
                    }
                }
            }
        });
    }
    
    /**
     * 
     */
    @Test
    public void progressNotificationsExceptionOnOpenOfNonExistantWithNoPassword() {
        LOGGER.info("** progressNotificationsExceptionOnOpenOfNonExistantWithNoPassword");
        final String dbName = "wah";
        final String dbDirPlusDbName = getAbsoluteDatabaseDirectory(dbName);
        final OpenerAdapter openerAdapter = new OpenerAdapter() {

            public void reportProgress(final ProgressStage progressStage, final String description) {
                progressRecorder.receiveProgress(progressStage, description);
            }

            public String requestPassword() {
                Assert.fail("Not an encrypted db; password should not have been prompted for");
                return null;
            }
        };
        try {
            opener.openDatabase(dbName, dbDirPlusDbName, openerAdapter);
            Assert.fail("Should not have been able to open a nonexistant database");
        } catch (final DataAccessResourceFailureException darfe) {
            LOGGER.info("Correctly caught not DataAccessResourceFailureException:" + darfe.getMessage());
        } catch (final Throwable t) {
            Assert.fail("Should not have caught a " + t.getClass().getName() + " opening a nonexistant database");
        }
        progressRecorder.assertProgressWasReceived(OpenerAdapter.ProgressStage.STARTING);
        
        progressRecorder.assertProgressWasReceived(OpenerAdapter.ProgressStage.OPENING);

        progressRecorder.assertProgressWasReceived(OpenerAdapter.ProgressStage.NOT_PRESENT);
    }
    

    /**
     * 
     */
    @Test
    public void progressNotificationsExceptionOnOpenOfNonExistantWithPassword() {
        LOGGER.info("** progressNotificationsExceptionOnOpenOfNonExistantWithPassword");
    }
}
