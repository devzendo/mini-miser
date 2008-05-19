package uk.me.gumbley.minimiser.persistence.impl;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
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
    private final SimpleJdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
    private volatile boolean isClosed = true;

    /**
     * Create a MigratableDatabase using Spring's SimpleJdbcTemplate.
     * 
     * @param url the URL to the database
     * @param databasePath the path of the database (for display)
     * @param template the Spring template
     * @param source the DataSource to use
     */
    public JdbcTemplateMigratableDatabaseImpl(final String url,
            final String databasePath, final SimpleJdbcTemplate template,
            final DataSource source) {
        this.dbURL = url;
        this.dbPath = databasePath;
        this.jdbcTemplate = template;
        this.dataSource = source;
        isClosed = false;
    }

    /**
     * {@inheritDoc}
     */
    public final void close() {
        if (isClosed) {
            return;
        }
        isClosed = true;
        try {
            DataSourceUtils.getConnection(dataSource).close();
        } catch (final SQLException e) {
            LOGGER.warn("Could not close connection:" + e.getMessage(), e);
        }
    }
}
