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

package org.devzendo.minimiser.util;

import java.awt.EventQueue;

import org.devzendo.commoncode.concurrency.ThreadUtils;


/**
 * Helper code for ensuring the Swing/AWT event queue is active
 * before a test, and flushed afterwards.
 * 
 * @author matt
 *
 */
public final class SwingUnittestHelper {
    private SwingUnittestHelper() {
    }

    /**
     * Queue an empty Runnable to ensure the event queue is running. 
     */
    public static void ensureEventThreadStarted() {
        waitForQueue();
    }

    private static void waitForQueue() {
        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                }
            });
        } catch (final Exception e) {
        }
    }

    /**
     * Wait for a second then queue a final empty Runnable to wait for the
     * final event to be dispatched.
     */
    public static void ensureEventThreadFlushed() {
        ThreadUtils.waitNoInterruption(1000L);
        waitForQueue();
    }
}
