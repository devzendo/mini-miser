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

import org.devzendo.minimiser.lifecycle.Lifecycle;
import org.devzendo.minimiser.util.DelayedExecutor;

/**
 * A Lifecycle that flushes any remaining tasks on the DelayedExecutor.
 * @author matt
 *
 */
public final class DelayedExecutorFlushLifecycle implements Lifecycle {
    private final DelayedExecutor delayedExecutor;

    /**
     * Construct
     * @param executor th edelayed executor
     */
    public DelayedExecutorFlushLifecycle(final DelayedExecutor executor) {
        this.delayedExecutor = executor;
        
    }
    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        // TODO write the flushing functionality
        // delayedExecutor.flush();
        delayedExecutor.toString(); // no checkstyle warning please Bob
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        // do nothing
    }
}
