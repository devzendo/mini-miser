package uk.me.gumbley.minimiser.persistence.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import uk.me.gumbley.minimiser.persistence.BadPasswordException;
import uk.me.gumbley.minimiser.persistence.DataAccessException;
import uk.me.gumbley.minimiser.persistence.JdbcAccess;
import uk.me.gumbley.minimiser.persistence.MigratableDatabase;

public class JdbcAccessImpl implements JdbcAccess {
    private static final Logger LOGGER = Logger.getLogger(JdbcAccessImpl.class);
    // TODO find the appropriate Spring JDBC exceptions for this signature 
    public MigratableDatabase openDatabase(String databasePath, String password)
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
}
