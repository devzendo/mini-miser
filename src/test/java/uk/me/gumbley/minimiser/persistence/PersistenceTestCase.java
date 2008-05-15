package uk.me.gumbley.minimiser.persistence;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import uk.me.gumbley.commoncode.executor.IteratorExecutor;
import uk.me.gumbley.commoncode.os.OSTypeDetect;
import uk.me.gumbley.commoncode.os.OSTypeDetect.OSType;
import uk.me.gumbley.commoncode.string.StringUtils;
import uk.me.gumbley.minimiser.config.TestConfig;
import uk.me.gumbley.minimiser.springloader.ApplicationContext;
import uk.me.gumbley.minimiser.springloader.SpringLoaderTestCase;

/**
 * All persistence test cases must have a test database directory that must be
 * present at start and empty afterwards. It's the responsibility of individual
 * tests to clean up after themselves.
 * 
 * @author matt
 */
@ApplicationContext("uk/me/gumbley/minimiser/persistence/PersistenceTestCase.xml")
public class PersistenceTestCase extends SpringLoaderTestCase {
    private static final Logger LOGGER = Logger
            .getLogger(PersistenceTestCase.class);
    private File databaseDirectory;
    private boolean suppressEmptyCheck = false;

    /**
     * Do we have a test database directory specified in test config?
     */
    @Before
    public final void testDatabaseDirectoryMustBeAvailable() {
        LOGGER.info(">>> testDatabaseDirectoryMustBeAvailable");
        try {
            final TestConfig config = getSpringLoader().getBean("testConfig",
                TestConfig.class);
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
                List<String> files = Arrays.asList(databaseDirectory.list());
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
        StringBuilder sb = new StringBuilder();
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
                boolean gone = file.delete();
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
        if (OSTypeDetect.getInstance().getOSType() == OSType.Linux) {
            Assert.assertTrue(databaseOpenFileDescriptors(dbName));
        }
        LOGGER.info("*** closing");
        mmData.close();
        LOGGER.info("*** closed");
        if (OSTypeDetect.getInstance().getOSType() == OSType.Linux) {
            Assert.assertFalse(databaseOpenFileDescriptors(dbName));
        }
        deleteDatabaseFiles(dbName);
    }
    /**
     * Does lsof | grep <dbName> report any open file descriptors?
     * This should only ever be run on Linux or platforms that
     * have lsof.
     * @param dbName the database name
     * @return true if files are open, false if not.
     */
    protected final boolean databaseOpenFileDescriptors(final String dbName) {
        if (OSTypeDetect.getInstance().getOSType() != OSType.Linux) {
            throw new IllegalStateException("databaseOpenFileDescriptors called on nonsupported platform");
        }
        final String dbDir = getDatabaseDirectory().getAbsolutePath();
        ArrayList <String>cmd = new ArrayList<String>();
        cmd.add("lsof");
        cmd.add("-Fn");
        cmd.add("+d");
        cmd.add(dbDir);
        IteratorExecutor ie = new IteratorExecutor((String[]) cmd.toArray(new String[0]));
        String ourFilePath = String.format("n%s/%s", dbDir, dbName);
        boolean anyOpen = false;
        while (ie.hasNext()) {
            String line = ie.next().toString();
            if (line.startsWith(ourFilePath)) {
                anyOpen = true;
                LOGGER.debug(String.format("Open file: '%s'", line));
            }
        }
        final int exitValue = ie.getExitValue();
        final String errMsg = String.format("lsof of %s returned %d : %s", dbDir, exitValue, anyOpen ? "FILES ARE OPEN" : "NO FILES OPEN");
        LOGGER.debug(errMsg);
        return anyOpen;
    }

    /**
     * Suppress the check for database directory emptiness after a test case
     * since if it fails, it'll obscure any other reason for test failure.
     */
    protected final void suppressEmptyCheck() {
        this.suppressEmptyCheck = true;
    }
}
