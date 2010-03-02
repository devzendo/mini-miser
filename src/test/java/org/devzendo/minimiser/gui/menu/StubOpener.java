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

package org.devzendo.minimiser.gui.menu;

import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.commoncode.patterns.observer.ObserverList;
import org.devzendo.minimiser.opener.DatabaseOpenEvent;
import org.devzendo.minimiser.opener.Opener;
import org.devzendo.minimiser.opener.OpenerAdapter;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.persistence.DAOFactory;
import org.devzendo.minimiser.util.InstanceSet;


/**
 * A stub opener that just issues a DatabaseOpenEvent immediately when
 * openDatabase is called.
 * 
 * @author matt
 *
 */
public final class StubOpener implements Opener {
    private final ObserverList<DatabaseOpenEvent> observerList;

    /**
     * Construct the StubOpener
     */
    public StubOpener() {
        observerList = new ObserverList<DatabaseOpenEvent>();
    }

    /**
     * {@inheritDoc}
     */
    public void addDatabaseOpenObserver(final Observer<DatabaseOpenEvent> observer) {
        observerList.addObserver(observer);
    }

    /**
     * {@inheritDoc}
     */
    public InstanceSet<DAOFactory> openDatabase(
            final String dbName,
            final String pathToDatabase,
            final OpenerAdapter openerAdapter) {
        observerList.eventOccurred(new DatabaseOpenEvent(new DatabaseDescriptor(dbName, pathToDatabase)));
        return null;
    }
}
