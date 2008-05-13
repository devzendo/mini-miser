package uk.me.gumbley.minimiser.persistence.impl;

import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import uk.me.gumbley.minimiser.persistence.MiniMiserDatabase;
import uk.me.gumbley.minimiser.persistence.dao.VersionDao;
import uk.me.gumbley.minimiser.persistence.dao.impl.JdbcTemplateVersionDao;

/**
 * A MiniMiserDatabase DAO factory that uses a JdbcTemplate
 * @author matt
 *
 */
public class JdbcTemplateMiniMiserDatabaseImpl implements MiniMiserDatabase {
    private static final Logger LOGGER = Logger.getLogger(JdbcTemplateMiniMiserDatabaseImpl.class);
    
    private final String dbURL;
    private final String dbPath;
    private final JdbcTemplate jdbcTemplate;
    private volatile boolean isClosed = true;
    private final VersionDao versionDao;

    /**
     * @param url the database URL
     * @param path the path to the database for display
     * @param template the JdbcTemplate to access this database with
     */
    public JdbcTemplateMiniMiserDatabaseImpl(final String url, final String path, final JdbcTemplate template) {
        this.dbURL = url;
        this.dbPath = path;
        this.jdbcTemplate = template;
        versionDao = new JdbcTemplateVersionDao(jdbcTemplate);
        isClosed = false;
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        isClosed = true;
        try {
            DataSourceUtils.getConnection(jdbcTemplate.getDataSource()).close();
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
