package uk.me.gumbley.minimiser.persistence.impl;

import uk.me.gumbley.minimiser.persistence.AccessFactory;
import uk.me.gumbley.minimiser.persistence.BadPasswordException;
import uk.me.gumbley.minimiser.persistence.DataAccessException;
import uk.me.gumbley.minimiser.persistence.HibernateAccess;
import uk.me.gumbley.minimiser.persistence.JdbcAccess;
import uk.me.gumbley.minimiser.persistence.MigratableDatabase;

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
            throws BadPasswordException, DataAccessException {
        return jdbcAccess.openDatabase(databasePath, password);
    }

}
