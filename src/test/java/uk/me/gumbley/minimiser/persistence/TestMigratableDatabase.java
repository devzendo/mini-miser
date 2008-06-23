package uk.me.gumbley.minimiser.persistence;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import uk.me.gumbley.commoncode.concurrency.ThreadUtils;
import uk.me.gumbley.commoncode.patterns.observer.ObservableEvent;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.persistence.domain.CurrentSchemaVersion;
import uk.me.gumbley.minimiser.persistence.domain.Version;
import uk.me.gumbley.minimiser.persistence.domain.VersionableEntity;
import uk.me.gumbley.minimiser.version.AppVersion;


/**
 * Basic tests for low level (JDBC) access to databases.
 * 
 * @author matt
 *
 */
public final class TestMigratableDatabase extends PersistenceUnittestCase {
    private static final String SQUEAMISH_OSSIFRAGE = "Squeamish Ossifrage";

    private static final Logger LOGGER = Logger
            .getLogger(TestMigratableDatabase.class);
    
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
        accessFactory.openMigratableDatabase(null, "");
    }
    
    /**
     * Empty db path is bad
     */
    @Test(expected = DataAccessResourceFailureException.class)
    public void testEmptyDatabaseThrowsUpOnOpen() {
        accessFactory.openMigratableDatabase("", "");
    }
    
    /**
     * Attempts to migrate a nonexistant database should throw.
     */
    @Test(expected = DataAccessResourceFailureException.class)
    public void testNonExistantDatabaseThrowsUpOnOpen() {
        LOGGER.info("*** testNonExistantDatabaseThrowsUpOnOpen");
        String dbDir = getAbsoluteDatabaseDirectory("nonexistant");
        Assert.assertFalse(new File(dbDir).exists());
        accessFactory.openMigratableDatabase(dbDir, null);
    }
    
    // CREATE TESTS ------------------------------------------------------------
    
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
        checkInvariantsForCreatedDatabase(dbName, mmData, shouldBeRandom);
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
            }};  
        final String dbName = "testcreate";
        final String dbDirPlusDbName = getAbsoluteDatabaseDirectory(dbName);
        final MiniMiserDatabase mmData = accessFactory.createDatabase(dbDirPlusDbName, "", observer);
        final boolean shouldBeRandom = false;
        checkInvariantsForCreatedDatabase(dbName, mmData, shouldBeRandom);
        Assert.assertTrue(count.get() > 0);
        LOGGER.info("Database creation steps: currently " + count.get());
        LOGGER.info("*** testCreatePlaintextDatabaseWithListener done");
    }
    
    private void checkInvariantsForCreatedDatabase(final String dbName, final MiniMiserDatabase mmData, final boolean shouldBeRandom) {
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
            Version dbVersion = mmData.getVersionDao().findVersion(VersionableEntity.SCHEMA_VERSION);
            LOGGER.info(String.format("... schema version returned from db should be null - it is %s", dbVersion));
            Assert.assertNotNull(dbVersion);
            Assert.assertEquals(VersionableEntity.SCHEMA_VERSION, dbVersion.getEntity());
            Assert.assertEquals(CurrentSchemaVersion.CURRENT_SCHEMA_VERSION, dbVersion.getVersion());
            //
            Version appVersion = mmData.getVersionDao().findVersion(VersionableEntity.APPLICATION_VERSION);
            LOGGER.info(String.format("... application version returned from db should be null - it is %s", appVersion));
            Assert.assertNotNull(dbVersion);
            Assert.assertEquals(VersionableEntity.APPLICATION_VERSION, appVersion.getEntity());
            Assert.assertEquals(AppVersion.getVersion(), appVersion.getVersion());
            // Here, ensure the complete design of the static data populated
            // in the database is checked for consistency here.
            LOGGER.info("... done");
        } finally {
            // tidy up
            LOGGER.info("... tidying up");
            boolean random = doesDatabaseLookRandom(dbName);
            deleteWithClosureCheck(dbName, mmData);
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
        checkInvariantsForCreatedDatabase(dbName, mmData, shouldBeRandom);
        LOGGER.info("*** testCreateEncryptedDatabase done");
    }
}
