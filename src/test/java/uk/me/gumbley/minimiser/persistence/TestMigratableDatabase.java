package uk.me.gumbley.minimiser.persistence;

import java.io.File;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import uk.me.gumbley.minimiser.persistence.domain.CurrentSchemaVersion;
import uk.me.gumbley.minimiser.persistence.domain.Version;
import uk.me.gumbley.minimiser.persistence.domain.VersionableEntity;


/**
 * Basic tests for low level (JDBC) access to databases.
 * 
 * @author matt
 *
 */
public final class TestMigratableDatabase extends PersistenceTestCase {
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
     * Test creation of a database
     */
    @Test
    public void testCreateDatabase() {
        LOGGER.info("*** testCreateDatabase");
        // should we be able to create this db?
        String dbDir = getAbsoluteDatabaseDirectory("testcreate");
        Assert.assertFalse(new File(dbDir).exists());
        // create it...
        MiniMiserDatabase mmData = accessFactory.createDatabase(dbDir, "");
        try {
            LOGGER.info("*** created");
            // was something created?
            int numberOfFilesInDatabaseDirectory = getNumberOfFilesInDatabaseDirectory();
            Assert.assertTrue(String.format("There are %d files in the dir, this should be positive", numberOfFilesInDatabaseDirectory), numberOfFilesInDatabaseDirectory > 0);
            LOGGER.info("*** testing version");
            // is it correct?
            Version version = mmData.getVersionDao().findVersion(VersionableEntity.SCHEMA_VERSION);
            Assert.assertNotNull(version);
            Assert.assertEquals(VersionableEntity.SCHEMA_VERSION, version.getEntity());
            Assert.assertEquals(CurrentSchemaVersion.CURRENT_SCHEMA_VERSION, version.getVersion());
            // FUTURE TODO: ensure the complete design of the static data populated
            // in the database is checked for consistency here.
            // tidy up
            // TODO WTF?
        } finally {
            LOGGER.info("*** closing");
            mmData.close();
            LOGGER.info("*** closed");
            deleteDatabaseFiles("testcreate");
        }
    }
}
