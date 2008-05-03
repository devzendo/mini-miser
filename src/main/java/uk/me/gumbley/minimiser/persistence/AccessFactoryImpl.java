package uk.me.gumbley.minimiser.persistence;

/**
 * The default implementation of the AccessFactory.
 * 
 * @author matt
 *
 */
public final class AccessFactoryImpl implements AccessFactory {

    private final JdbcAccess jdbcAccess;
    private final HibernateAccess hibernateAccess;

    /**
     * Create the factory for database access
     * @param jdbcAcc a delegate object for low level operations
     * @param hibernateAcc a delegate object for high-level access
     */
    public AccessFactoryImpl(final JdbcAccess jdbcAcc, final HibernateAccess hibernateAcc) {
        this.jdbcAccess = jdbcAcc;
        this.hibernateAccess = hibernateAcc;
    }

    /**
     * {@inheritDoc}
     */
    public MigratableDatabase openMigratableDatabase(final String databasePath, final String password)
            throws BadPasswordException {
        return jdbcAccess.openDatabase(databasePath, password);
    }

}
