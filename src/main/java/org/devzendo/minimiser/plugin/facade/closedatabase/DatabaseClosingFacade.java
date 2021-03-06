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
package org.devzendo.minimiser.plugin.facade.closedatabase;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * Facade provided to let plugins close any database-related resources
 * before the actual database is closed.
 * 
 * @author matt
 *
 */
public interface DatabaseClosingFacade {
    /**
     * Perform any final tidy-up or session-closing actions before the
     * database is closed by the framework.
     * 
     * @param dataSource the DataSource, for low-level access to
     * the database
     * @param jdbcTemplate the Spring SimpleJdbcTemplate, for
     * easier access to the database atop JDBC
     * @throws DataAccessException on migration failure
     */
    void closeDatabase(
            DataSource dataSource,
            SimpleJdbcTemplate jdbcTemplate) throws DataAccessException;
}
