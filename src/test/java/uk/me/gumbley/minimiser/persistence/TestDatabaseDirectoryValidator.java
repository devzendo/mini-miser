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
        LOGGER.info("*** shouldntCreateNullDir");
        final String out = DatabaseDirectoryValidator.validateDirectoryForDatabaseCreation(null);
        LOGGER.info("shouldntCreateNullDir: " + out);
        Assert.assertNotNull(out);
    }
    
    /**
     * 
     */
    @Test
    public void shouldntCreateNonExistantDir() {
        LOGGER.info("*** shouldntCreateNonExistantDir");
        final File nondir = new File(getDatabaseDirectory(), "doesnt-exist");
        final String out = DatabaseDirectoryValidator.validateDirectoryForDatabaseCreation(nondir);
        LOGGER.info("shouldntCreateNonExistantDir: " + out);
        Assert.assertNotNull(out);
    }
    
    /**
     * @throws IOException on failure
     */
    @Test
    public void shouldntCreateInFileInsteadOfDir() throws IOException {
        LOGGER.info("*** shouldntCreateInFileInsteadOfDir");
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
        LOGGER.info("*** shouldntCreateInNonEmptyDir");
        final File file = new File(getDatabaseDirectory(), "testfile");
        Assert.assertFalse(file.exists());
        file.deleteOnExit();
        try {
            final PrintWriter pw = new PrintWriter(file);
            pw.print("This is a test file");
            pw.close();
            Assert.assertTrue(file.exists());
            final String out = DatabaseDirectoryValidator.validateDirectoryForDatabaseCreation(getDatabaseDirectory());
            LOGGER.info("shouldntCreateInNonEmptyDir: " + out);
            Assert.assertNotNull(out);
        } finally {
            file.delete();
        }
    }
    
    /**
     * 
     */
    @Test
    public void shouldCreateInEmptyDir() {
        LOGGER.info("*** shouldCreateInEmptyDir");
        final String out = DatabaseDirectoryValidator.validateDirectoryForDatabaseCreation(getDatabaseDirectory());
        Assert.assertNull(out);
    }

    /**
     * 
     */
    @Test
    public void shouldntOpenNull() {
        LOGGER.info("*** shouldntOpenNull");
        final String out = DatabaseDirectoryValidator.validateDirectoryForOpeningExistingDatabase(null);
        LOGGER.info("shouldntOpenNull: " + out);
        Assert.assertNotNull(out);
    }
    
    /**
     * 
     */
    @Test
    public void shouldntOpenNonExistantDir() {
        LOGGER.info("*** shouldntOpenNonExistantDir");
        final File nondir = new File(getDatabaseDirectory(), "doesnt-exist");
        final String out = DatabaseDirectoryValidator.validateDirectoryForOpeningExistingDatabase(nondir);
        LOGGER.info("shouldntOpenNonExistantDir: " + out);
        Assert.assertNotNull(out);
    }
    
    /**
     * @throws IOException on failure
     */
    @Test
    public void shouldntOpenFileInsteadOfDir() throws IOException {
        LOGGER.info("*** shouldntOpenFileInsteadOfDir");
        final File file = new File(getDatabaseDirectory(), "testfile");
        Assert.assertFalse(file.exists());
        file.deleteOnExit();
        try {
            final PrintWriter pw = new PrintWriter(file);
            pw.print("This is a test file");
            pw.close();
            Assert.assertTrue(file.exists());
            final String out = DatabaseDirectoryValidator.validateDirectoryForOpeningExistingDatabase(file);
            LOGGER.info("shouldntOpenFileInsteadOfDir: " + out);
            Assert.assertNotNull(out);
        } finally {
            file.delete();
        }
    }
    
    /**
     * 
     */
    @Test
    public void shouldntOpenEmptyDir() {
        LOGGER.info("*** shouldntOpenEmptyDir");
        final String out = DatabaseDirectoryValidator.validateDirectoryForOpeningExistingDatabase(getDatabaseDirectory());
        LOGGER.info("shouldntOpenEmptyDir: " + out);
        Assert.assertNotNull(out);
    }

    /**
     * @throws IOException on failure
     */
    @Test
    public void shouldntOpenIfNotEnoughDbFilesInDir() throws IOException {
        LOGGER.info("*** shouldntOpenIfNotEnoughDbFilesInDir");
        final List<File> fileList = new ArrayList<File>();
        final File dbDir = getDatabaseDirectory();
        final String dbName = dbDir.getName();
        try {
            fileList.add(createTempFile(dbName + ".data.db"));
            fileList.add(createTempFile(dbName + ".index.db"));
            final String out = DatabaseDirectoryValidator.validateDirectoryForOpeningExistingDatabase(getDatabaseDirectory());
            LOGGER.info("shouldntOpenIfNotEnoughDbFilesInDir: " + out);
            Assert.assertNotNull(out);
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
        LOGGER.info("*** shouldOpenIfEnoughDbFilesInDir");
        final List<File> fileList = new ArrayList<File>();
        final File dbDir = getDatabaseDirectory();
        final String dbName = dbDir.getName();
        try {
            fileList.add(createTempFile(dbName + ".1.log.db"));
            fileList.add(createTempFile(dbName + ".data.db"));
            fileList.add(createTempFile(dbName + ".index.db"));
            final String out = DatabaseDirectoryValidator.validateDirectoryForOpeningExistingDatabase(getDatabaseDirectory());
            LOGGER.info("shouldOpenIfEnoughDbFilesInDir: " + out);
            Assert.assertNull(out);
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
