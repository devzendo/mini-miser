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

package org.devzendo.minimiser.closer;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.gui.dialog.problem.ProblemDialogHelper;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.persistence.MiniMiserDAOFactory;
import org.springframework.dao.DataAccessException;


/**
 * Toolkit for closing databases
 * 
 * @author matt
 *
 */
public final class Closer {
    private static final Logger LOGGER = Logger.getLogger(Closer.class);
    
    private Closer() {
        // no instances
    }

    /**
     * Close the database stored in a DatabaseDescriptor, report problems
     * if a problem occurs.
     * @param descriptor the database descriptor
     * @return true iff closed OK
     */
    public static boolean close(final DatabaseDescriptor descriptor) {
        final String databaseName = descriptor.getDatabaseName();
        LOGGER.info("Closing database '" + databaseName + "'");
        try {
            final MiniMiserDAOFactory database = descriptor.getDAOFactory(MiniMiserDAOFactory.class);
            if (database == null) {
                LOGGER.warn("No DAO Factory stored in database descriptor for database name '" + databaseName + "'");
                return false;
            }
            database.close();
            LOGGER.info("Database '" + databaseName + "' closed");
            return true;
        } catch (final DataAccessException dae) {
            LOGGER.warn("Could not close database '" + databaseName + "': " + dae.getMessage(), dae);
            ProblemDialogHelper.reportProblem("while closing the '" + databaseName + "' database", dae);
            return false;
        }
    }
}
