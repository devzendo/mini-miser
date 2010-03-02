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

import org.apache.log4j.Logger;
import org.devzendo.minimiser.lifecycle.Lifecycle;
import org.devzendo.minimiser.opener.Opener;
import org.devzendo.minimiser.opener.OpenerAdapter;
import org.devzendo.minimiser.opener.OpenerAdapterFactory;
import org.devzendo.minimiser.openlist.OpenDatabaseList;
import org.devzendo.minimiser.prefs.Prefs;
import org.devzendo.minimiser.util.DatabasePair;
import org.devzendo.minimiser.util.DatabasePairEncapsulator;


/**
 * A Lifecycle that opens all databases saved in prefs, and switches to the
 * last active one.
 * 
 * @author matt
 *
 */
public final class DatabaseOpenerLifecycle implements Lifecycle {
    private static final Logger LOGGER = Logger
            .getLogger(DatabaseOpenerLifecycle.class);
    private final Opener opener;
    private final OpenerAdapterFactory openerAdapterFactory;
    private final Prefs prefs;
    private final OpenDatabaseList openDatabaseList;

    /**
     * We need to read the open database list so we know what to open on
     * startup.
     * @param openr the Opener
     * @param adapterFactory the OpenerAdaptorFactory
     * @param openList the OpenDatabaseList
     * @param prefstore the prefs store
     */
    public DatabaseOpenerLifecycle(final Opener openr,
            final OpenerAdapterFactory adapterFactory,
            final OpenDatabaseList openList,
            final Prefs prefstore) {
        this.opener = openr;
        this.openerAdapterFactory = adapterFactory;
        this.openDatabaseList = openList;
        this.prefs = prefstore;
    }
    
    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        LOGGER.info("Opening all databases that were open on last shutdown");
        final String[] encapsulatedPairs = prefs.getOpenFiles();
        for (final String encapsulatedPair : encapsulatedPairs) {
            final DatabasePair databasePair = DatabasePairEncapsulator.unescape(encapsulatedPair);
            final OpenerAdapter openerAdapter = openerAdapterFactory.createOpenerAdapter(databasePair.getName());
            opener.openDatabase(databasePair.getName(), databasePair.getPath(), openerAdapter);
        }
        LOGGER.info("Previously open databases opened");
        
        final String lastActiveDatabaseName = prefs.getLastActiveFile();
        if (lastActiveDatabaseName == null) {
            LOGGER.info("No last active database recorded, so cannot switch");
            openDatabaseList.checkForEmptiness();
        } else {
            LOGGER.info("Switching to last active database");
            openDatabaseList.switchDatabase(lastActiveDatabaseName);
            LOGGER.info("Switched to last active database");
        }
    }
}
