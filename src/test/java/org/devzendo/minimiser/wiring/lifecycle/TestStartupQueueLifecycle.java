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

import java.util.concurrent.atomic.AtomicBoolean;

import org.devzendo.commonapp.lifecycle.LifecycleManager;
import org.devzendo.commonapp.spring.springloader.ApplicationContext;
import org.devzendo.commonapp.spring.springloader.SpringLoaderUnittestCase;
import org.devzendo.minimiser.startupqueue.StartupQueue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the StartupQueue that's controlled as a Lifecycle.
 *
 * @author matt
 *
 */
@ApplicationContext("org/devzendo/minimiser/wiring/lifecycle/StartupQueueLifecycleTestCase.xml")
public final class TestStartupQueueLifecycle extends SpringLoaderUnittestCase {

    private LifecycleManager lifecycleManager;
    private StartupQueue startupQueue;

    /**
     *
     */
    @Before
    public void getPrerequisites() {
        lifecycleManager = getSpringLoader().getBean("lifecycleManager", LifecycleManager.class);
        Assert.assertNotNull(lifecycleManager);

        startupQueue = getSpringLoader().getBean("startupQueue", StartupQueue.class);
        Assert.assertNotNull(startupQueue);
    }

    /**
     *
     */
    @Test(timeout = 5000)
    public void taskExecutedOnStartup() {
        final AtomicBoolean executed = new AtomicBoolean(false);
        final Runnable task = new Runnable() {
            public void run() {
                executed.set(true);
            }
        };
        startupQueue.addRunnable(task);

        Assert.assertFalse(executed.get());

        lifecycleManager.startup();

        startupQueue.join();

        Assert.assertTrue(executed.get());
    }
}
