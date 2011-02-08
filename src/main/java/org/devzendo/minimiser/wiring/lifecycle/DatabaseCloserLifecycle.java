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

package org.devzendo.minimiser.wiring.lifecycle;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.devzendo.commonapp.lifecycle.Lifecycle;
import org.devzendo.minimiser.closer.Closer;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.openlist.OpenDatabaseList;
import org.devzendo.minimiser.prefs.MiniMiserPrefs;
import org.devzendo.minimiser.util.DatabasePairEncapsulator;

/**
 * A Lifecycle that records all open databases in prefs, then closes them all.
 * 
 * @author matt
 *
 */
public final class DatabaseCloserLifecycle implements Lifecycle {
    private static final Logger LOGGER = Logger
            .getLogger(DatabaseCloserLifecycle.class);
    private final OpenDatabaseList openDatabaseList;
    private final MiniMiserPrefs prefs;

    /**
     * We need to store the open database list so we know what to close on
     * shutdown.
     * @param openList the OpenDatabaseList
     * @param prefstore the prefs store
     */
    public DatabaseCloserLifecycle(final OpenDatabaseList openList, final MiniMiserPrefs prefstore) {
        this.openDatabaseList = openList;
        this.prefs = prefstore;
    }
    
    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        persistActiveDatabase();
        persistOpenDatabaseList();
        closeDatabases();
    }

    private void persistActiveDatabase() {
        final DatabaseDescriptor currentDatabase = openDatabaseList.getCurrentDatabase();
        if (currentDatabase == null) {
            LOGGER.info("There is no currently active database, so clearing record of it");
            prefs.clearLastActiveFile();
        } else {
            final String databaseName = currentDatabase.getDatabaseName();
            LOGGER.info("Recording ' " + databaseName + "' as the current database");
            prefs.setLastActiveFile(databaseName);
        }
    }

    private void persistOpenDatabaseList() {
        LOGGER.info("Saving list of open databases");
        final ArrayList<String> listAsStringPaths = new ArrayList<String>();
        final List<DatabaseDescriptor> openDatabases = openDatabaseList.getOpenDatabases();
        for (int i = 0; i < openDatabases.size(); i++) {
            final DatabaseDescriptor descriptor = openDatabases.get(i);
            listAsStringPaths.add(DatabasePairEncapsulator.escape(descriptor.getDatabaseName(), descriptor.getDatabasePath()));
        }
        prefs.setOpenFiles(listAsStringPaths.toArray(new String[0]));
        LOGGER.info("List of open databases saved");
    }

    private void closeDatabases() {
        LOGGER.info("Closing open databases");
        final List<DatabaseDescriptor> openDatabases = openDatabaseList.getOpenDatabases();
        for (final DatabaseDescriptor descriptor : openDatabases) {
            LOGGER.debug("Closing database " + descriptor.getDatabaseName());
            Closer.close(descriptor);
            LOGGER.debug("Removing database " + descriptor.getDatabaseName() + " from open database list");
            openDatabaseList.removeClosedDatabase(descriptor);
        }
        LOGGER.info("Open databases closed");
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        // do nothing
    }
}
