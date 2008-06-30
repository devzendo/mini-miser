package uk.me.gumbley.minimiser.persistence.impl;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabase;
import uk.me.gumbley.minimiser.persistence.dao.VersionDao;
import uk.me.gumbley.minimiser.persistence.dao.impl.JdbcTemplateVersionDao;

/**
 * A MiniMiserDatabase DAO factory that uses a SimpleJdbcTemplate
 * 
 * @author matt
 *
 */
public final class JdbcTemplateMiniMiserDatabaseImpl implements MiniMiserDatabase {
    private static final Logger LOGGER = Logger.getLogger(JdbcTemplateMiniMiserDatabaseImpl.class);
    
    @SuppressWarnings("unused")
    private final String dbURL;
    @SuppressWarnings("unused")
    private final String dbPath;
    private final SimpleJdbcTemplate jdbcTemplate;
    private volatile boolean isClosed = true;
    private final VersionDao versionDao;
    private final DataSource dataSource;

    /**
     * @param url the database URL
     * @param path the path to the database for display
     * @param template the SimpleJdbcTemplate to access this database with
     * @param source the dataSource to access this databse with
     */
    public JdbcTemplateMiniMiserDatabaseImpl(final String url, final String path, final SimpleJdbcTemplate template, final DataSource source) {
        this.dbURL = url;
        this.dbPath = path;
        this.jdbcTemplate = template;
        this.dataSource = source;
        isClosed = false;
        versionDao = new JdbcTemplateVersionDao(jdbcTemplate);
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        if (isClosed) {
            return;
        }
        isClosed = true;
        try {
            DataSourceUtils.getConnection(dataSource).close();
        } catch (final CannotGetJdbcConnectionException e) {
            LOGGER.warn("Can't get JDBC Connection on close: " + e.getMessage(), e);
        } catch (final SQLException e) {
            LOGGER.warn("SQL Exception on close: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public VersionDao getVersionDao() {
        checkClosed("getVersionDao");
        return versionDao;
    }

    private void checkClosed(final String method) {
        if (isClosed) {
            throw new IllegalStateException(String.format("Cannot call %s with a closed database", method));
        }
    }
}
