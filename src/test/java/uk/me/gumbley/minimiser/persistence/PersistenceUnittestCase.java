package uk.me.gumbley.minimiser.persistence;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import uk.me.gumbley.commoncode.os.OSTypeDetect;
import uk.me.gumbley.commoncode.os.OSTypeDetect.OSType;
import uk.me.gumbley.commoncode.string.StringUtils;
import uk.me.gumbley.minimiser.config.UnittestingConfig;
import uk.me.gumbley.minimiser.springloader.ApplicationContext;
import uk.me.gumbley.minimiser.springloader.SpringLoaderUnittestCase;
import uk.me.gumbley.minimiser.util.OpenFilesDetector;

/**
 * All persistence test cases must have a test database directory that must be
 * present at start and empty afterwards. It's the responsibility of individual
 * tests to clean up after themselves.
 * 
 * @author matt
 */
@ApplicationContext("uk/me/gumbley/minimiser/persistence/PersistenceTestCase.xml")
public class PersistenceUnittestCase extends SpringLoaderUnittestCase {
    private static final Logger LOGGER = Logger
            .getLogger(PersistenceUnittestCase.class);
    private File databaseDirectory;
    private boolean suppressEmptyCheck = false;

    /**
     * Do we have a test database directory specified in test config?
     */
    @Before
    public final void testDatabaseDirectoryMustBeAvailable() {
        LOGGER.info(">>> testDatabaseDirectoryMustBeAvailable");
        try {
            final UnittestingConfig config = getSpringLoader().getBean("testConfig",
                UnittestingConfig.class);
            if (config == null) {
                final String err = "Could not instantiate TestConfig";
                LOGGER.error(err);
                Assert.fail(err);
            }
            databaseDirectory = config.getTestDatabaseDirectory();
            if (databaseDirectory == null
                    || databaseDirectory.getAbsolutePath().length() == 0) {
                final String err = "No database directory defined";
                LOGGER.error(err);
                Assert.fail(err);
            }
            if (!databaseDirectory.exists() || !databaseDirectory.isDirectory()) {
                final String err = String.format(
                                "Database dir %s does not exist or is not a directory",
                                databaseDirectory.getAbsolutePath());
                LOGGER.error(err);
                Assert.fail(err);
            }
        } finally {
            LOGGER.info("<<< testDatabaseDirectoryMustBeAvailable");
        }
    }

    /**
     * Is the test database directory empty?
     */
    @After
    public final void testDatabaseDirectoryMustBeEmpty() {
        LOGGER.info(">>> testDatabaseDirectoryMustBeEmpty");
        try {
            if (suppressEmptyCheck) {
                return;
            }
            if (databaseDirectory != null && databaseDirectory.exists()
                    && databaseDirectory.isDirectory()) {
                final List<String> files = Arrays.asList(databaseDirectory.list());
                if (files == null || files.size() == 0) {
                    return;
                }
                final String err = String.format(
                                "Database directory %s contains %d files not cleared up after test: %s",
                                databaseDirectory.getAbsoluteFile(),
                                files.size(), StringUtils.join(files, ", "));
                LOGGER.error(err);
                if (files.size() != 0) {
                    Assert.fail(err);
                }
            }
        } finally {
            LOGGER.info("<<< testDatabaseDirectoryMustBeEmpty");
        }
    }
    
    /**
     * @return the number of files found in the database directory
     */
    protected final int getNumberOfFilesInDatabaseDirectory() {
        if (databaseDirectory != null && databaseDirectory.exists()
                && databaseDirectory.isDirectory()) {
            return databaseDirectory.list().length;
        }
        return -1;
    }

    /**
     * @return the database directory
     */
    protected final File getDatabaseDirectory() {
        return databaseDirectory;
    }
    
    /**
     * Obtain the full path to a named directory
     * @param dbname the name of the database directory e.g. foo
     * @return the directory, prefixed with the test database directory, e.g.
     * /home/matt/testdb/foo
     */
    protected final String getAbsoluteDatabaseDirectory(final String dbname) {
        final StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.slashTerminate(databaseDirectory.getAbsolutePath()));
        sb.append(dbname);
        return sb.toString();
    }
    
    /**
     * Delete all files in the test directory that start with dbname
     * @param dbname the name of the database 
     */
    protected final void deleteDatabaseFiles(final String dbname) {
        int count = 0;
        boolean allGone = true;
        LOGGER.info(String.format("Deleting database %s files", dbname));
        if (databaseDirectory != null && databaseDirectory.exists()
                && databaseDirectory.isDirectory()) {
            final FileFilter filter = new FileFilter() {
                public boolean accept(final File pathname) {
                    LOGGER.debug(String.format("Considering %s", pathname.getAbsolutePath()));
                    return pathname.isFile() && pathname.getName().startsWith(dbname);
                }
            };
            final File[] dbFiles = databaseDirectory.listFiles(filter);
            count = dbFiles.length;
            for (File file : dbFiles) {
                LOGGER.debug(String.format("Deleting %s", file.getAbsoluteFile()));
                final boolean gone = file.delete();
                allGone &= gone;
                if (!gone) {
                    LOGGER.warn(String.format("Could not delete %s", file.getAbsolutePath()));
                }
            }
        }
        Assert.assertTrue("No files to delete, when some were expected", count > 0);
        Assert.assertTrue("Some files failed to delete", allGone);
    }
    
    /**
     * Close the database, and if on Linux, check to see if it really is
     * closed.
     * @param dbName the database name
     * @param mmData the factory to close the db with
     */
    protected final void deleteWithClosureCheck(final String dbName, final Database mmData) {
        LOGGER.info("*** still open?");
        assertDatabaseShouldBeOpen(dbName);
        LOGGER.info("*** closing");
        mmData.close();
        LOGGER.info("*** closed");
        assertDatabaseShouldBeClosed(dbName);
        deleteDatabaseFiles(dbName);
    }

    /**
     * The database should be open.
     * @param dbName the name of the database in the test directory
     */
    protected final void assertDatabaseShouldBeOpen(final String dbName) {
        if (OSTypeDetect.getInstance().getOSType() == OSType.Linux) {
            Assert.assertTrue(databaseHasOpenFileDescriptors(dbName));
        }
    }

    /**
     * The database should be closed.
     * @param dbName the name of the database in the test directory
     */
    protected final void assertDatabaseShouldBeClosed(final String dbName) {
        if (OSTypeDetect.getInstance().getOSType() == OSType.Linux) {
            Assert.assertFalse(databaseHasOpenFileDescriptors(dbName));
        }
    }
    /**
     * Does lsof | grep <dbName> report any open file descriptors?
     * This should only ever be run on Linux or platforms that
     * have lsof.
     * @param dbName the database name
     * @return true if files are open, false if not.
     */
    protected final boolean databaseHasOpenFileDescriptors(final String dbName) {
        final String dbDir = getDatabaseDirectory().getAbsolutePath();
        return OpenFilesDetector.anyOpenFiles(dbDir, dbName);
    }

    
    /**
     * Suppress the check for database directory emptiness after a test case
     * since if it fails, it'll obscure any other reason for test failure.
     */
    protected final void suppressEmptyCheck() {
        this.suppressEmptyCheck = true;
    }

    /**
     * @param dbName the database name to check for randomness
     * @return true if it looks random, false if not
     */
    protected final boolean doesDatabaseLookRandom(final String dbName) {
        final List<File> dbFiles = getDatabaseFiles(dbName);
        boolean atLeastOneFileToCheck = false;
        boolean random = true;
        for (File file : dbFiles) {
            // skip small files e.g. lock files are ~ 99 bytes.
            // even an empty db has files around 128KB.
            if (random && file.isFile() && file.length() > 1024) {
                atLeastOneFileToCheck = true;
                LOGGER.debug(String.format("Checking randomness of %s", file.getAbsoluteFile()));
                final boolean isFileRandom = isRandom(file);
                LOGGER.debug(String.format("%s is %srandom", file.getAbsolutePath(), isFileRandom ? "" : "NOT "));
                random &= isFileRandom;
            }
        }
        final boolean randomnessResult = atLeastOneFileToCheck && random;
        LOGGER.debug(String.format("Randomness of %s is %s", dbName, randomnessResult));
        return randomnessResult;
    }

    private boolean isRandom(final File file) {
        try {
            final long[] hist = new long[256];
            final long size = file.length();
            final InputStream is = new FileInputStream(file);
            try {
                final byte[] buf = new byte[512];
                int nread = 0;
                do {
                    nread = is.read(buf);
                    if (nread != -1) {
                        for (int i = 0; i < nread; i++) {
                            final int rand = buf[i] & 0x00ff;
                            hist[rand]++;
                        }
                    }
                } while (nread != -1);
            } finally {
                is.close();
            }
            // need 75% of the data in the file to be within 20% of size/256
            final double bytesPerfectlyEquallyDistributed = size / 256;
            final double twentyPercent = (bytesPerfectlyEquallyDistributed * 0.2);
            final double upperTolerance = bytesPerfectlyEquallyDistributed + twentyPercent; 
            final double lowerTolerance = bytesPerfectlyEquallyDistributed - twentyPercent; 
            //LOGGER.debug(String.format("[%f, %f, %f]", lowerTolerance, bytesPerfectlyEquallyDistributed, upperTolerance));
            int numWithinTolerance = 0;
            for (int i = 0; i < 256; i++) {
                final boolean withinTolerance = (hist[i] >= (long) lowerTolerance && hist[i] <= (long) upperTolerance);
                //LOGGER.debug(String.format("Byte 0x%02X, count %d %swithin tolerance", i, hist[i], withinTolerance ? " " : "NOT "));
                if (withinTolerance) {
                    numWithinTolerance++;
                }
            }
            final double randomness = (double) numWithinTolerance / 256.0;
            //LOGGER.debug(String.format("Randomness is %f", randomness));
            return randomness >= 0.75;
        } catch (final IOException e) {
            LOGGER.warn(String.format("IOException checking randomness: %s", e.getMessage()));
            return false;
        }
    }

    private List<File> getDatabaseFiles(final String dbName) {
        final List<File> dbFiles = new ArrayList<File>();
        if (databaseDirectory != null && databaseDirectory.exists()
                && databaseDirectory.isDirectory()) {
            final FileFilter filter = new FileFilter() {
                public boolean accept(final File pathname) {
                    LOGGER.debug(String.format("Considering %s", pathname.getAbsolutePath()));
                    return pathname.isFile() && pathname.getName().startsWith(dbName);
                }
            };
            dbFiles.addAll(Arrays.asList(databaseDirectory.listFiles(filter)));
        }
        return dbFiles;
    }
}
