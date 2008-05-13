package uk.me.gumbley.minimiser.persistence.impl;

import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.me.gumbley.minimiser.persistence.MigratableDatabase;

/**
 * A MigratableDatabase using Spring's JdbcTemplate.
 * 
 * @author matt
 *
 */
public class JdbcTemplateMigratableDatabaseImpl implements MigratableDatabase {
    @SuppressWarnings("unused")
    private static final Logger LOGGER = Logger
            .getLogger(JdbcTemplateMigratableDatabaseImpl.class);

    @SuppressWarnings("unused") // TODO remove suppression when we do use them!
    private final String dbURL;

    @SuppressWarnings("unused")
    private final String dbPath;

    @SuppressWarnings("unused")
    private final JdbcTemplate jdbcTemplate;

    /**
     * Create a MigratableDatabase using Spring's JdbcTemplate.
     * 
     * @param url the URL to the database
     * @param databasePath the path of the database (for display)
     * @param template the Spring template
     */
    public JdbcTemplateMigratableDatabaseImpl(final String url,
            final String databasePath, final JdbcTemplate template) {
        this.dbURL = url;
        this.dbPath = databasePath;
        this.jdbcTemplate = template;
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        // TODO how do we close the JdbcTemplate? WTF?!!
        try {
            jdbcTemplate.getDataSource().getConnection().close();
        } catch (final SQLException e) {
            LOGGER.warn("Could not close connection:" + e.getMessage(), e);
        }
    }
}
