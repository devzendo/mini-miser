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

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.concurrency.ThreadUtils;
import org.devzendo.commoncode.logging.LoggingUnittestHelper;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.util.DelayedExecutor.Executable;
import org.devzendo.minimiser.util.DelayedExecutor.ExecutableReplaced;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;



/**
 * Tests the delayed executor.
 *
 * @author matt
 *
 */
public final class TestDelayedExecutor {
    private static final Logger LOGGER = Logger
            .getLogger(TestDelayedExecutor.class);
    private static final String TEST_KEY = "test";
    private DelayedExecutor delayedExecutor;

    /**
     * 
     */
    @BeforeClass
    public static void setupLogging() {
        LoggingUnittestHelper.setupLogging();
    }

    /**
     * All tests need one...
     */
    @Before
    public void getDelayedExecutor() {
        delayedExecutor = new DelayedExecutor();
    }

    /**
     * Equality is based on key alone.
     */
    @Test
    public void testExecutableEqual() {
        final Executable early = new Executable(TEST_KEY, 50, new Runnable() {
            public void run() {
            }
        });
        final Executable late = new Executable(TEST_KEY, 500, new Runnable() {
            public void run() {
            }
        });
        Assert.assertTrue(early.equals(late));
    }

    /**
     * Equality is based on key alone
     */
    @Test
    public void testExecutableInEqual() {
        final Executable early = new Executable("Ee chim u jim sho", 50, new Runnable() {
            public void run() {
            }
        });
        final Executable late = new Executable("kwamse imboku", 500, new Runnable() {
            public void run() {
            }
        });
        Assert.assertFalse(early.equals(late));
    }

    /**
     * No delay means run ASAP.
     */
    @Test(timeout = 5000)
    public void testImmediateExecution() {
        final AtomicBoolean run = new AtomicBoolean(false);
        final Runnable r = new Runnable() {
            public void run() {
                run.set(true);
            }
        };
        delayedExecutor.submit(TEST_KEY, 0L, r);
        ThreadUtils.waitNoInterruption(250);
        Assert.assertTrue(run.get());
    }

    /**
     * Are we actually running on another thread?
     */
    @Test(timeout = 5000)
    public void testExecutingOnDelayedExecutorThread() {
        final String[] name = new String[] {"change me"};
        final Runnable r = new Runnable() {
            public void run() {
                name[0] = Thread.currentThread().getName();
            }
        };
        delayedExecutor.submit(TEST_KEY, 0L, r);
        ThreadUtils.waitNoInterruption(250);
        Assert.assertEquals("Delayed Executor", name[0]);
    }

    /**
     * Are we actually running on the AWT/Swing Event thread?
     */
    @Test(timeout = 5000)
    public void testExecutingOnAWTSwingEventThread() {
        SwingUnittestHelper.ensureEventThreadStarted();
        try {
            final AtomicBoolean swing = new AtomicBoolean(false);
            final Runnable r = new Runnable() {
                public void run() {
                    swing.set(SwingUtilities.isEventDispatchThread());
                }
            };
            delayedExecutor.submitGui(TEST_KEY, 0L, r);
            ThreadUtils.waitNoInterruption(500);
            Assert.assertTrue(swing.get());
        } finally {
            SwingUnittestHelper.ensureEventThreadFlushed();
        }
    }

    /**
     * Do we run all unique jobs?
     */
    @Test(timeout = 5000)
    public void testMultipleSequentialImmediateUniqueKeyedSubmissionsAreAllExecutedImmediately() {
        // it should not block after removing a Runnable
        final AtomicBoolean run0 = new AtomicBoolean(false);
        final AtomicBoolean run1 = new AtomicBoolean(false);
        final AtomicBoolean run2 = new AtomicBoolean(false);
        final Runnable r0 = new Runnable() {
            public void run() {
                run0.set(true);
            }
        };
        final Runnable r1 = new Runnable() {
            public void run() {
                run1.set(true);
            }
        };
        final Runnable r2 = new Runnable() {
            public void run() {
                run2.set(true);
            }
        };
        final long start = System.currentTimeMillis();
        delayedExecutor.submit("test1", 0L, r0);
        delayedExecutor.submit("test2", 0L, r1);
        delayedExecutor.submit("test3", 0L, r2);
        ThreadUtils.waitNoInterruption(1000);
        Assert.assertTrue(run0.get());
        Assert.assertTrue(run1.get());
        Assert.assertTrue(run2.get());
        final long stop = System.currentTimeMillis();
        Assert.assertTrue(stop - start < 2000);
    }

    private static final long ENTRY_DELAY = 50L;
    private static final int ENTRIES = 100;

    /**
     * Do we run tasks sequentially?
     */
    @Test(timeout = 10000)
    public void testManyUniquelyKeyedSubmissionsWithSlightDelayAreAllExecutedInOrder() {
        final AtomicInteger count = new AtomicInteger(0);
        final Runnable[] runnables = new Runnable[ENTRIES];
        final long[] lastRunTime = new long[] {0L};
        final AtomicBoolean sequential = new AtomicBoolean(true); // optimist
        for (int i = 0; i < ENTRIES; i++) {
            runnables[i] = new Runnable() {
                public void run() {
                    count.incrementAndGet();
                    final long now = System.currentTimeMillis();
                    if (now < lastRunTime[0]) {
                        sequential.set(false);
                    }
                    lastRunTime[0] = now;
                }
            };
        }
        for (int i = 0; i < ENTRIES; i++) {
            delayedExecutor.submit("test" + i, ENTRY_DELAY, runnables[i]);
        }
        ThreadUtils.waitNoInterruption((50 * ENTRIES) + 500);
        Assert.assertTrue(count.get() == ENTRIES);
        Assert.assertTrue(sequential.get());
    }

    /**
     * Do we ignore early runs of multiple jobs fired rapidly?
     */
    @Test(timeout = 10000)
    public void testManyIdenticalKeysSubmittedInDelayPeriodOnlyExecuteLast() {
        final boolean[] replacementShouldHaveOccurred = new boolean[] {false};
        delayedExecutor.addExecutableReplacementObserver(new Observer<ExecutableReplaced>() {
            public void eventOccurred(final ExecutableReplaced observableEvent) {
                replacementShouldHaveOccurred[0] = true;
            }
        });
        final AtomicInteger count = new AtomicInteger(0);
        final Runnable[] runnables = new Runnable[ENTRIES];
        final long[] lastRunTime = new long[] {0L};
        for (int i = 0; i < ENTRIES; i++) {
            runnables[i] = new Runnable() {
                public void run() {
                    count.incrementAndGet();
                    lastRunTime[0] = System.currentTimeMillis();
                }
            };
        }
        for (int i = 0; i < ENTRIES; i++) {
            delayedExecutor.submit(TEST_KEY, 1000L, runnables[i]);
        }
        ThreadUtils.waitNoInterruption(1500L);
        Assert.assertTrue(count.get() == 1);
        Assert.assertTrue(lastRunTime[0] != 0L);
        Assert.assertTrue(replacementShouldHaveOccurred[0]);
    }

    /**
     * If we submit the same job again in its delay period, does it get
     * replaced?
     */
    @Test
    public void testReplacementOfUniqueKeys() {
        final boolean[] replacementShouldHaveOccurred = new boolean[] {false};
        final Executable[] replacementExecutable = new Executable[] {null};
        delayedExecutor.addExecutableReplacementObserver(new Observer<ExecutableReplaced>() {
            public void eventOccurred(final ExecutableReplaced observableEvent) {
                replacementShouldHaveOccurred[0] = true;
                replacementExecutable[0] = observableEvent.getExecutable();
            }
        });
        final Runnable run1 = new Runnable() {
            public void run() {
                // nop
            }
        };
        final Runnable run2 = new Runnable() {
            public void run() {
                // nop
            }
        };
        delayedExecutor.submit(TEST_KEY, 500L, run1);
        delayedExecutor.submit(TEST_KEY, 500L, run2);
        ThreadUtils.waitNoInterruption(1000L);
        Assert.assertTrue(replacementShouldHaveOccurred[0]);
        Assert.assertNotNull(replacementExecutable[0]);
        Assert.assertSame(run1, replacementExecutable[0].getRunnable());
    }

    private static final int SMALL_ENTRIES = 5;

    /**
     * Do we run everything if we give enough time between tasks?
     */
    @Test(timeout = 10000)
    public void testManyIdenticalKeyedSubmissionsGivenTimeAfterDelayAllExecuteInOrder() {
        final AtomicBoolean replaced = new AtomicBoolean(false);
        delayedExecutor.addExecutableReplacementObserver(new Observer<ExecutableReplaced>() {
            public void eventOccurred(final ExecutableReplaced observableEvent) {
                final String fail = "The Executable " + observableEvent.getExecutable() + " was erroneously replaced";
                LOGGER.error(fail);
                replaced.set(true);
            }
        });
        final AtomicInteger count = new AtomicInteger(0);
        final Runnable[] runnables = new Runnable[SMALL_ENTRIES];
        final long[] lastRunTime = new long[] {0L};
        final AtomicBoolean sequential = new AtomicBoolean(true); // optimist
        for (int i = 0; i < SMALL_ENTRIES; i++) {
            runnables[i] = new Runnable() {
                public void run() {
                    count.incrementAndGet();
                    final long now = System.currentTimeMillis();
                    if (now < lastRunTime[0]) {
                        sequential.set(false);
                    }
                    lastRunTime[0] = now;
                }
            };
        }
        for (int i = 0; i < SMALL_ENTRIES; i++) {
            delayedExecutor.submit(TEST_KEY, ENTRY_DELAY, runnables[i]);
            ThreadUtils.waitNoInterruption(ENTRY_DELAY * 4);
        }
        ThreadUtils.waitNoInterruption(2000);
        Assert.assertTrue(count.get() == SMALL_ENTRIES);
        Assert.assertFalse(replaced.get());
        Assert.assertTrue(sequential.get());
    }
}
