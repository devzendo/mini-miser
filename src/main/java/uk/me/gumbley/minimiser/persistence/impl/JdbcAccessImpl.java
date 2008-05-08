package uk.me.gumbley.minimiser.persistence.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.h2.constant.ErrorCode;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.MetaDataAccessException;

import uk.me.gumbley.minimiser.persistence.BadPasswordException;
import uk.me.gumbley.minimiser.persistence.DataAccessException;
import uk.me.gumbley.minimiser.persistence.JdbcAccess;
import uk.me.gumbley.minimiser.persistence.MigratableDatabase;
import uk.me.gumbley.minimiser.springloader.SpringLoader;

public class JdbcAccessImpl implements JdbcAccess {
    private final SpringLoader springLoader;
    public JdbcAccessImpl(final SpringLoader sL) {
        springLoader = sL;
        
    }
    private static final Logger LOGGER = Logger.getLogger(JdbcAccessImpl.class);
    // TODO find the appropriate Spring JDBC exceptions for this signature 
    public MigratableDatabase openDatabaseX(String databasePath, String password)
            throws BadPasswordException, DataAccessException {
        // how do we get Spring to instantiate all this?
        // or do we just do it by hand?
        
        // TODO add the Spring JDBC datasource and template artifacts to the pom
        
        // Generate a DataSource
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            String str = "H2 Database driver not found";
            LOGGER.error(str);
            throw new DataAccessException(str);
        }
        String dbPassword = (password == null) ? "" : password;
        String dbURL = dbPassword.length() == 0 ?
                String.format("jdbc:h2:%s;IFEXISTS=TRUE", databasePath) :
                String.format("jdbc:h2:%s;CIPHER=AES;IFEXISTS=TRUE");
        try {
            Connection connection = DriverManager.getConnection(dbURL, "sa", dbPassword);
            return new MigratableDatabaseImpl(dbURL, databasePath, connection);
        } catch (SQLException e) {
            // TODO differentiate between bad password and other errors
            String str = String.format("Could not open connection to database at %s: %s", databasePath, e.getMessage());
            LOGGER.warn(str);
            throw new DataAccessException(str);
        }
    }
    public MigratableDatabase openDatabase(String databasePath, String password)
    throws BadPasswordException, DataAccessException {
        String dbPassword = (password == null) ? "" : password;
        String dbURL = dbPassword.length() == 0 ?
                String.format("jdbc:h2:%s;IFEXISTS=TRUE", databasePath) :
                String.format("jdbc:h2:%s;CIPHER=AES;IFEXISTS=TRUE");
        // We can populate half the data source from Spring, but we need to
        // build the URL at runtime, since it depends on the database path and
        // password.
        SingleConnectionDataSource dataSource = springLoader.getBean("dataSource", SingleConnectionDataSource.class);
        dataSource.setUrl(dbURL);
        dataSource.setPassword(dbPassword);
        // Just instantiate directly rather than bothering with Spring
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource, false);
        // Possible Spring bug: if the database isn't there, it doesn't throw
        // an (unchecked) exception. - it does detect it and logs voluminously,
        // but then doesn't pass the error on to me.
        // Looks like a 90013 (DATABASE_HOT_FOUND_1) isn't mapped by the default
        // Spring sql-error-codes.xml.
        // So, I have to check myself. (Obviating one of the reasons I chose Spring!)
        try {
            dataSource.getConnection().close();
        } catch (SQLException e) {
            if (e.getErrorCode() == ErrorCode.DATABASE_NOT_FOUND_1) {
                throw new DataAccessResourceFailureException(String.format("Database at %s not found", databasePath));
            }
            // Assume that anything that goes wrong here is bad...
            throw new org.springframework.jdbc.UncategorizedSQLException(String.format("Possible database not found - SQL Error Code %d", e.getErrorCode()), "", e);
        }
        LOGGER.info("got to here - it doesn't throw at all, but logs voluminously");
        return new MigratableDatabaseImpl(dbURL, databasePath, jdbcTemplate);
    }

}
