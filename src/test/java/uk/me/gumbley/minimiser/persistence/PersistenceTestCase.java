package uk.me.gumbley.minimiser.persistence;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

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

    /**
     * Do we have a test database directory specified in test config?
     */
    @Before
    public void testDatabaseDirectoryMustBeAvailable() {
        final TestConfig config = getSpringLoader().getBean("testConfig",
            TestConfig.class);
        if (config == null) {
            Assert.fail("Could not instantiate TestConfig");
        }
        databaseDirectory = config.getTestDatabaseDirectory();
        if (databaseDirectory == null
                || databaseDirectory.getAbsolutePath().length() == 0) {
            Assert.fail("No database directory defined");
        }
        if (!databaseDirectory.exists() || !databaseDirectory.isDirectory()) {
            Assert.fail(String.format(
                "Database dir %s does not exist or is not a directory",
                databaseDirectory.getAbsolutePath()));
        }
    }

    /**
     * Is the test database directory empty?
     */
    @After
    public void testDatabaseDirectoryMustBeEmpty() {
        if (databaseDirectory != null && databaseDirectory.exists()
                && databaseDirectory.isDirectory()) {
            String[] files = databaseDirectory.list();
            if (files == null || files.length == 0) {
                return;
            }
            StringBuffer sb = new StringBuffer();
            sb.append('[');
            for (String string : files) {
                sb.append(string);
                sb.append(',');
            }
            sb.deleteCharAt(sb.length()-1);
            sb.append(']');
            LOGGER.warn(String.format(
                "Database directory %s contains %d files not cleared up after test: %s",
                databaseDirectory.getAbsoluteFile(),
                files.length, sb.toString()));
            Assert.assertEquals(0, files.length);
        }
    }
    
    protected int getNumberOfFilesInDatabaseDirectory() {
        if (databaseDirectory != null && databaseDirectory.exists()
                && databaseDirectory.isDirectory()) {
            return databaseDirectory.list().length;
        }
        return -1;
    }

    /**
     * @return the database directory
     */
    protected File getDatabaseDirectory() {
        return databaseDirectory;
    }
    
    /**
     * Obtain the full path to a named directory
     * @param dbname the name of the database directory e.g. foo
     * @return the directory, prefixed with the test database directory, e.g.
     * /home/matt/testdb/foo
     */
    protected String getAbsoluteDatabaseDirectory(final String dbname) {
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.slashTerminate(databaseDirectory.getAbsolutePath()));
        sb.append(dbname);
        return sb.toString();
    }
    
    /**
     * Delete all files in the test directory that start with dbname
     * @param dbname the name of the database 
     */
    protected void deleteDatabaseFiles(final String dbname) {
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
}
