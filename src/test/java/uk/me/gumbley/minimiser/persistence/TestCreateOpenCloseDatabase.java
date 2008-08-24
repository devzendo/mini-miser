package uk.me.gumbley.minimiser.persistence;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.closer.Closer;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor.AttributeIdentifier;
import uk.me.gumbley.minimiser.persistence.domain.CurrentSchemaVersion;
import uk.me.gumbley.minimiser.persistence.domain.Version;
import uk.me.gumbley.minimiser.persistence.domain.VersionableEntity;
import uk.me.gumbley.minimiser.version.AppVersion;


/**
 * Basic tests for creating / opening / closing databases.
 * 
 * @author matt
 *
 */
public final class TestCreateOpenCloseDatabase extends PersistenceUnittestCase {
    private static final String SQUEAMISH_OSSIFRAGE = "Squeamish Ossifrage";

    private static final Logger LOGGER = Logger
            .getLogger(TestCreateOpenCloseDatabase.class);
    
    private AccessFactory accessFactory;
    
    /**
     * Grab the configured AccessFactory from the Spring App Context.
     * TODO not in a unit test!
     */
    @Before
    public void getAccessFactory() {
        accessFactory = getSpringLoader().getBean("accessFactory", AccessFactory.class);
    }
    
    // OPEN TESTS --------------------------------------------------------------
    
    /**
     * Null db path is bad
     */
    @Test(expected = DataAccessResourceFailureException.class)
    public void testNullDatabaseThrowsUpOnOpen() {
        accessFactory.openDatabase(null, "");
    }
    
    /**
     * Empty db path is bad
     */
    @Test(expected = DataAccessResourceFailureException.class)
    public void testEmptyDatabaseThrowsUpOnOpen() {
        accessFactory.openDatabase("", "");
    }
    
    /**
     * Attempts to open a nonexistant database should throw.
     */
    @Test(expected = DataAccessResourceFailureException.class)
    public void testNonExistantDatabaseThrowsUpOnOpen() {
        LOGGER.info("*** testNonExistantDatabaseThrowsUpOnOpen");
        final String dbDir = getAbsoluteDatabaseDirectory("nonexistant");
        Assert.assertFalse(new File(dbDir).exists());
        accessFactory.openDatabase(dbDir, null);
    }

    /**
     * Creates plaintext db, then tries to open it, verifying that it can.
     */
    @Test
    public void testGoodPlaintextDatabaseCanBeOpened() {
        LOGGER.info("*** testGoodPlaintextDatabaseCanBeOpened start");
        final String dbName = "testopen";
        createDatabaseWithPluggableBehaviourBeforeDeletion(accessFactory, dbName, "", new RunOnCreatedDb() {
            public void runOnCreatedDb(final String dbName, final String dbPassword, final String dbDirPlusDbName) {
                LOGGER.info("... re-opening");
                final MiniMiserDatabase openedDatabase = accessFactory.openDatabase(dbDirPlusDbName, dbPassword);
                try {
                    assertDatabaseShouldBeOpen(dbName);
                    Assert.assertFalse(openedDatabase.isClosed());
                    Assert.assertNotNull(openedDatabase);
                    LOGGER.info("... we opened the database!");
                    checkDatabaseInvariants(dbName, openedDatabase, false, false);
                } finally {
                    LOGGER.info("... re-closing");
                    openedDatabase.close();
                    Assert.assertTrue(openedDatabase.isClosed());
                }
            }
            
        });
    }

    /**
     * Creates encrypted db, then tries to open it with the creation password,
     * verifying that it can.
     */
    @Test
    public void testGoodEncryptedDatabaseCanBeOpened() {
        LOGGER.info("*** testGoodEncryptedDatabaseCanBeOpened start");
        final String dbName = "testopenenc";
        final String dbPassword = "Qwerty123";
        createDatabaseWithPluggableBehaviourBeforeDeletion(accessFactory, dbName, dbPassword, new RunOnCreatedDb() {
            public void runOnCreatedDb(final String dbName, final String dbPassword, final String dbDirPlusDbName) {
                LOGGER.info("... re-opening");
                final MiniMiserDatabase openedDatabase = accessFactory.openDatabase(dbDirPlusDbName, dbPassword);
                try {
                    assertDatabaseShouldBeOpen(dbName);
                    Assert.assertNotNull(openedDatabase);
                    LOGGER.info("... we opened the database!");
                    checkDatabaseInvariants(dbName, openedDatabase, true, false);
                } finally {
                    LOGGER.info("... re-closing");
                    openedDatabase.close();
                }
            }
            
        });
    }

    /**
     * Creates encrypted db, then tries to open it with another password,
     * verifying that it can't.
     */
    @Test
    public void testGoodEncryptedDatabaseCantBeOpenedWithOtherPassword() {
        LOGGER.info("*** testGoodEncryptedDatabaseCantBeOpenedWithOtherPassword start");
        final String dbName = "testopenenc";
        final String dbCreationPassword = "Qwerty123";
        final String dbEvilHackerPassword = "Fossi11ized";
        createDatabaseWithPluggableBehaviourBeforeDeletion(accessFactory, dbName, dbCreationPassword, new RunOnCreatedDb() {
            public void runOnCreatedDb(final String dbName, final String dbPassword, final String dbDirPlusDbName) {
                LOGGER.info("... re-opening");
                MiniMiserDatabase openedDatabase = null;
                boolean caughtBPE = false;
                try {
                    openedDatabase = accessFactory.openDatabase(dbDirPlusDbName, dbEvilHackerPassword);
                    Assert.fail("Should not have been able to open an encrypted database with some other password");
                } catch (final BadPasswordException bpe) {
                    LOGGER.info("Correctly caught bad password exception: " + bpe.getMessage());
                    caughtBPE = true;
                } catch (final Throwable t) {
                    LOGGER.warn("Caught unexpected " + t.getClass().getName(), t);
                    Assert.fail("Should not have caught some unknown throwable");
                } finally {
                    LOGGER.info("... re-closing");
                    assertDatabaseShouldBeClosed(dbName);
                    Assert.assertNull(openedDatabase);
                    Assert.assertTrue("Did not catch a bad password exception", caughtBPE);
                }
            }
            
        });
    }

    /**
     * Creates encrypted db, then tries to open it with an empty password,
     * verifying that it can't.
     */
    @Test
    public void testGoodEncryptedDatabaseCantBeOpenedWithEmptyPassword() {
        LOGGER.info("*** testGoodEncryptedDatabaseCantBeOpenedWithEmptyPassword start");
        final String dbName = "testopenenc";
        final String dbCreationPassword = "Qwerty123";
        final String dbEvilHackerPassword = "";
        createDatabaseWithPluggableBehaviourBeforeDeletion(accessFactory, dbName, dbCreationPassword, new RunOnCreatedDb() {
            public void runOnCreatedDb(final String dbName, final String dbPassword, final String dbDirPlusDbName) {
                LOGGER.info("... re-opening");
                MiniMiserDatabase openedDatabase = null;
                boolean caughtBPE = false;
                try {
                    openedDatabase = accessFactory.openDatabase(dbDirPlusDbName, dbEvilHackerPassword);
                    Assert.fail("Should not have been able to open an encrypted database with an empty password");
                } catch (final BadPasswordException bpe) {
                    LOGGER.info("Correctly caught bad password exception: " + bpe.getMessage());
                    caughtBPE = true;
                } catch (final Throwable t) {
                    LOGGER.warn("Caught unexpected " + t.getClass().getName(), t);
                    Assert.fail("Should not have caught some unknown throwable");
                } finally {
                    LOGGER.info("... re-closing");
                    assertDatabaseShouldBeClosed(dbName);
                    Assert.assertNull(openedDatabase);
                    Assert.assertTrue("Did not catch a bad password exception", caughtBPE);
                }
            }
            
        });
    }

    /**
     * Null db path is bad
     */
    @Test(expected = DataAccessResourceFailureException.class)
    public void testNullDatabaseThrowsUpOnCreate() {
        accessFactory.createDatabase(null, "");
    }
    
    /**
     * Empty db path is bad
     */
    @Test(expected = DataAccessResourceFailureException.class)
    public void testEmptyDatabaseThrowsUpOnCreate() {
        accessFactory.createDatabase("", "");
    }
    
    /**
     * Test creation of a non-encrypted database
     */
    @Test
    public void testCreatePlaintextDatabase() {
        LOGGER.info("*** testCreatePlaintextDatabase start");
        final String dbName = "testcreate";
        // should we be able to create this db?
        // TODO my assumption about the databse directory + dbname was wrong.
        // I thought it'd create a dir for the db, but dbName is just the
        // prefix of the files in the parent dir
        final String dbDirPlusDbName = getAbsoluteDatabaseDirectory(dbName);
        LOGGER.info(String.format("... dbDirPlusDbName = %s", dbDirPlusDbName));
        // create it...
        LOGGER.info("... creating");
        final MiniMiserDatabase mmData = accessFactory.createDatabase(dbDirPlusDbName, "");
        final boolean shouldBeRandom = false;
        checkDatabaseInvariants(dbName, mmData, shouldBeRandom, true);
        LOGGER.info("*** testCreatePlaintextDatabase done");
    }

    /**
     * Test creation of a non-encrypted database, listening for progress events
     */
    @Test
    public void testCreatePlaintextDatabaseWithListener() {
        LOGGER.info("*** testCreatePlaintextDatabaseWithListener start");
        final AtomicInteger count = new AtomicInteger(0);
        final Observer<PersistenceObservableEvent> observer = new Observer<PersistenceObservableEvent>() {
            public void eventOccurred(final PersistenceObservableEvent observableEvent) {
                LOGGER.info("Progress: " + observableEvent.getDescription());
                count.incrementAndGet();
            }
        };  
        final String dbName = "testcreate";
        final String dbDirPlusDbName = getAbsoluteDatabaseDirectory(dbName);
        final MiniMiserDatabase mmData = accessFactory.createDatabase(dbDirPlusDbName, "", observer);
        final boolean shouldBeRandom = false;
        checkDatabaseInvariants(dbName, mmData, shouldBeRandom, true);
        final int numberOfDatabaseCreationSteps = accessFactory.getNumberOfDatabaseCreationSteps();
        Assert.assertTrue(count.get() == numberOfDatabaseCreationSteps);
        LOGGER.info("Database creation steps: currently " + count.get());
        LOGGER.info("*** testCreatePlaintextDatabaseWithListener done");
    }
    
    private void checkDatabaseInvariants(final String dbName,
            final MiniMiserDatabase mmData, final boolean shouldBeRandom,
            final boolean doDeleteAndClosureCheck) {
        try {
            LOGGER.info("... created");
            // was something created?
            final int numberOfFilesInDatabaseDirectory = getNumberOfFilesInDatabaseDirectory();
            Assert.assertTrue(
                String.format("There are %d files in the dir, this should be positive",
                    numberOfFilesInDatabaseDirectory),
                    numberOfFilesInDatabaseDirectory > 0);
            LOGGER.info(String.format("... %s file(s) in dir, testing version", numberOfFilesInDatabaseDirectory));
            // is it correct?
            final Version dbVersion = mmData.getVersionDao().findVersion(VersionableEntity.SCHEMA_VERSION);
            LOGGER.info(String.format("... schema version returned from db should not be null - it is %s", dbVersion));
            Assert.assertNotNull(dbVersion);
            Assert.assertEquals(VersionableEntity.SCHEMA_VERSION, dbVersion.getEntity());
            Assert.assertEquals(CurrentSchemaVersion.CURRENT_SCHEMA_VERSION, dbVersion.getVersion());
            //
            final Version appVersion = mmData.getVersionDao().findVersion(VersionableEntity.APPLICATION_VERSION);
            LOGGER.info(String.format("... application version returned from db should not be null - it is %s", appVersion));
            Assert.assertNotNull(dbVersion);
            Assert.assertEquals(VersionableEntity.APPLICATION_VERSION, appVersion.getEntity());
            Assert.assertEquals(AppVersion.getVersion(), appVersion.getVersion());
            // Here, ensure the complete design of the static data populated
            // in the database is checked for consistency here.
            LOGGER.info("... done");
        } finally {
            // tidy up
            LOGGER.info("... tidying up");
            final boolean random = doesDatabaseLookRandom(dbName);
            if (doDeleteAndClosureCheck) {
                deleteWithClosureCheck(dbName, mmData);
            }
            Assert.assertEquals(shouldBeRandom, random);
        }
    }

    /**
     * Test creation of an encrypted database
     */
    @Test
    public void testCreateEncryptedDatabase() {
        LOGGER.info("*** testCreateEncryptedDatabase start");
        final String dbName = "encrypted";
        final String dbDirPlusDbName = getAbsoluteDatabaseDirectory(dbName);
        final MiniMiserDatabase mmData = accessFactory.createDatabase(dbDirPlusDbName, SQUEAMISH_OSSIFRAGE);
        final boolean shouldBeRandom = true;
        checkDatabaseInvariants(dbName, mmData, shouldBeRandom, true);
        LOGGER.info("*** testCreateEncryptedDatabase done");
    }
    
    /**
     * Test that we have a positive number of db creation steps
     */
    @Test
    public void testThereAreDatabaseCreationSteps() {
        LOGGER.info("*** testThereAreDatabaseCreationSteps start");
        final int numberOfDatabaseCreationSteps = accessFactory.getNumberOfDatabaseCreationSteps();
        Assert.assertTrue(numberOfDatabaseCreationSteps > 0);
        LOGGER.info("There are " + numberOfDatabaseCreationSteps + " DB creation steps");
        LOGGER.info("*** testThereAreDatabaseCreationSteps start");
    }
    
    /**
     * Test that we cannot get DAOs when we've closed.
     * TODO loop on all DAOs when we have more - need to check that all fail
     * @throws IllegalStateException due to closed db
     */
    @Test(expected = IllegalStateException.class)
    public void testGetVersionDaoFailsOnClosedDatabase() throws IllegalStateException {
        LOGGER.info("*** testGetVersionDaoFailsOnClosedDatabase start");
        final String dbName = "testgetdao";
        final String dbDirPlusDbName = getAbsoluteDatabaseDirectory(dbName);
        LOGGER.info(String.format("... dbDirPlusDbName = %s", dbDirPlusDbName));
        // create it...
        LOGGER.info("... creating");
        final MiniMiserDatabase mmData = accessFactory.createDatabase(dbDirPlusDbName, "");
        LOGGER.info("... created");
        try {
            // now close and open it
            assertDatabaseShouldBeOpen(dbName);
            LOGGER.info("... closing");
            mmData.close();
            assertDatabaseShouldBeClosed(dbName);
            LOGGER.info("... now trying to getVersionDao");
            mmData.getVersionDao().findVersion(VersionableEntity.SCHEMA_VERSION);
        } finally {
            LOGGER.info("... deleting");
            deleteDatabaseFiles(dbName);
            LOGGER.info("... done");
        }
    }
    
    // Tests the closer code ---------------------------------------------------

    /**
     * Creates plaintext db, then opens it, then tries to close it.
     */
    @Test
    public void testGoodPlaintextDatabaseCanBeOpenedThenClosed() {
        LOGGER.info("*** testGoodPlaintextDatabaseCanBeOpenedThenClosed start");
        final String dbName = "testcloser";
        createDatabaseWithPluggableBehaviourBeforeDeletion(accessFactory, dbName, "", new RunOnCreatedDb() {
            public void runOnCreatedDb(final String dbName, final String dbPassword, final String dbDirPlusDbName) {
                final MiniMiserDatabase openedDatabase = accessFactory.openDatabase(dbDirPlusDbName, dbPassword);
                final DatabaseDescriptor dd = new DatabaseDescriptor(dbName, dbDirPlusDbName);
                dd.setAttribute(AttributeIdentifier.Database, openedDatabase);

                Closer.close(dd);
                Assert.assertTrue(openedDatabase.isClosed());
            }
            
        });
    }
    
}
