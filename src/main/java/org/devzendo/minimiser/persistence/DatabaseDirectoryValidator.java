package org.devzendo.minimiser.persistence;

import java.io.File;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.string.StringUtils;


/**
 * Toolkit for validating that a directory might be
 * suitable for holding a new database, or that it
 * holds an existing database.
 * 
 * @author matt
 *
 */
public final class DatabaseDirectoryValidator {
    private static final Logger LOGGER = Logger
            .getLogger(DatabaseDirectoryValidator.class);
    /**
     * No instantiations. 
     */
    private DatabaseDirectoryValidator() {
    }

    /**
     * Is a directory suitable for a new database?
     * @param dir the directory in question
     * @return a String describing why the directory is not suitable, or null if it is suitable.
     */
    public static String validateDirectoryForDatabaseCreation(final File dir) {
        if (dir == null) {
            return "You must create a new folder to hold this database";
        }
        if (!dir.exists()) {
            return "The '" + dir.getName() + "' folder does not exist";
        }
        if (!dir.isDirectory()) {
            return "'" + dir.getName() + "' is not a folder";
        }
        final int numFiles = dir.list().length;
        if (numFiles > 0) {
            return "The folder must be empty - there "
            + StringUtils.getAreIs(numFiles) + " " + numFiles + " "
            + StringUtils.pluralise("file", numFiles)
            + " in the " + dir.getName() + " folder";
        }
        return null;
    }

    /**
     * Does a directory contain an existing database?
     * @param dir the directory in question
     * @return a String describing why the directory does not appear to contain
     * an existing database, or null if it is suitable.
     */
    public static String validateDirectoryForOpeningExistingDatabase(final File dir) {
        LOGGER.debug("validating " + dir + " for opening existing database");
        if (dir == null) {
            return "You must choose an existing database folder";
        }
        final String directoryName = dir.getName();
        if (!dir.exists()) {
            return "The '" + directoryName + "' folder does not exist";
        }
        if (!dir.isDirectory()) {
            return "'" + directoryName + "' is not a folder";
        }
        // TODO what about lock files?
        // TODO what if we already have this db open?
        int numCorrectlyNamedFiles = 0;
        final String[] fileList = dir.list();
        for (String fileName : fileList) {
            LOGGER.debug("Examining file '" + fileName + "'");
            if (fileName.startsWith(directoryName + '.') && fileName.endsWith(".db")) {
                numCorrectlyNamedFiles++;
            }
        }
        if (numCorrectlyNamedFiles < 3) {
            return "The folder must contain existing database files";
        }
        return null;
    }
    
}
