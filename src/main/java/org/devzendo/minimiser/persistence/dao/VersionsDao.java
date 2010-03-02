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

package org.devzendo.minimiser.persistence.dao;

import org.devzendo.minimiser.persistence.domain.Version;
import org.springframework.dao.DataAccessException;


/**
 * Describes all versionable entities in the application, and their version
 * number strings.
 * 
 * For example, the database schema has a version, as does the application.
 * 
 * This bean/table is present in all versions of the schema, and is used to
 * determine which version of the application created the opened database, and
 * which version of the schema is present, for migration.
 * 
 * @author matt
 *
 */
public interface VersionsDao {
    /**
     * Find a Version for a given entity
     * @param pluginName the name of a plugin that should be
     * checked for existence
     * @param entity the name of the entity whose version should be found 
     * @return a Version for a versionable entity.
     * @throws DataAccessException on database failures
     */
    Version findVersion(String pluginName, String entity) throws DataAccessException;

    /**
     * Store a specific Version
     *  
     * @param version the version to store
     * @throws DataAccessException on database failures
     */
    void persistVersion(Version version) throws DataAccessException;

    /**
     * Check for existence of a specific verison
     * @param pluginName the name of a plugin that should be
     * checked for existence
     * @param entity the name of an entity that should be checked
     * for existence
     * @return true if it exists, false if not
     * @throws DataAccessException on database failures
     */
    boolean exists(String pluginName, String entity) throws DataAccessException;
}