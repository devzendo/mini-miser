package uk.me.gumbley.minimiser.persistence;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the database directory validator.
 * 
 * @author matt
 *
 */
public final class TestDatabaseDirectoryValidator extends PersistenceUnittestCase {
    private static final Logger LOGGER = Logger
            .getLogger(TestDatabaseDirectoryValidator.class);
    
    /**
     * 
     */
    @Test
    public void shouldntCreateNullDir() {
        Assert.assertNotNull(DatabaseDirectoryValidator.validateDirectoryForDatabaseCreation(null));
    }
    
    /**
     * 
     */
    @Test
    public void shouldntCreateNonExistantDir() {
        final File nondir = new File(getDatabaseDirectory(), "doesnt-exist");
        final String out = DatabaseDirectoryValidator.validateDirectoryForDatabaseCreation(nondir);
        Assert.assertNotNull(out);
        LOGGER.info("shouldntCreateNonExistantDir: " + out);
    }
    
    /**
     * @throws IOException on failure
     */
    @Test
    public void shouldntCreateInFileInsteadOfDir() throws IOException {
        final File file = new File(getDatabaseDirectory(), "testfile");
        Assert.assertFalse(file.exists());
        file.deleteOnExit();
        try {
            final PrintWriter pw = new PrintWriter(file);
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
    
    /**
     * @throws IOException on failure
     */
    @Test
    public void shouldntCreateInNonEmptyDir() throws IOException {
        final File file = new File(getDatabaseDirectory(), "testfile");
        Assert.assertFalse(file.exists());
        file.deleteOnExit();
        try {
            final PrintWriter pw = new PrintWriter(file);
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
    
    /**
     * 
     */
    @Test
    public void shouldCreateInEmptyDir() {
        Assert.assertNull(DatabaseDirectoryValidator.validateDirectoryForDatabaseCreation(getDatabaseDirectory()));
    }

    /**
     * 
     */
    @Test
    public void shouldntOpenNull() {
        Assert.assertNotNull(DatabaseDirectoryValidator.validateDirectoryForOpeningExistingDatabase(null));
    }
    
    /**
     * 
     */
    @Test
    public void shouldntOpenNonExistantDir() {
        final File nondir = new File(getDatabaseDirectory(), "doesnt-exist");
        final String out = DatabaseDirectoryValidator.validateDirectoryForOpeningExistingDatabase(nondir);
        Assert.assertNotNull(out);
        LOGGER.info("shouldntOpenNonExistantDir: " + out);
    }
    
    /**
     * @throws IOException on failure
     */
    @Test
    public void shouldntOpenFileInsteadOfDir() throws IOException {
        final File file = new File(getDatabaseDirectory(), "testfile");
        Assert.assertFalse(file.exists());
        file.deleteOnExit();
        try {
            final PrintWriter pw = new PrintWriter(file);
            pw.print("This is a test file");
            pw.close();
            Assert.assertTrue(file.exists());
            final String out = DatabaseDirectoryValidator.validateDirectoryForOpeningExistingDatabase(file);
            Assert.assertNotNull(out);
            LOGGER.info("shouldntOpenFileInsteadOfDir: " + out);
        } finally {
            file.delete();
        }
    }
    
    /**
     * 
     */
    @Test
    public void shouldntOpenEmptyDir() {
        Assert.assertNotNull(DatabaseDirectoryValidator.validateDirectoryForOpeningExistingDatabase(getDatabaseDirectory()));
    }

    /**
     * @throws IOException on failure
     */
    @Test
    public void shouldntOpenIfNotEnoughDbFilesInDir() throws IOException {
        final List<File> fileList = new ArrayList<File>();
        final File dbDir = getDatabaseDirectory();
        final String dbName = dbDir.getName();
        try {
            fileList.add(createTempFile(dbName + ".data.db"));
            fileList.add(createTempFile(dbName + ".index.db"));
            Assert.assertNotNull(DatabaseDirectoryValidator.validateDirectoryForOpeningExistingDatabase(getDatabaseDirectory()));
        } finally {
            for (File file : fileList) {
                file.delete();
            }
        }
    }

    /**
     * @throws IOException on failure
     */
    public void shouldOpenIfEnoughDbFilesInDir() throws IOException {
        final List<File> fileList = new ArrayList<File>();
        final File dbDir = getDatabaseDirectory();
        final String dbName = dbDir.getName();
        try {
            fileList.add(createTempFile(dbName + ".1.log.db"));
            fileList.add(createTempFile(dbName + ".data.db"));
            fileList.add(createTempFile(dbName + ".index.db"));
            Assert.assertNull(DatabaseDirectoryValidator.validateDirectoryForOpeningExistingDatabase(getDatabaseDirectory()));
        } finally {
            for (File file : fileList) {
                file.delete();
            }
        }
    }

    private File createTempFile(final String fileName) throws IOException {
        final File file = new File(getDatabaseDirectory(), fileName);
        Assert.assertFalse(file.exists());
        file.deleteOnExit();
        final PrintWriter pw = new PrintWriter(file);
        pw.print("This is a test file");
        pw.close();
        Assert.assertTrue(file.exists());
        return file;
    }
}
