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

import org.devzendo.commonapp.lifecycle.Lifecycle;
import org.devzendo.minimiser.openlist.DatabaseEventListenerManager;

/**
 * A Lifecycle that wires and unwires the DatabaseEventListenerManager on
 * startup and shutdown.
 * 
 * @author matt
 *
 */
public final class DatabaseEventListenerLifecycle implements Lifecycle {
    
    private final DatabaseEventListenerManager databaseEventListenerManager;

    /**
     * Create the DatabaseEventListenerLifecycle, given the
     * DatabaseEventListenerManager to wire/unwire.
     * 
     * @param manager the DatabaseEventListenerManager
     */
    public DatabaseEventListenerLifecycle(final DatabaseEventListenerManager manager) {
        this.databaseEventListenerManager = manager;
    }
    
    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        databaseEventListenerManager.unwire();
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        databaseEventListenerManager.wire();
    }
}
