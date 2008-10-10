package uk.me.gumbley.minimiser.persistence.impl;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.h2.command.Parser;
import org.h2.command.Prepared;
import org.h2.engine.Session;
import org.h2.engine.SessionInterface;
import org.h2.jdbc.JdbcConnection;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
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
    
    // WOZERE - this needs tests writing!
    // have hacked up testGetVersionDaoFailsOnClosedDatabase to call this and
    // manually see what happens.
    public Prepared parse(final String sql) {
        try {
            Connection connection = dataSource.getConnection();
            LOGGER.info("parse: The connection is a " + connection.getClass().getName());
            JdbcConnection jdbcConnection = (JdbcConnection) connection;
            SessionInterface session = jdbcConnection.getSession();
            LOGGER.info("parse: the session is a " + session.getClass().getName());
            Parser parser = new Parser((Session) session);
            LOGGER.info("parse: the parser is a " + parser.getClass().getName());
            Prepared prepared = parser.parseOnly(sql);
            LOGGER.info("parse: the Prepared is a " + prepared.getClass().getName());
            LOGGER.info("parse: the prepared: " + prepared.toString());
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
