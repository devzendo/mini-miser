package uk.me.gumbley.minimiser.persistence.impl;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import uk.me.gumbley.minimiser.persistence.MigratableDatabase;

/**
 * A MigratableDatabase that uses straight JDBC.
 * 
 * @author matt
 *
 */
public class StraightMigratableDatabaseImpl implements MigratableDatabase {
    private static final Logger LOGGER = Logger
            .getLogger(StraightMigratableDatabaseImpl.class);

    private final Connection dbConnection;

    @SuppressWarnings("unused") // TODO remove when we do use it
    private final String dbURL;

    private final String dbPath;

    /**
     * Create a MigratableDatabase using JDBC
     * @param url the URL to the database
     * @param databasePath the path to the database (for display)
     * @param connection the JDBC Connection to the database
     */
    public StraightMigratableDatabaseImpl(final String url, 
            final String databasePath, final Connection connection) {
        this.dbURL = url;
        this.dbPath = databasePath;
        this.dbConnection = connection;
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        try {
            if (dbConnection != null) {
                dbConnection.close();
            }
        } catch (SQLException e) {
            LOGGER.warn(String.format("Could not close database at %s: %s", dbPath, e.getMessage()));
        }
    }
}
