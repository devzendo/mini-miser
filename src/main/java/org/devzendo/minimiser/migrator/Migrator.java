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

package org.devzendo.minimiser.migrator;

import org.devzendo.minimiser.persistence.DAOFactory;
import org.devzendo.minimiser.util.InstanceSet;
import org.springframework.dao.DataAccessException;


/**
 * Performs migrations - upgrades from an old schema to a new one on open
 * databases, and updates the versions of all plugins via the VersionsDao. All
 * done within a transaction, which is rolled back upon failure.
 * 
 * @author matt
 * 
 */
public interface Migrator {
    public enum MigrationVersion {
        /**
         * The database being considered is old w.r.t. the plugins
         * current version and should be migrated.
         */
        OLD,
        
        /**
         * The database being considered is the same version as
         * the plugins and does not need migrating. 
         */
        CURRENT,
        
        /**
         * The database being considered was created by newer
         * versions of the plugins, and so cannot be migrated or
         * opened. 
         */
        FUTURE
    };

    /**
     * Does the opened database require migration?
     * 
     * @param daoFactories
     *        the DAO factories
     * @return true iff it requires migration
     */
    MigrationVersion requiresMigration(InstanceSet<DAOFactory> daoFactories);

    /**
     * Migrate the database to the latest version and update the
     * database schema version numbers. If the exception
     * is thrown, the whole migration is rolled back.
     * 
     * @param daoFactories the DAO factories
     * @throws DataAccessException on migration failure
     */
    void migrate(InstanceSet<DAOFactory> daoFactories) throws DataAccessException;
}
