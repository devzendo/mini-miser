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

package org.devzendo.minimiser.startupqueue;

import org.devzendo.minimiser.springloader.SpringLoader;

/**
 * Toolkit for easily adding Runnables to the StartupQueue..
 * 
 * @author matt
 *
 */
public final class StartupQueueHelper {
    private static StartupQueue startupQueue;

    /**
     * 
     */
    private StartupQueueHelper() {
        // no instances
    }
    
    /**
     * Initialise the toolkit by obtaining the things it needs to add Runnables
     * to the Startup Queue.
     * 
     * @param springLoader the spring loader
     */
    public static void initialise(final SpringLoader springLoader) {
        startupQueue = springLoader.getBean("startupQueue", StartupQueue.class);
    }

    /**
     * @see StartupQueue.addRunnable(Runnable)
     * @param runnable the Runnable to add.
     */
    public static void addRunnable(final Runnable runnable) {
        startupQueue.addRunnable(runnable);
    }
}
