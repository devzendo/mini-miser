/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
