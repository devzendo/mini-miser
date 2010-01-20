package org.devzendo.minimiser.persistence.sql;

import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * The SQLAccess interface provides low-level direct SQL access to the
 * database, for migration, and validating/parsing SQL statements.
 *  
 * @author matt
 *
 */
public interface SQLAccess {

    /**
     * Describes the sort of output a valid SQL statement would produce. 
     * @author matt
     *
     */
    enum ResultType {
        /**
         * A ResultSet is returned. 
         */
        ResultSet,
        /**
         * An indication of the success or failure of the statement is
         * returned.
         */
        SuccessFailure,
        /**
         * A count of the number of records changed is returned.
         */
        RowCount,
        /**
         * A numeric result
         */
        Number
    }
    
    /**
     * Parse a piece of SQL and if valid, return an indication of the type
     * of output the SQL would return. Will throw a runtime BadSQLException
     * on parse failure.
     * @param sql the SQL (DML or DDL)
     * @return the ResultType.
     */
    ResultType parse(String sql);
    
    /**
     * Obtain the DataSource, for low-level access. 
     * @return the DataSource
     */
    DataSource getDataSource();

    /**
     * Create a JDBC Statement.
     * @return the Statement
     */
    Statement createStatement();

    /**
     * Obtain the Spring SimpleJdbcTemplate to make easier use of
     * JDBC.
     * @return the SimpleJdbcTemplate
     */
    SimpleJdbcTemplate getSimpleJdbcTemplate();

    /**
     * Create a new Transactiontemplate with which work can be
     * done, then committed or rolled back.
     * @return the TransactionTemplate
     */
    TransactionTemplate createTransactionTemplate();
}
