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

package org.devzendo.minimiser.opener;

import org.devzendo.minimiser.migrator.Migrator;
import org.devzendo.minimiser.persistence.DAOFactory;
import org.devzendo.minimiser.util.InstanceSet;
import org.springframework.dao.DataAccessException;


/**
 * A Migrator that always says the data is current.
 * 
 * @author matt
 *
 */
public final class AlwaysCurrentMigrator implements Migrator {
    /**
     * {@inheritDoc}
     */
    public MigrationVersion requiresMigration(
            final InstanceSet<DAOFactory> daoFactories) {
        return MigrationVersion.CURRENT;
    }

    /**
     * {@inheritDoc}
     */
    public void migrate(final InstanceSet<DAOFactory> daoFactories)
            throws DataAccessException {
    }
}
