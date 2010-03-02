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

import org.devzendo.minimiser.gui.StatusBarMessageQueueAdapter;
import org.devzendo.minimiser.lifecycle.Lifecycle;

/**
 * A Lifecycle that receives the adapter between Status Bar and Message Queue,
 * and initialises it.
 * 
 * @author matt
 *
 */
public final class StatusBarAdapterLifecycle implements Lifecycle {
    
   
    private final StatusBarMessageQueueAdapter statusBarMessageQueueAdapter;

    /**
     * Create the StatusBarAdapterLifecycle, given the
     * StatusBarMessageQueueAdapter.
     * 
     * @param adapter the StatusBarMessageQueueAdapter
     */
    public StatusBarAdapterLifecycle(final StatusBarMessageQueueAdapter adapter) {
        this.statusBarMessageQueueAdapter = adapter;
    }
    
    /**
     * {@inheritDoc}
     */
    public void shutdown() {
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        statusBarMessageQueueAdapter.wireAdapter();
    }
}
