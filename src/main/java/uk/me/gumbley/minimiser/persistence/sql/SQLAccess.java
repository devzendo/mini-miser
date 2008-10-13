package uk.me.gumbley.minimiser.persistence.sql;

import java.sql.Statement;

/**
 * The SQLAccess interface provides low-level direct SQL access to the
 * database, ofr migration, and validating/parsing SQL statements.
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
        Count
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
     * Create a JDBC Statement.
     * @return the Statement
     */
    Statement createStatement();
}
