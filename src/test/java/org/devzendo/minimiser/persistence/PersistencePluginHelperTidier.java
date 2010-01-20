package org.devzendo.minimiser.persistence;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

/**
 * Handles the closing and tidying up after test databases have
 * been created.
 *  
 * @author matt
 *
 */
public final class PersistencePluginHelperTidier {
    private static final Logger LOGGER = Logger
            .getLogger(PersistencePluginHelperTidier.class);
    private final HashSet<String> mCreatedDatabaseNames;
    private final HashMap<String, MiniMiserDAOFactory> mOpenedDatabases;
    private final File mTestDatabaseDirectory;

    /**
     * Construct the tidier.
     * @param testDatabaseDirectory the directory used to create
     * test databases.
     */
    public PersistencePluginHelperTidier(final File testDatabaseDirectory) {
        mTestDatabaseDirectory = testDatabaseDirectory;
        mCreatedDatabaseNames = new HashSet<String>();
        mOpenedDatabases = new HashMap<String, MiniMiserDAOFactory>();
    }

    /**
     * Close and remove all databases that have been recorded. 
     */
    public void tidy() {
        closeOpenDatabases();
        deleteCreatedDatabases();
    }

    private void deleteCreatedDatabases() {
        LOGGER.info("Deleting files for databases " + mCreatedDatabaseNames);
        for (final String dbName : mCreatedDatabaseNames) {
            int count = 0;
            boolean allGone = true;
            LOGGER.info(String.format("Deleting database '%s' files", dbName));
            if (mTestDatabaseDirectory != null && mTestDatabaseDirectory.exists()
                    && mTestDatabaseDirectory.isDirectory()) {
                final FileFilter filter = new FileFilter() {
                    public boolean accept(final File pathname) {
                        LOGGER.debug(String.format("Considering %s", pathname.getAbsolutePath()));
                        return pathname.isFile() && pathname.getName().startsWith(dbName);
                    }
                };
                final File[] dbFiles = mTestDatabaseDirectory.listFiles(filter);
                count = dbFiles.length;
                LOGGER.debug("count is " + count);
                for (File file : dbFiles) {
                    LOGGER.debug(String.format("Deleting %s", file.getAbsoluteFile()));
                    final boolean gone = file.delete();
                    allGone &= gone;
                    if (!gone) {
                        LOGGER.warn(String.format("Could not delete %s", file.getAbsolutePath()));
                    }
                }
            }
            if (count == 0) {
                final String err = "No files to delete, when some were expected";
                LOGGER.warn(err);
                throw new IllegalStateException(err);
            }
            if (!allGone) {
                final String err = "Some files failed to delete";
                LOGGER.warn(err);
                throw new IllegalStateException(err);
            }
        }
    }

    private void closeOpenDatabases() {
        LOGGER.info("Closing any open databases");
        for (Entry<String, MiniMiserDAOFactory> entry : mOpenedDatabases.entrySet()) {
            LOGGER.info("Closing " + entry.getKey());
            if (entry.getValue().isClosed()) {
                LOGGER.info(" (already closed)");
            } else {
                entry.getValue().close();
            }
        }
    }

    /**
     * Add a database to the set that are to be deleted by
     * deleteCreatedDatabases. For instance if you're using two
     * instances of the helper, one to create a database with an
     * old schema, using an "old" plugin, and one to verify
     * migration to a new schema, using a "new plugin".
     * @param dbName the name of the database to add to the
     * deletion set.
     * @param miniMiserDAOFactory a MiniMiserDAOFactory used to
     * close the database if open, prior to deletion
     */
    public void addDatabaseToDelete(
            final String dbName,
            final MiniMiserDAOFactory miniMiserDAOFactory) {
        mCreatedDatabaseNames.add(dbName);
        if (miniMiserDAOFactory == null) {
            LOGGER.warn("dbname has null MiniMiserDAOFactory");
        } else {
            mOpenedDatabases.put(dbName, miniMiserDAOFactory);
        }
    }

    /**
     * Add a database to the set that are to be deleted by
     * deleteCreatedDatabases. For instance if you're using two
     * instances of the helper, one to create a database with an
     * old schema, using an "old" plugin, and one to verify
     * migration to a new schema, using a "new plugin".
     * @param dbName the name of the database to add to the
     * deletion set.
     */
    public void addDatabaseToDelete(final String dbName) {
        mCreatedDatabaseNames.add(dbName);
    }

}
