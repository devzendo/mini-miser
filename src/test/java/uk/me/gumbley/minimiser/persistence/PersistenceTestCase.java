package uk.me.gumbley.minimiser.persistence;

import java.io.File;

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
            LOGGER.warn(String.format(
                "Database directory %s contains %d files not cleared up after test",
                databaseDirectory.getAbsoluteFile(),
                files.length));
            Assert.assertEquals(0, files.length);
        }
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
}
