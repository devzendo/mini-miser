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
import org.devzendo.minimiser.updatechecker.UpdateChecker;
import org.devzendo.minimiser.updatechecker.UpdateProgressAdapter;
import org.devzendo.minimiser.updatechecker.UpdateProgressAdapterFactory;
import org.devzendo.minimiser.util.Sleeper;


/**
 * A Lifecycle that waits an hour, then does an update check,
 * and repeats indefinitely.
 * 
 * @author matt
 *
 */
public class PeriodicUpdateCheckerLifecycle implements Lifecycle {
    private static final Logger LOGGER = Logger
            .getLogger(PeriodicUpdateCheckerLifecycle.class);
    private Thread mSleepThread;
    private volatile boolean mAlive = false;
    private final UpdateChecker mUpdateChecker;
    private final Sleeper mSleeper;
    private final UpdateProgressAdapterFactory mProgressAdapterFactory;
    
    /**
     * Construct a PeriodicUpdateCheckerLifecycle
     * @param updateChecker the update checker that does the work
     * @param sleeper the source of time
     * @param progressAdapterFactory the update progress adapter
     * factory that builds progress adapters with which to notify
     * the user that updates are happening.
     */
    public PeriodicUpdateCheckerLifecycle(
            final UpdateChecker updateChecker,
            final Sleeper sleeper,
            final UpdateProgressAdapterFactory progressAdapterFactory) {
                mUpdateChecker = updateChecker;
                mSleeper = sleeper;
                mProgressAdapterFactory = progressAdapterFactory;
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        if (mSleepThread != null) {
            mAlive = false;
            mSleepThread.interrupt();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        mAlive = true;
        mSleepThread = new Thread(new Runnable() {
            public void run() {
                LOGGER.info("Starting background update availability checker");
                while (mAlive) {
                    LOGGER.info("Next check in one hour");
                    mSleeper.sleep(1000 * 60 * 60); // 1 hour
                    if (mAlive) {
                        LOGGER.info("Starting periodic update availability check");
                        final UpdateProgressAdapter adapter = mProgressAdapterFactory.createBackgroundUpdateProgressAdapter();
                        mUpdateChecker.triggerUpdateCheck(adapter);
                    }
                }
            }
        });
        mSleepThread.setPriority(Thread.MIN_PRIORITY + 1);
        mSleepThread.setName("Background update checker");
        mSleepThread.setDaemon(true);
        mSleepThread.start();
    }
}
