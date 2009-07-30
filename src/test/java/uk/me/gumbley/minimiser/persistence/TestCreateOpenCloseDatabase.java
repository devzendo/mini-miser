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
import uk.me.gumbley.minimiser.persistence.domain.Version;
import uk.me.gumbley.minimiser.persistence.domain.VersionableEntity;
import uk.me.gumbley.minimiser.pluginmanager.Plugin;
import uk.me.gumbley.minimiser.pluginmanager.PluginManager;


/**
 * Basic tests for creating / opening / closing databases.
 * 
 * @author matt
 *
 */
public final class TestCreateOpenCloseDatabase extends DummyAppPluginManagerPersistenceUnittestCase {
    private static final String SQUEAMISH_OSSIFRAGE = "Squeamish Ossifrage";

    private static final Logger LOGGER = Logger
            .getLogger(TestCreateOpenCloseDatabase.class);

    private PluginManager mPluginManager;
    
    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        mPluginManager = getSpringLoader().getBean("pluginManager", PluginManager.class);
    }
    
    // OPEN TESTS --------------------------------------------------------------
    
    /**
     * Null db path is bad
     */
    @Test(expected = DataAccessResourceFailureException.class)
    public void testNullDatabaseThrowsUpOnOpen() {
        LOGGER.info(">>> testNullDatabaseThrowsUpOnOpen");
        getAccessFactory().openDatabase(null, "");
        LOGGER.info("<<< testNullDatabaseThrowsUpOnOpen");
    }
    
    /**
     * Empty db path is bad
     */
    @Test(expected = DataAccessResourceFailureException.class)
    public void testEmptyDatabaseThrowsUpOnOpen() {
        LOGGER.info(">>> testEmptyDatabaseThrowsUpOnOpen");
        getAccessFactory().openDatabase("", "");
        LOGGER.info("<<< testEmptyDatabaseThrowsUpOnOpen");
    }
    
    /**
     * Attempts to open a nonexistant database should throw.
     */
    @Test(expected = DataAccessResourceFailureException.class)
    public void testNonExistantDatabaseThrowsUpOnOpen() {
        LOGGER.info(">>> testNonExistantDatabaseThrowsUpOnOpen");
        final String dbDir = getAbsoluteDatabaseDirectory("nonexistant");
        Assert.assertFalse(new File(dbDir).exists());
        getAccessFactory().openDatabase(dbDir, null);
        LOGGER.info("<<< testNonExistantDatabaseThrowsUpOnOpen");
    }

    /**
     * Creates plaintext db, then tries to open it, verifying that it can.
     */
    @Test
    public void testGoodPlaintextDatabaseCanBeOpened() {
        LOGGER.info(">>> testGoodPlaintextDatabaseCanBeOpened");
        final String dbName = "testopen";
        doCreateDatabaseBoilerplate(getAccessFactory(), dbName, "", new RunOnCreatedDb() {
            public void runOnCreatedDb(final String dbName, final String dbPassword, final String dbDirPlusDbName) {
                LOGGER.info("... re-opening");
                final MiniMiserDAOFactory openedDatabase = getAccessFactory().openDatabase(dbDirPlusDbName, dbPassword);
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
        LOGGER.info("<<< testGoodPlaintextDatabaseCanBeOpened");
    }

    /**
     * Creates encrypted db, then tries to open it with the creation password,
     * verifying that it can.
     */
    @Test
    public void testGoodEncryptedDatabaseCanBeOpened() {
        LOGGER.info(">>> testGoodEncryptedDatabaseCanBeOpened");
        final String dbName = "testopenenc";
        final String dbPassword = "Qwerty123";
        doCreateDatabaseBoilerplate(getAccessFactory(), dbName, dbPassword, new RunOnCreatedDb() {
            public void runOnCreatedDb(final String dbName, final String dbPassword, final String dbDirPlusDbName) {
                LOGGER.info("... re-opening");
                final MiniMiserDAOFactory openedDatabase = getAccessFactory().openDatabase(dbDirPlusDbName, dbPassword);
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
        LOGGER.info("<<< testGoodEncryptedDatabaseCanBeOpened");
    }

    /**
     * Creates encrypted db, then tries to open it with another password,
     * verifying that it can't.
     */
    @Test
    public void testGoodEncryptedDatabaseCantBeOpenedWithOtherPassword() {
        LOGGER.info(">>> testGoodEncryptedDatabaseCantBeOpenedWithOtherPassword");
        final String dbName = "testopenenc";
        final String dbCreationPassword = "Qwerty123";
        final String dbEvilHackerPassword = "Fossi11ized";
        doCreateDatabaseBoilerplate(getAccessFactory(), dbName, dbCreationPassword, new RunOnCreatedDb() {
            public void runOnCreatedDb(final String dbName, final String dbPassword, final String dbDirPlusDbName) {
                LOGGER.info("... re-opening");
                MiniMiserDAOFactory openedDatabase = null;
                boolean caughtBPE = false;
                try {
                    openedDatabase = getAccessFactory().openDatabase(dbDirPlusDbName, dbEvilHackerPassword);
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
        LOGGER.info("<<< testGoodEncryptedDatabaseCantBeOpenedWithOtherPassword");
    }

    /**
     * Creates encrypted db, then tries to open it with an empty password,
     * verifying that it can't.
     */
    @Test
    public void testGoodEncryptedDatabaseCantBeOpenedWithEmptyPassword() {
        LOGGER.info(">>> testGoodEncryptedDatabaseCantBeOpenedWithEmptyPassword");
        final String dbName = "testopenenc";
        final String dbCreationPassword = "Qwerty123";
        final String dbEvilHackerPassword = "";
        doCreateDatabaseBoilerplate(getAccessFactory(), dbName, dbCreationPassword, new RunOnCreatedDb() {
            public void runOnCreatedDb(final String dbName, final String dbPassword, final String dbDirPlusDbName) {
                LOGGER.info("... re-opening");
                MiniMiserDAOFactory openedDatabase = null;
                boolean caughtBPE = false;
                try {
                    openedDatabase = getAccessFactory().openDatabase(dbDirPlusDbName, dbEvilHackerPassword);
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
        LOGGER.info("<<< testGoodEncryptedDatabaseCantBeOpenedWithEmptyPassword");
    }

    /**
     * Null db path is bad
     */
    @Test(expected = DataAccessResourceFailureException.class)
    public void testNullDatabaseThrowsUpOnCreate() {
        LOGGER.info(">>> testNullDatabaseThrowsUpOnCreate");
        getAccessFactory().createDatabase(null, "");
        LOGGER.info("<<< testNullDatabaseThrowsUpOnCreate");
    }
    
    /**
     * Empty db path is bad
     */
    @Test(expected = DataAccessResourceFailureException.class)
    public void testEmptyDatabaseThrowsUpOnCreate() {
        LOGGER.info(">>> testEmptyDatabaseThrowsUpOnCreate");
        getAccessFactory().createDatabase("", "");
        LOGGER.info("<<< testEmptyDatabaseThrowsUpOnCreate");
    }
    
    /**
     * Test creation of a non-encrypted database
     */
    @Test
    public void testCreatePlaintextDatabase() {
        LOGGER.info(">>> testCreatePlaintextDatabase");
        final String dbName = "testcreate";
        // should we be able to create this db?
        // TODO my assumption about the databse directory + dbname was wrong.
        // I thought it'd create a dir for the db, but dbName is just the
        // prefix of the files in the parent dir
        final String dbDirPlusDbName = getAbsoluteDatabaseDirectory(dbName);
        LOGGER.info(String.format("... dbDirPlusDbName = %s", dbDirPlusDbName));
        // create it...
        LOGGER.info("... creating");
        final MiniMiserDAOFactory mmData = getAccessFactory().createDatabase(dbDirPlusDbName, "");
        final boolean shouldBeRandom = false;
        checkDatabaseInvariants(dbName, mmData, shouldBeRandom, true);
        LOGGER.info("<<< testCreatePlaintextDatabase");
    }

    /**
     * Test creation of a non-encrypted database, listening for progress events
     */
    @Test
    public void testCreatePlaintextDatabaseWithListener() {
        LOGGER.info(">>> testCreatePlaintextDatabaseWithListener");
        final AtomicInteger count = new AtomicInteger(0);
        final Observer<PersistenceObservableEvent> observer = new Observer<PersistenceObservableEvent>() {
            public void eventOccurred(final PersistenceObservableEvent observableEvent) {
                LOGGER.info("Progress: " + observableEvent.getDescription());
                count.incrementAndGet();
            }
        };  
        final String dbName = "testcreate";
        final String dbDirPlusDbName = getAbsoluteDatabaseDirectory(dbName);
        final MiniMiserDAOFactory mmData = getAccessFactory().createDatabase(dbDirPlusDbName, "", observer, null);
        final boolean shouldBeRandom = false;
        checkDatabaseInvariants(dbName, mmData, shouldBeRandom, true);
        final int numberOfDatabaseCreationSteps = getAccessFactory().getNumberOfDatabaseCreationSteps(null);
        Assert.assertTrue(count.get() == numberOfDatabaseCreationSteps);
        LOGGER.info("Database creation steps: currently " + count.get());
        LOGGER.info("<<< testCreatePlaintextDatabaseWithListener");
    }
    
    private void checkDatabaseInvariants(final String dbName,
            final MiniMiserDAOFactory mmData, final boolean shouldBeRandom,
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
            // The versions of the plugins will be checked for in
            // TestVersionDao so don't be exhaustive here.
            final Plugin appPlugin = mPluginManager.getApplicationPlugin();
            final Version dbVersion = mmData.getVersionDao().findVersion(appPlugin.getName(), VersionableEntity.SCHEMA_VERSION);
            LOGGER.info(String.format("... schema version returned from db should not be null - it is %s", dbVersion));
            Assert.assertNotNull(dbVersion);
            //
            final Version appVersion = mmData.getVersionDao().findVersion(appPlugin.getName(), VersionableEntity.APPLICATION_VERSION);
            LOGGER.info(String.format("... application version returned from db should not be null - it is %s", appVersion));
            Assert.assertNotNull(dbVersion);
            
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
        LOGGER.info(">>> testCreateEncryptedDatabase");
        final String dbName = "encrypted";
        final String dbDirPlusDbName = getAbsoluteDatabaseDirectory(dbName);
        final MiniMiserDAOFactory mmData = getAccessFactory().createDatabase(dbDirPlusDbName, SQUEAMISH_OSSIFRAGE);
        final boolean shouldBeRandom = true;
        checkDatabaseInvariants(dbName, mmData, shouldBeRandom, true);
        LOGGER.info("<<< testCreateEncryptedDatabase");
    }
    
    /**
     * Test that we have a positive number of db creation steps
     */
    @Test
    public void testThereAreDatabaseCreationSteps() {
        LOGGER.info(">>> testThereAreDatabaseCreationSteps");
        final int numberOfDatabaseCreationSteps = getAccessFactory().getNumberOfDatabaseCreationSteps(null);
        Assert.assertTrue(numberOfDatabaseCreationSteps > 0);
        LOGGER.info("There are " + numberOfDatabaseCreationSteps + " DB creation steps");
        LOGGER.info("<<< testThereAreDatabaseCreationSteps");
    }
    
    /**
     * Test that we cannot get DAOs when we've closed.
     * TODO loop on all DAOs when we have more - need to check that all fail
     * @throws IllegalStateException due to closed db
     */
    @Test(expected = IllegalStateException.class)
    public void testGetVersionDaoFailsOnClosedDatabase() throws IllegalStateException {
        LOGGER.info(">>> testGetVersionDaoFailsOnClosedDatabase");
        final String dbName = "testgetdao";
        final String dbDirPlusDbName = getAbsoluteDatabaseDirectory(dbName);
        LOGGER.info(String.format("... dbDirPlusDbName = %s", dbDirPlusDbName));
        // create it...
        LOGGER.info("... creating");
        final MiniMiserDAOFactory mmData = getAccessFactory().createDatabase(dbDirPlusDbName, "");
        LOGGER.info("... created");
        try {
            // now close and open it
            assertDatabaseShouldBeOpen(dbName);
            LOGGER.info("... closing");
            
            mmData.close();
            assertDatabaseShouldBeClosed(dbName);
            LOGGER.info("... now trying to getVersionDao");
            mmData.getVersionDao().findVersion(null, VersionableEntity.SCHEMA_VERSION);
        } finally {
            LOGGER.info("... deleting");
            deleteDatabaseFiles(dbName);
            LOGGER.info("... done");
        }
        LOGGER.info("<<< testGetVersionDaoFailsOnClosedDatabase");
    }
    
    // Tests the closer code ---------------------------------------------------

    /**
     * Creates plaintext db, then opens it, then tries to close it.
     */
    @Test
    public void testGoodPlaintextDatabaseCanBeOpenedThenClosed() {
        LOGGER.info(">>> testGoodPlaintextDatabaseCanBeOpenedThenClosed");
        final String dbName = "testcloser";
        doCreateDatabaseBoilerplate(getAccessFactory(), dbName, "", new RunOnCreatedDb() {
            public void runOnCreatedDb(final String dbName, final String dbPassword, final String dbDirPlusDbName) {
                final MiniMiserDAOFactory openedDatabase = getAccessFactory().openDatabase(dbDirPlusDbName, dbPassword);
                final DatabaseDescriptor dd = new DatabaseDescriptor(dbName, dbDirPlusDbName);
                dd.setAttribute(AttributeIdentifier.Database, openedDatabase);

                Closer.close(dd);
                Assert.assertTrue(openedDatabase.isClosed());
            }
            
        });
        LOGGER.info("<<< testGoodPlaintextDatabaseCanBeOpenedThenClosed");
    }
}
