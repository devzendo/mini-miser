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

package org.devzendo.minimiser.recentlist;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.prefs.MiniMiserPrefs;
import org.devzendo.minimiser.util.DatabasePair;
import org.devzendo.minimiser.util.DatabasePairEncapsulator;

/**
 * A RecentFilesList implementation that persists via prefs.
 * 
 * @author matt
 *
 */
public final class DefaultRecentFilesListImpl extends AbstractRecentFilesListImpl {
    private static final Logger LOGGER = Logger
            .getLogger(DefaultRecentFilesListImpl.class);
    private final MiniMiserPrefs preferences;

    /**
     * Construct a recent files list
     * @param prefs the prefs in which to store the recent files list
     */
    public DefaultRecentFilesListImpl(final MiniMiserPrefs prefs) {
        super();
        preferences = prefs;
        setDatabaseList(load());
    }

    /**
     * {@inheritDoc}
     */
    protected void save() {
        LOGGER.info("saving recent files...");
        final ArrayList<String> listAsStringPaths = new ArrayList<String>();
        for (DatabaseDescriptor databaseDescriptor : getDatabaseList()) {
            listAsStringPaths.add(DatabasePairEncapsulator.escape(databaseDescriptor.getDatabaseName(),
                databaseDescriptor.getDatabasePath()));
        }
        for (String string : listAsStringPaths) {
            LOGGER.info("  " + string);
        }
        preferences.setRecentFiles(listAsStringPaths.toArray(new String[0]));
    }
    
    /**
     * {@inheritDoc}
     */
    protected List<DatabaseDescriptor> load() {
        LOGGER.info("loading recent files...");
        final String[] recentEscapedNamesAndPaths = preferences.getRecentFiles();
        final List<DatabaseDescriptor> descriptors = new ArrayList <DatabaseDescriptor>();
        for (String nameAndPath : recentEscapedNamesAndPaths) {
            try {
                final DatabasePair pair = DatabasePairEncapsulator.unescape(nameAndPath);
                LOGGER.info("stored " + nameAndPath + " name " + pair.getName() + " path " + pair.getPath());
                descriptors.add(new DatabaseDescriptor(pair.getName(), pair.getPath()));
            } catch (final IllegalArgumentException iae) {
                // TODO: should this be a warning in a dialog?
                LOGGER.warn("Could not reload entry from recent list '" + nameAndPath + "'");
            }
        }
        return descriptors;
    }
}
