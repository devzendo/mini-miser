package uk.me.gumbley.minimiser.persistence.impl;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

import uk.me.gumbley.minimiser.persistence.MiniMiserDAOFactory;
import uk.me.gumbley.minimiser.persistence.dao.SequenceDao;
import uk.me.gumbley.minimiser.persistence.dao.VersionDao;
import uk.me.gumbley.minimiser.persistence.dao.impl.JdbcTemplateSequenceDao;
import uk.me.gumbley.minimiser.persistence.dao.impl.JdbcTemplateVersionDao;
import uk.me.gumbley.minimiser.persistence.sql.SQLAccess;
import uk.me.gumbley.minimiser.persistence.sql.impl.H2SQLAccess;

/**
 * A MiniMiser DAO factory that uses a SimpleJdbcTemplate
 * 
 * @author matt
 *
 */
public final class JdbcTemplateDAOFactoryImpl implements MiniMiserDAOFactory {
    private static final Logger LOGGER = Logger.getLogger(JdbcTemplateDAOFactoryImpl.class);
    
    @SuppressWarnings("unused")
    private final String dbURL;
    @SuppressWarnings("unused")
    private final String dbPath;
    private final SimpleJdbcTemplate jdbcTemplate;
    private volatile boolean isClosed = true;
    private final VersionDao versionDao;
    private final SequenceDao sequenceDao;
    private final DataSource dataSource;
    private SQLAccess sqlAccess;

    /**
     * @param url the database URL
     * @param path the path to the database for display
     * @param template the SimpleJdbcTemplate to access this database with
     * @param source the dataSource to access this databse with
     */
    public JdbcTemplateDAOFactoryImpl(final String url, final String path, final SimpleJdbcTemplate template, final DataSource source) {
        this.dbURL = url;
        this.dbPath = path;
        this.jdbcTemplate = template;
        this.dataSource = source;
        isClosed = false;
        versionDao = new JdbcTemplateVersionDao(jdbcTemplate);
        sequenceDao = new JdbcTemplateSequenceDao(jdbcTemplate);
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        if (isClosed) {
            return;
        }
        try {
            DataSourceUtils.getConnection(dataSource).close();
            isClosed = true;
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

    /**
     * {@inheritDoc}
     */
    public SequenceDao getSequenceDao() {
        checkClosed("getSequenceDao");
        return sequenceDao;
    }
    
    private void checkClosed(final String method) {
        if (isClosed) {
            throw new IllegalStateException(String.format("Cannot call %s with a closed database", method));
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isClosed() {
        if (isClosed) {
            return true;
        }
        try {
            return DataSourceUtils.getConnection(dataSource).isClosed();
        } catch (final CannotGetJdbcConnectionException e) {
            LOGGER.warn("Can't get JDBC Connection on isClosed: " + e.getMessage(), e);
        } catch (final SQLException e) {
            LOGGER.warn("SQL Exception on isClosed: " + e.getMessage(), e);
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public SQLAccess getSQLAccess() {
        synchronized (this) {
            checkClosed("getSQLAccess");
            if (sqlAccess == null) {
                sqlAccess = new H2SQLAccess(dataSource);
            }
            return sqlAccess;
        }
    }
}
