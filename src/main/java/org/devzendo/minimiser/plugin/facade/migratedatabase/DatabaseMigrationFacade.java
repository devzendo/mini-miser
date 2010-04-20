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

package org.devzendo.minimiser.plugin.facade.migratedatabase;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * Facade that allows plugins to upgrade their databases to the
 * latest schema upon opening.
 * 
 * @author matt
 *
 */
public interface DatabaseMigrationFacade {
    /**
     * The database schema is at a version older than that given
     * by the plugin, so migrate it to the latest version. The
     * framework will record the new version in the Versions
     * table, after migration. If the exception is thrown, the
     * entire migration will be rolled back, and the open
     * terminated.
     * 
     * @param dataSource the DataSource, for low-level access to
     * the database
     * @param jdbcTemplate the Spring SimpleJdbcTemplate, for
     * easier access to the database atop JDBC
     * @param currentSchemaVersion this plugin's current database
     * schema version
     * @throws DataAccessException on migration failure
     */
    void migrateSchema(
            DataSource dataSource,
            SimpleJdbcTemplate jdbcTemplate,
            String currentSchemaVersion) throws DataAccessException;
}
