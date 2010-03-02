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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

/**
 * The startup queue accepts an infinite list of Things To Do
 * @author matt
 *
 */
public final class StartupQueue {
    private static final Logger LOGGER = Logger.getLogger(StartupQueue.class);
    private List<Runnable> queue;
    private CountDownLatch allTasksRun;

    /**
     * Construct the startup queue.
     */
    public StartupQueue() {
        queue = new ArrayList<Runnable>();
        allTasksRun = new CountDownLatch(1);
    }
    
    /**
     * Add a Runnable to the queue, to be executed in turn upon calling
     * executeQueue.
     * @param runnable the Runnable to add
     */
    public void addRunnable(final Runnable runnable) {
        if (runnable == null) {
            final String warning = "Can't add null to the StartupQueue";
            LOGGER.warn(warning);
            throw new IllegalArgumentException(warning);
        }
        synchronized (queue) {
            LOGGER.debug("Adding task to startup queue: " + runnable.getClass().getName());
            queue.add(runnable);
        }
    }

    /**
     * Execute all Runnables on the queue in a thread created for this purpose.
     * All Runnables run on the same thread, in the order in which they were
     * added to the startup queue.
     */
    public void executeQueue() {
        synchronized (queue) {
            if (queue.size() == 0) {
                LOGGER.info("No tasks on startup queue");
                allTasksRun.countDown();
                return;
            }
            final Thread queueExecutionThread = new Thread(new Runnable() {
                public void run() {
                    synchronized (queue) {
                        LOGGER.info("Running " + queue.size() + " task(s) from startup queue");
                        for (Runnable runnable : queue) {
                            LOGGER.debug("Executing startup queue task: " + runnable.getClass().getName());
                            runnable.run();
                        }
                        LOGGER.info("Finished executing tasks form the startup queue");
                        allTasksRun.countDown();
                    }
                }
            });
            queueExecutionThread.setName("Startup Queue Executor");
            queueExecutionThread.start();
        }
    }

    /**
     * For use by tests really, wait until all tasks have been completed.
     */
    public void join() {
        try {
            LOGGER.debug("Waiting for all startup queue tasks to complete");
            allTasksRun.await();
            LOGGER.debug("All startup queue tasks have finished");
        } catch (final InterruptedException e) {
            LOGGER.warn("Interrupted whilst waiting for tasks to complete: " + e.getMessage(), e);
        }
    }
}
