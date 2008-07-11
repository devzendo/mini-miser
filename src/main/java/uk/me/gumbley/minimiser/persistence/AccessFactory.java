package uk.me.gumbley.minimiser.persistence;

import uk.me.gumbley.commoncode.patterns.observer.Observer;

/**
 * This is the entry point into the persistence layer.
 * 
 * It allows access to given databases via a MiniMiserDatabase instance, which
 * uses JDBC + JdbcTemplate to access a database at the latest schema version,
 * for normal application usage.
 * 
 * Databases can be created, or opened.
 * 
 * The MiniMiser object also allows lower-level (DDL) execution, so as to
 * perform a migration from the database's current schema version to the latest. 
 *      
 * Historical note: the initial thoughts on architecture were that I should
 *      use Hibernate for this access method, but the Spring docs and course
 *      notes suggest that for many apps, JDBC + JdbcTemplate is just fine. I
 *      started with a straight JDBC version, but it was too painful, so now I
 *      just have the JdbcTemplate version.
 *      
 * @author matt
 *
 */
public interface AccessFactory {

    /**
     * Open an existing database for normal use.
     * 
     * Note that the database may not be at the current schema version, so the
     * migration code should be invoked first to update it, before the main DAO
     * objects are used.
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
     * @return a MiniMiserDatabase, from which DAOs can be obtained, and DDL
     * executed. 
     */
    MiniMiserDatabase openDatabase(String directory, String password);
    
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
    MiniMiserDatabase createDatabase(String databasePath, String password,
            Observer<PersistenceObservableEvent> observer);

    /**
     * For displaying progress bars during database creation, we need to know
     * how many steps are involved. Each step might take some small amount
     * of time, so that after each step is complete, the user could discern
     * the creation progress bar increasing.
     * This number of steps is dependent on the number of tables and other
     * administrative activities involved.
     * @return the numebr of database creation steps.
     */
    int getNumberOfDatabaseCreationSteps();
}
