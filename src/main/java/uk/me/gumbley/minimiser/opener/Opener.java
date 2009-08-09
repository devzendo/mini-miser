package uk.me.gumbley.minimiser.opener;

import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.persistence.DAOFactory;
import uk.me.gumbley.minimiser.util.InstanceSet;

/**
 * The Opener is responsible for opening databases, informing the user and
 * notifying observers of newly opened databases. Typical listeners would
 * add these to the open database list.
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
 * If successfully opened, the Opener notifies observers.
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
public interface Opener {
    /**
     * Add an observer of database open events.
     * @param observer the observer to add.
     */
    void addDatabaseOpenObserver(final Observer<DatabaseOpenEvent> observer);

    /**
     * Open the named database and report progress.
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
     * the MiniMiserDatabase is returned. Observers of DatabaseOpenEvents are
     * notified. Null is also returned if the database cannot be found;
     * observers are notified of the failure.
     */
    InstanceSet<DAOFactory> openDatabase(
            final String dbName,
            final String pathToDatabase,
            final OpenerAdapter openerAdapter);
}