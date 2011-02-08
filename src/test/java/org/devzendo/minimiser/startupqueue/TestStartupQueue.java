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

import java.util.List;

import org.devzendo.commonapp.util.OrderMonitor;
import org.devzendo.commoncode.logging.LoggingUnittestHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Tests the startup queue.
 * 
 * @author matt
 *
 */
public final class TestStartupQueue {
    private StartupQueue startupQueue;
    private OrderMonitor orderMonitor;

    /**
     * 
     */
    @BeforeClass
    public static void setupLogging() {
        LoggingUnittestHelper.setupLogging();
    }

    /**
     * 
     */
    @Before
    public void getPrerequisites() {
        startupQueue = new StartupQueue();
    }

    /**
     * 
     */
    @Test
    public void addedTaskGetsExecutedOnExecuteQueue() {
        orderMonitor = new OrderMonitor();
        
        final Runnable one = new Runnable() {
            public void run() {
                orderMonitor.add("one");
            }
        };
        startupQueue.addRunnable(one);
        final Runnable two = new Runnable() {
            public void run() {
                orderMonitor.add("two");
            }
        };
        startupQueue.addRunnable(two);
        
        startupQueue.executeQueue();
        
        startupQueue.join();
        
        final List<String> ordering = orderMonitor.getOrdering();
        Assert.assertEquals(2, ordering.size());
        Assert.assertEquals("one", ordering.get(0));
        Assert.assertEquals("two", ordering.get(1));
    }
    
    /**
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public void cantAddNull() {
        startupQueue.addRunnable(null);
    }
    
    /**
     * 
     */
    @Test
    public void addedTaskRunsOnOwnQueue() {
        final Thread testRunnerThread = Thread.currentThread();
        final Thread[] runningThread = new Thread[] {null};
        final Runnable one = new Runnable() {
            public void run() {
                runningThread[0] = Thread.currentThread();
            }
        };
        
        startupQueue.addRunnable(one);
        
        startupQueue.executeQueue();

        startupQueue.join();

        Assert.assertNotNull(runningThread[0]);
        Assert.assertNotSame(testRunnerThread, runningThread[0]);
    }
}
