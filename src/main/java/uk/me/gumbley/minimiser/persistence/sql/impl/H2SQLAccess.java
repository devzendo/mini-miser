package uk.me.gumbley.minimiser.persistence.sql.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.h2.command.Parser;
import org.h2.command.Prepared;
import org.h2.command.dml.Delete;
import org.h2.command.dml.Insert;
import org.h2.command.dml.Select;
import org.h2.command.dml.Update;
import org.h2.engine.Session;
import org.h2.engine.SessionInterface;
import org.h2.jdbc.JdbcConnection;
import uk.me.gumbley.minimiser.persistence.sql.BadSQLException;
import uk.me.gumbley.minimiser.persistence.sql.SQLAccess;
import uk.me.gumbley.minimiser.persistence.sql.SQLAccessException;

/**
 * Low-level access to H2 databases.
 * 
 * @author matt
 *
 */
public final class H2SQLAccess implements SQLAccess {
    private static final Logger LOGGER = Logger.getLogger(H2SQLAccess.class);
    private final Map<Class, ResultType> preparedToResultTypeMap;
    private Parser parser;
    private Connection connection;
    
    /**
     * Construct using a datasource
     * @param dataSource the datasource
     */
    public H2SQLAccess(final DataSource dataSource) {
        preparedToResultTypeMap = initialisePreparedToResultTypeMap();
        try {
            connection = dataSource.getConnection();
            //LOGGER.debug("H2SQLAccess: The connection is a " + connection.getClass().getName());
            final JdbcConnection jdbcConnection = (JdbcConnection) connection;
            final SessionInterface session = jdbcConnection.getSession();
            //LOGGER.debug("H2SQLAccess: the session is a " + session.getClass().getName());
            parser = new Parser((Session) session);
            //LOGGER.debug("H2SQLAccess: the parser is a " + parser.getClass().getName());
        } catch (final SQLException e) {
            final String warning = "Could not obtain H2 Parser from DataSource:" + e.getMessage() + "; SQL error code " + e.getErrorCode();
            LOGGER.warn(warning, e);
            throw new SQLAccessException(warning, e);
        }
    }


    private Map<Class, ResultType> initialisePreparedToResultTypeMap() {
        final Map<Class, ResultType> map = new HashMap<Class, ResultType>();
        map.put(Select.class, ResultType.ResultSet);
        map.put(Delete.class, ResultType.Count);
        map.put(Insert.class, ResultType.Count);
        map.put(Update.class, ResultType.Count);
        
        return map;
    }


    /**
     * {@inheritDoc}
     */
    public ResultType parse(final String sql) {
        try {
            final Prepared prepared = parser.parseOnly(sql);
            //LOGGER.info("parse: the Prepared is a " + prepared.getClass().getName());
            //LOGGER.info("parse: the prepared: " + prepared.toString());
            final ResultType resultType = preparedToResultTypeMap.get(prepared.getClass());
            if (resultType != null) {
                //LOGGER.debug("parse: the result type is " + resultType);
                return resultType;
            }
            final String warning = "No ResultType map entry found for Prepared type " + prepared.getClass().getSimpleName();
            LOGGER.warn(warning);
            throw new SQLAccessException(warning);
        } catch (final SQLException e) {
            final String warning = e.getMessage() + "; SQL error code " + e.getErrorCode();
            LOGGER.warn(warning, e);
            throw new BadSQLException(warning, e);
        }
    }


    /**
     * {@inheritDoc}
     */
    public Statement createStatement() {
        try {
            return connection.createStatement();
        } catch (final SQLException e) {
            final String warning = e.getMessage() + "; SQL error code " + e.getErrorCode();
            LOGGER.warn(warning, e);
            throw new SQLAccessException(warning);
        }
    }
}
