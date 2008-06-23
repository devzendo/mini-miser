package uk.me.gumbley.minimiser.persistence;

import uk.me.gumbley.commoncode.patterns.observer.Observer;

/**
 * This is the entry point into the persistence layer. It allows access to
 * given databases in two ways:
 * <ul>
 * <li> MigratableDatabase, which uses JDBC + JdbcTemplate operations to access
 *      the version table of the v1 schema, and migrate a database to the
 *      latest schema version.
 * <li> MiniMiserDatabase, which uses JDBC + JdbcTemplate to access a database
 *      at the latest schema verison, for normal application usage.
 *      
 *      Note that the initial thoughts on architecture were that I should
 *      use Hibernate for this access method, but the Spring docs and course
 *      notes suggest that for many apps, JDBC + JdbcTemplate is just fine.
 * </ul>  
 * @author matt
 *
 */
public interface AccessFactory {

    /**
     * Obtain low level access to a given database for version checking and
     * migration.
     * 
     * Can throw runtime exceptions:
     * <ul>
     * <li> BadPasswordException a runtime exception indicating you got the
     * password wrong.
     * This is not thrown if a password is supplied for unencrypted databases.
     * <li> DataAccessResourceFailureException a [Spring] runtime exception
     * indicating that the database (probably) doesn't exist.
     * </ul>
     *  
     * @param directory the directory containing the database
     * @param password an optional password for encrypted databases; ignored
     * for unencrypted databases
     * @return a MigratableDatabase facade for migration operations
     */
    MigratableDatabase openMigratableDatabase(String directory, String password);

    /**
     * Open an existing, current-schema database for normal use.
     * The typical use case is that openMigratableDatabase has been done,
     * the MigratableDatabase has been used to determine that the database
     * exists and is at the correct schema version, and has then been closed.
     * This call is then made to allow normal access to the database.
     * TODO write this
     */
    
    /**
     * Create a database for normal use.
     * 
     * @param databasePath the path to the database.
     * @param password the password, if the database is encrypted, or,
     * if not encrypted, this can be the empty string or null.
     * @return a MiniMiserDatabase object allowing you to access the
     * database.
     */
    MiniMiserDatabase createDatabase(String databasePath, String password);

    /**
     * Create a database for normal use, listening for events during creation.
     * 
     * @param databasePath the path to the database.
     * @param password the password, if the database is encrypted, or,
     * if not encrypted, this can be the empty string or null.
     * @param observer the listener of creation events.
     * @return a MiniMiserDatabase object allowing you to access the
     * database.
     */
    MiniMiserDatabase createDatabase(String dbDirPlusDbName, String string, Observer<PersistenceObservableEvent> observer);
}
