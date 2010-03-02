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

package org.devzendo.minimiser.persistence;

import org.devzendo.minimiser.persistence.dao.SequenceDao;
import org.devzendo.minimiser.persistence.dao.VersionsDao;
import org.devzendo.minimiser.persistence.sql.SQLAccess;

/**
 * A factory for the database schema's DAO objects.
 *  
 * @author matt
 *
 */
public interface MiniMiserDAOFactory extends DAOFactory {
    /**
     * VersionDAO added in V1 of the schema.
     * @return the DAO for accessing the Versions table
     */
    VersionsDao getVersionDao();

    /**
     * SequenceDao added in V1 of the schema. 
     * @return the DAO for accessing the Sequence sequence.
     */
    SequenceDao getSequenceDao();
    
    /**
     * Obtain low-lvel access to the database. Can throw an SQLAccessException
     * (a RuntimeException) on failure to get low-level access.
     * @return an interface for performing low-level direct SQL access to the
     * database, e.g. for migration and validating/parsing SQL statements.
     */
    SQLAccess getSQLAccess();
    
    /**
     * Close the database.
     */
    void close();

    /**
     * @return true iff the database is closed
     */
    boolean isClosed();
}
