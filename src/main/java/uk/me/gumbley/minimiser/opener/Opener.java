package uk.me.gumbley.minimiser.opener;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;

import uk.me.gumbley.minimiser.opener.OpenerAdapter.ProgressStage;
import uk.me.gumbley.minimiser.openlist.OpenDatabaseList;
import uk.me.gumbley.minimiser.persistence.AccessFactory;
import uk.me.gumbley.minimiser.persistence.BadPasswordException;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabase;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabaseDescriptor;

/**
 * The Opener is responsible for opening databases, informing the user and
 * adding the opened database to the open database list.
 * <p>
 * Requests to open are started by the openDatabase call; progress is reported
 * via notifications given to an adapter passed in the call.
 * <p>
 * These notifications progress through several stages:
 * <ol>
 * <li> Starting to open
 * <li> Opening
 * <li> Password required (only for encrypted databases, will retry Opening)
 * <li> Migration to latest schema required (currently unimplemented)
 * <li> Migrating Phase x of y ... (currently unimplemented)
 * <li> Opened OK | Failed to open | Failed to migrate | Password entry cancelled
 * </ol>
 * 
 * The Opener is used by:
 * <ul>
 * <li> the File|Open wizard
 * <li> the startup code to open all databases on the persistent open list
 *      (i.e. those the user had open last time they exited)
 * <li> the Open recent... submenu
 * </ul>
 * 
 * If successfully opened, the Opener adds the opened database to the
 * OpenDatabaseList.
 * <p>
 * The separation of concerns between persistance and display is achieved
 * with the Opener / OpenerAdapter (persistence / display).
 * <p>
 * The notifications can be used to update a progress indicator, either in the
 * File|Open wizard or status bar, depending on what's calling the Opener.
 * <p>
 * The Opener is not responsible for prompting the user for their password, for 
 * requesting confirmation of migration, and informing them of any failures via
 * dialogs. It indicates that it needs these activities performing via the
 * adapter passed in the openDatabase call.
 * <p>
 * @author matt
 *
 */
public final class Opener {
    private static final Logger LOGGER = Logger.getLogger(Opener.class);
    
    private final AccessFactory access;
    private final OpenDatabaseList databaseList;

    /**
     * Construct the Opener.
     * @param accessFactory the access factory used for accessing databases
     * @param openDatabaseList the open database list, which will be added to
     */
    public Opener(final AccessFactory accessFactory, final OpenDatabaseList openDatabaseList) {
        this.access = accessFactory;
        this.databaseList = openDatabaseList;
    }

    /**
     * Open the named database and report progress.
     * 
     * Note that it is not (yet) the responsibility of the Opener to add a
     * successfully-opened database to the OpenDatabaseList.
     * <p>
     * @param dbName the name of the database, typically for display.
     * <p>
     * Typically if called from the UI, this would be the name of the directory
     * that holds the database, and which would form a prefix for the
     * database's files. The path would have this as the last directory part. 
     * <p>
     * If called from a unit test, this would be the name, but the path would
     * be the test database directory.
     * @param pathToDatabase the full path that will be used to open the
     * database by the access factory.
     * @param openerAdapter an adapter that will be notified of the start and
     * end of the open operation, ongoing progress of the open,
     * requests for information (passwords, confirmation for migration),
     * database not found and serious problems.
     * @return null if the database open was cancelled, e.g. if the password
     * was required, but the user cancelled entry of it. If the open succeeds,
     * the MiniMiserDatabase is returned. It is also added to the
     * OpenDatabaseList.
     */
    public MiniMiserDatabase openDatabase(
            final String dbName,
            final String pathToDatabase,
            final OpenerAdapter openerAdapter) {
        openerAdapter.startOpening();
        LOGGER.info("Opening database '" + dbName + "' from path '" + pathToDatabase + "'");
        openerAdapter.reportProgress(ProgressStage.STARTING, "Starting to open");

        // Try at first with an empty password - if we get a BPE, prompt for
        // password and retry.
        String dbPassword = "";
        String tryingToOpenMessage = "Opening database";
        while (true) {
            try {
                openerAdapter.reportProgress(ProgressStage.OPENING, tryingToOpenMessage);
                final MiniMiserDatabase database = access.openDatabase(pathToDatabase, dbPassword);
                LOGGER.info("Opened OK");
        
                openerAdapter.reportProgress(ProgressStage.OPENED, "Opened OK");
                openerAdapter.stopOpening();
                
                databaseList.addOpenedDatabase(new MiniMiserDatabaseDescriptor(dbName, pathToDatabase, database));

                return database;
                
            } catch (final BadPasswordException bad) {
                LOGGER.warn("Bad password: " + bad.getMessage());
                openerAdapter.reportProgress(ProgressStage.PASSWORD_REQUIRED, "Password required");
                dbPassword = openerAdapter.requestPassword();
                if (dbPassword == null || dbPassword.equals("")) {
                    LOGGER.info("Open of encrypted database cancelled");
                    openerAdapter.reportProgress(ProgressStage.PASSWORD_CANCELLED, "Open cancelled");
                    openerAdapter.stopOpening();
                    return null;
                }
                
                tryingToOpenMessage = "Trying to open database";
            } catch (final DataAccessResourceFailureException darfe) {
                LOGGER.warn("Could not open database: " + darfe.getMessage());
                openerAdapter.reportProgress(ProgressStage.NOT_PRESENT, "Database not found");
                openerAdapter.databaseNotFound(darfe);
                openerAdapter.stopOpening();
                return null;
                
            } catch (final DataAccessException dae) {
                LOGGER.warn("Data access exception opening database: " + dae.getMessage(), dae);
                openerAdapter.reportProgress(ProgressStage.OPEN_FAILED, "Open failed");
                openerAdapter.seriousProblemOccurred(dae);
                openerAdapter.stopOpening();
                return null;
            }
        }
    }
}
