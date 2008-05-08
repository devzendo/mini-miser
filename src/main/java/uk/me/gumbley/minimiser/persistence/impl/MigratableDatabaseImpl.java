package uk.me.gumbley.minimiser.persistence.impl;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import uk.me.gumbley.minimiser.persistence.MigratableDatabase;

public class MigratableDatabaseImpl implements MigratableDatabase {
    private static final Logger LOGGER = Logger
            .getLogger(MigratableDatabaseImpl.class);

    private final Connection connection;

    private final String dbURL;

    private final String databasePath;

    private final JdbcTemplate jdbcTemplate;

    public MigratableDatabaseImpl(String dbURL, String databasePath, Connection connection) {
        this.dbURL = dbURL;
        this.databasePath = databasePath;
        this.connection = connection;
        this.jdbcTemplate = null;
    }

    public MigratableDatabaseImpl(String dbURL, String databasePath, JdbcTemplate jdbcTemplate) {
        this.dbURL = dbURL;
        this.databasePath = databasePath;
        this.jdbcTemplate = jdbcTemplate;
        this.connection = null;
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            LOGGER.warn(String.format("Could not close database at %s: %s", databasePath, e.getMessage()));
        }
    }
}
