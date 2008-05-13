package uk.me.gumbley.minimiser.persistence.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessResourceFailureException;
import uk.me.gumbley.minimiser.persistence.AccessFactory;
import uk.me.gumbley.minimiser.persistence.MigratableDatabase;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabase;

/**
 * A JdbcAccess implementation that uses JDBC directly.
 *
 * @author matt
 *
 */
public final class StraightJdbcAccess implements AccessFactory {
    private static final Logger LOGGER =
        Logger.getLogger(StraightJdbcAccess.class);

    /**
     * Constructor.
     */
    public StraightJdbcAccess() {
    }

    /**
     * {@inheritDoc}
     */
    public MigratableDatabase openMigratableDatabase(final String databasePath,
            final String password) {
        // how do we get Spring to instantiate all this?
        // or do we just do it by hand?

        // Generate a DataSource
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            String str = "H2 Database driver not found";
            LOGGER.error(str);
            throw new DataAccessResourceFailureException(str);
        }
        String dbPassword = (password == null) ? "" : password;
        String dbURL = dbPassword.length() == 0 ?
                String.format("jdbc:h2:%s;IFEXISTS=TRUE", databasePath) :
                String.format("jdbc:h2:%s;CIPHER=AES;IFEXISTS=TRUE");
        try {
            Connection connection = DriverManager.getConnection(dbURL, "sa", dbPassword);
            return new StraightMigratableDatabaseImpl(dbURL, databasePath, connection);
        } catch (SQLException e) {
            // TODO differentiate between bad password and other errors
            String str = String.format("Could not open connection to database at %s: %s", databasePath, e.getMessage());
            LOGGER.warn(str);
            throw new DataAccessResourceFailureException(str);
        }
    }

    public MiniMiserDatabase createDatabase(String databasePath, String password) {
        // TODO Auto-generated method stub perhaps we ditch this class?
        return null;
    }
}
