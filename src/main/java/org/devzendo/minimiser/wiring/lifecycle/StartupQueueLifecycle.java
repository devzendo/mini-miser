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
import org.devzendo.minimiser.startupqueue.StartupQueue;

/**
 * A Lifecycle that executes the StartupQueue on startup.
 * This is configured to run LAST in the lifecycle list, so that it'll run
 * after any databases are open.
 * 
 * @author matt
 *
 */
public final class StartupQueueLifecycle implements Lifecycle {
    
    private final StartupQueue startupQueue;

    /**
     * Create the StartupQueueLifecycle, given the
     * StartupQueue to execute startup tasks.
     * 
     * @param sq the StartupQueue
     */
    public StartupQueueLifecycle(final StartupQueue sq) {
        this.startupQueue = sq;
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
        startupQueue.executeQueue();
    }
}
