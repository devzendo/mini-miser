package uk.me.gumbley.minimiser.persistence;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests the database directory validator.
 * 
 * @author matt
 *
 */
public class TestDatabaseDirectoryValidator extends PersistenceUnittestCase {
    private static final Logger LOGGER = Logger
            .getLogger(TestDatabaseDirectoryValidator.class);
    private AccessFactory accessFactory;
    
    /**
     * Grab the configured AccessFactory from the Spring App Context.
     * TODO not in a unit test!
     */
    @Before
    public void setup() {
        accessFactory = getSpringLoader().getBean("accessFactory", AccessFactory.class);
    }

    @Test
    public void shouldntCreateNullDir() {
        Assert.assertNotNull(DatabaseDirectoryValidator.validateDirectoryForDatabaseCreation(null));
    }
    
    @Test
    public void shouldntCreateNonExistantDir() {
        final File nondir = new File(getDatabaseDirectory(), "doesnt-exist");
        final String out = DatabaseDirectoryValidator.validateDirectoryForDatabaseCreation(nondir);
        Assert.assertNotNull(out);
        LOGGER.info("shouldntCreateNonExistantDir: " + out);
    }
    
    @Test
    public void shouldntCreateInFileInsteadOfDir() throws IOException {
        final File file = new File(getDatabaseDirectory(), "testfile");
        Assert.assertFalse(file.exists());
        file.deleteOnExit();
        try {
            PrintWriter pw = new PrintWriter(file);
            pw.print("This is a test file");
            pw.close();
            Assert.assertTrue(file.exists());
            final String out = DatabaseDirectoryValidator.validateDirectoryForDatabaseCreation(file);
            Assert.assertNotNull(out);
            LOGGER.info("shouldntCreateInFileInsteadOfDir: " + out);
        } finally {
            file.delete();
        }
    }
    
    @Test
    public void shouldntCreateInNonEmptyDir() throws IOException {
        final File file = new File(getDatabaseDirectory(), "testfile");
        Assert.assertFalse(file.exists());
        file.deleteOnExit();
        try {
            PrintWriter pw = new PrintWriter(file);
            pw.print("This is a test file");
            pw.close();
            Assert.assertTrue(file.exists());
            final String out = DatabaseDirectoryValidator.validateDirectoryForDatabaseCreation(getDatabaseDirectory());
            Assert.assertNotNull(out);
            LOGGER.info("shouldntCreateInNonEmptyDir: " + out);
        } finally {
            file.delete();
        }
    }
    
    @Test
    public void shouldCreateInEmptyDir() {
        Assert.assertNull(DatabaseDirectoryValidator.validateDirectoryForDatabaseCreation(getDatabaseDirectory()));
    }
    
    // WOZERE do the validate for openingn existing
}
