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

package org.devzendo.minimiser.plugin.facade.opendatabase;

import javax.sql.DataSource;

import org.devzendo.minimiser.persistence.DAOFactory;
import org.devzendo.minimiser.util.InstancePair;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;


/**
 * Facade provided to allow plugins to create DAOFactory
 * instances in order to provide access to their custom data.
 * 
 * @author matt
 *
 */
public interface DatabaseOpeningFacade {

    /**
     * Create a DAOFactory that provides DAOs to access the given
     * database.
     * @param dataSource the data source for other connection to
     * the database
     * @param jdbcTemplate the Spring JDBC Template access to the
     * database
     * 
     * @return your plugin's DAOFactory sub-interface by which the
     * instance will later be retrieved by your plugin, and an
     * instance of this type, encapsulated in an InstancePair
     * 
     */
    InstancePair<DAOFactory> createDAOFactory(
            DataSource dataSource,
            SimpleJdbcTemplate jdbcTemplate);
}
