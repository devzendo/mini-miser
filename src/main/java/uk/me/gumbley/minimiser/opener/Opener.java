package uk.me.gumbley.minimiser.opener;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import uk.me.gumbley.minimiser.opener.OpenerAdapter.ProgressStage;
import uk.me.gumbley.minimiser.persistence.AccessFactory;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabase;

/**
 * The Opener is responsible for opening databases.
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
 * The Opener does <em>not</em> add the opened database to the OpenDatabaseList,
 * rather, it is returned by the openDatabase call, and it's up to the
 * calling code to do this.
 * <p>
 * Furthermore the Opener does not set/clear the hourglass cursor, again, it's
 * up to the caller to do this around Opener activity.
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
public class Opener {
    private static final Logger LOGGER = Logger.getLogger(Opener.class);
    
    private final AccessFactory databaseAccessFactory;

    /**
     * Construct the Opener.
     * @param accessFactory the access factory used for accessing databases
     */
    public Opener(final AccessFactory accessFactory) {
        this.databaseAccessFactory = accessFactory;
    }

    /**
     * Open the named database and report progress. Note that it is not the
     * responsibility of the Opener to add a successfully-opened database to
     * the OpenDatabaseList.
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
     * @param openerAdapter an adapter that will be notified of open progress
     * and requests for information (passwords, confirmation for migration).
     * @return null if the database open was cancelled, e.g. if the password
     * was required, but the user cancelled entry of it. If the open succeeds,
     * the MiniMiserDatabase is returned.
     * @throws DataAccessException upon database open failure, other than a 
     * bad password, which will be prompted for. 
     * <p>
     * Note that this should be
     * treated as a very bad problem (database corruption?.
     * <p>
     * Note that if you receive the subclass DataAccessResourceFailureException
     * then this indicates that the database isn't there. 
     */
    public MiniMiserDatabase openDatabase(
            final String dbName,
            final String pathToDatabase,
            final OpenerAdapter openerAdapter) throws DataAccessException {
        LOGGER.info("Opening database '" + dbName + "' from path '" + pathToDatabase + "'");
        openerAdapter.reportProgress(ProgressStage.STARTING, "Starting to open");
        // TODO Auto-generated method stub
        return null;
    }
}
