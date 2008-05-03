package uk.me.gumbley.minimiser.persistence;

/**
 * This is the entry point into the persistence layer. It allows access to
 * given databases in two ways:
 * <ul>
 * <li> MigratableDatabase, which uses lower level (JDBC) operations to access
 *      the version table of the v1 schema, and migrate a database to the
 *      latest schema version.
 * <li> HibernateDatabase, which uses Hibernate to access a database at a
 *      specific schema version using ORM.
 * </ul>  
 * @author matt
 *
 */
public interface AccessFactory {

    /**
     * Obtain low level access to a given database for version checking and
     * migration.
     * @param directory the directory containing the database
     * @param password an optional password for encrypted databases; ignored
     * for unencrypted databases
     * @return a MigratableDatabase facade for migration operations
     * @throws BadPasswordException if the database is encrypted and the
     * supplied password is incorrect, or is null/empty. 
     * This is not thrown if a password is supplied for unencrypted databases.
     */
    MigratableDatabase openMigratableDatabase(String directory, String password) throws BadPasswordException;
}
