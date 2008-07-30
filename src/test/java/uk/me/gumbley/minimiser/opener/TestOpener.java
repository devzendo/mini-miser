package uk.me.gumbley.minimiser.opener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.opener.OpenerAdapter.ProgressStage;
import uk.me.gumbley.minimiser.openlist.DatabaseEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseOpenedEvent;
import uk.me.gumbley.minimiser.openlist.DatabaseSwitchedEvent;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
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
    private OpenDatabaseList openDatabaseList;
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
        private boolean startReceived = false;
        private boolean stopReceived = false;
        private boolean illegalProgressBeforeStart = false;
        private boolean illegalProgressAfterStop = false;
        private boolean notFoundReceived = false;
        private boolean seriousProblemReceived = false;
        
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
            if (!startReceived) {
                illegalProgressBeforeStart = true;
            }
            if (stopReceived) {
                illegalProgressAfterStop = true;
            }
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
        
        public void assertStopAndStartReceived() {
            Assert.assertTrue("start() was not called on the OpenerAdapter", startReceived);
            Assert.assertTrue("stop() was not called on the OpenerAdapter", stopReceived);
            if (illegalProgressBeforeStart) {
                Assert.fail("progress received but start() has not yet been called");
            }
            if (illegalProgressAfterStop) {
                Assert.fail("progress received after stop() has been called");
            }
        }

        public void startReceived() {
            startReceived = true;
        }

        public void stopReceived() {
            stopReceived = true;
        }

        public void notFoundReceived() {
            notFoundReceived = true;
        }
        
        public void seriousProblemReceived() {
            seriousProblemReceived = true;
        }

        public void assertNotFoundReceived() {
            Assert.assertTrue("notFound should have been called but was not", notFoundReceived);
        }

        public void assertSeriousProblemReceived() {
            Assert.assertTrue("seriousProblem should have been called but was not", seriousProblemReceived);
        }

        public void assertNotFoundNotReceived() {
            Assert.assertFalse("notFound should not have been called but was", notFoundReceived);
        }

        public void assertSeriousProblemNotReceived() {
            Assert.assertFalse("seriousProblem should not have been called but was", seriousProblemReceived);
        }
    }

    /**
     * Grab the configured AccessFactory from the Spring App Context, and pass
     * it to a new Opener.
     */
    @Before
    public void getPrerequisites() {
        accessFactory = getSpringLoader().getBean("accessFactory", AccessFactory.class);
        openDatabaseList = new OpenDatabaseList();
        opener = new Opener(accessFactory, openDatabaseList);
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
     * start() and stop() methods must also have been called.
     */
    @After
    public void checkForStartAndStop() {
        progressRecorder.assertStopAndStartReceived();
    }
    
    /**
     * Does most of the things a test OpenerAdapter needs - except supplying
     * a password
     * @author matt
     *
     */
    private abstract class AbstractTestOpenerAdapter implements OpenerAdapter {
        private final ProgressRecorder recorder;

        /**
         * Make me one!
         * @param progRecorder the recorder to notify of events
         */
        public AbstractTestOpenerAdapter(final ProgressRecorder progRecorder) {
            recorder = progRecorder;
        }

        public final void reportProgress(final ProgressStage progressStage, final String description) {
            recorder.receiveProgress(progressStage, description);
        }

        public final void startOpening() {
            recorder.startReceived();
        }
        
        public final void databaseNotFound(final DataAccessResourceFailureException darfe) {
            LOGGER.warn("Database not found: "  + darfe.getMessage());
            recorder.notFoundReceived();
        }
        
        public final void seriousProblemOccurred(final DataAccessException dae) {
            LOGGER.warn("Serious problem detected: " + dae.getMessage());
            recorder.seriousProblemReceived();
        }

        public final void stopOpening() {
            recorder.stopReceived();
        }
    };
    /**
     * 
     */
    @Test(timeout = 8000)
    public void progressNotificationsOnPlainOpen() {
        LOGGER.info("** progressNotificationsOnPlainOpen");
        final String dbName = "progressplain";
        createDatabaseWithPluggableBehaviourBeforeDeletion(accessFactory, dbName, "", new RunOnCreatedDb() {
            @SuppressWarnings("unchecked")
            public void runOnCreatedDb(final String dbName, final String dbPassword, final String dbDirPlusDbName) {
                
                final OpenerAdapter openerAdapter = new AbstractTestOpenerAdapter(progressRecorder) {
                    public String requestPassword() {
                        Assert.fail("Not an encrypted db; password should not have been prompted for");
                        return null;
                    }
                };
                final Observer<DatabaseEvent> obs = EasyMock.createStrictMock(Observer.class);
                obs.eventOccurred(EasyMock.eq(new DatabaseOpenedEvent(dbName, dbDirPlusDbName)));
                obs.eventOccurred(EasyMock.eq(new DatabaseSwitchedEvent(dbName)));
                EasyMock.replay(obs);

                openDatabaseList.addDatabaseEventObserver(obs);

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
                    
                    progressRecorder.assertNotFoundNotReceived();
                    progressRecorder.assertSeriousProblemNotReceived();
                    
                    EasyMock.verify(obs);
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
            @SuppressWarnings("unchecked")
            public void runOnCreatedDb(final String dbName, final String dbPassword, final String dbDirPlusDbName) {

                final OpenerAdapter openerAdapter = new AbstractTestOpenerAdapter(progressRecorder) {
                    public String requestPassword() {
                        return dbPassword;
                    }
                };
                final Observer<DatabaseEvent> obs = EasyMock.createStrictMock(Observer.class);
                obs.eventOccurred(EasyMock.eq(new DatabaseOpenedEvent(dbName, dbDirPlusDbName)));
                obs.eventOccurred(EasyMock.eq(new DatabaseSwitchedEvent(dbName)));
                EasyMock.replay(obs);

                openDatabaseList.addDatabaseEventObserver(obs);
                
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
                    
                    progressRecorder.assertNotFoundNotReceived();
                    progressRecorder.assertSeriousProblemNotReceived();

                    EasyMock.verify(obs);
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
            @SuppressWarnings("unchecked")
            public void runOnCreatedDb(final String dbName, final String dbPassword, final String dbDirPlusDbName) {
                
                final OpenerAdapter openerAdapter = new AbstractTestOpenerAdapter(progressRecorder) {
                    public String requestPassword() {
                        return "";
                    }
                };
                final Observer<DatabaseEvent> obs = EasyMock.createStrictMock(Observer.class);
                EasyMock.replay(obs);

                openDatabaseList.addDatabaseEventObserver(obs);
                                
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
                    
                    progressRecorder.assertNotFoundNotReceived();
                    progressRecorder.assertSeriousProblemNotReceived();

                    EasyMock.verify(obs);
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
            @SuppressWarnings("unchecked")
            public void runOnCreatedDb(final String dbName, final String dbPassword, final String dbDirPlusDbName) {
                
                final OpenerAdapter openerAdapter = new AbstractTestOpenerAdapter(progressRecorder) {
                    public String requestPassword() {
                        final int index = passwordAttempt.getAndDecrement();
                        final String password = dbPasswordAttempts[index];
                        LOGGER.info("Trying password '" + password + "'");
                        return password;
                    }
                };
                final Observer<DatabaseEvent> obs = EasyMock.createStrictMock(Observer.class);
                obs.eventOccurred(EasyMock.eq(new DatabaseOpenedEvent(dbName, dbDirPlusDbName)));
                obs.eventOccurred(EasyMock.eq(new DatabaseSwitchedEvent(dbName)));
                EasyMock.replay(obs);

                openDatabaseList.addDatabaseEventObserver(obs);
                
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
                    
                    progressRecorder.assertNotFoundNotReceived();
                    progressRecorder.assertSeriousProblemNotReceived();

                    EasyMock.verify(obs);
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
    @SuppressWarnings("unchecked")
    @Test
    public void progressNotificationsExceptionOnOpenOfNonExistant() {
        LOGGER.info("** progressNotificationsExceptionOnOpenOfNonExistant");
        final String dbName = "wah";
        final String dbDirPlusDbName = getAbsoluteDatabaseDirectory(dbName);
        final OpenerAdapter openerAdapter = new AbstractTestOpenerAdapter(progressRecorder) {
            public String requestPassword() {
                Assert.fail("Not an encrypted db; password should not have been prompted for");
                return null;
            }
        };
        final Observer<DatabaseEvent> obs = EasyMock.createStrictMock(Observer.class);
        EasyMock.replay(obs);

        Assert.assertNull(opener.openDatabase(dbName, dbDirPlusDbName, openerAdapter));
        progressRecorder.assertProgressWasReceived(OpenerAdapter.ProgressStage.STARTING);
        
        progressRecorder.assertProgressWasReceived(OpenerAdapter.ProgressStage.OPENING);

        progressRecorder.assertProgressWasReceived(OpenerAdapter.ProgressStage.NOT_PRESENT);
        
        progressRecorder.assertNotFoundReceived();
        progressRecorder.assertSeriousProblemNotReceived();

        EasyMock.verify(obs);
    }
    
    /**
     * 
     */
    @Test
    public void progressNotificationsExceptionOnOpenOfCorrupt() {
        LOGGER.info("** progressNotificationsExceptionOnOpenOfCorrupt");
        final String dbName = "corrupt";
        createDatabaseWithPluggableBehaviourBeforeDeletion(accessFactory, dbName, "", new RunOnCreatedDb() {
            @SuppressWarnings("unchecked")
            public void runOnCreatedDb(final String dbName, final String dbPassword, final String dbDirPlusDbName) {

                // Corrupt the database
                final File dbFile = new File(getAbsoluteDatabaseDirectory(dbName) + ".data.db");
                LOGGER.info("data file is " + dbFile.getAbsolutePath());
                Assert.assertTrue(dbFile.exists());
                corruptFile(dbFile);

                final OpenerAdapter openerAdapter = new AbstractTestOpenerAdapter(progressRecorder) {
                    public String requestPassword() {
                        Assert.fail("Not an encrypted db; password should not have been prompted for");
                        return null;
                    }
                };
                final Observer<DatabaseEvent> obs = EasyMock.createStrictMock(Observer.class);
                EasyMock.replay(obs);

                Assert.assertNull(opener.openDatabase(dbName, dbDirPlusDbName, openerAdapter));

                progressRecorder.assertProgressWasReceived(OpenerAdapter.ProgressStage.STARTING);
                
                progressRecorder.assertProgressWasReceived(OpenerAdapter.ProgressStage.OPENING);
                // First time round (which is all we'll have here), expect this.
                Assert.assertEquals("Opening database", progressRecorder.getDescriptions(OpenerAdapter.ProgressStage.OPENING).get(0));
                
                progressRecorder.assertProgressWasReceived(OpenerAdapter.ProgressStage.OPEN_FAILED);
                
                
                progressRecorder.assertNotFoundNotReceived();
                progressRecorder.assertSeriousProblemReceived();

                EasyMock.verify(obs);
            }
        });
    }

    private void corruptFile(final File dbFile) {
        RandomAccessFile raf;
        try {
            raf = new RandomAccessFile(dbFile, "rw");
            raf.seek(1024);
            raf.writeChars("Let's write all over the database, to see if it'll fail to open!");
            raf.close();
        } catch (final FileNotFoundException e) {
            Assert.fail("Got a file not found!: " + e.getMessage());
        } catch (final IOException e) {
            Assert.fail("Got an IOexception!: " + e.getMessage());
        }
    }
}
