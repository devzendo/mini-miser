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

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.concurrency.ThreadUtils;
import org.devzendo.commoncode.patterns.observer.ObservableEvent;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.commoncode.patterns.observer.ObserverList;


/**
 * Allows Runnables to be enqueued and run after some specified delay.
 * Runnables are submitted with a key, and if many are submitted with the
 * same key, only the last one submitted is executed (at the end of the delay).
 * 
 * This is useful to prevent firestorms of execution - e.g. window movements
 * causing the current geometry to be stored in prefs.
 * 
 * @author matt
 *
 */
public final class DelayedExecutor {
    private static final Logger LOGGER = Logger
            .getLogger(DelayedExecutor.class);
    private static final long DEFAULT_EMPTY_TREE_WAIT_TIME = 1000L;
    private final Object lock;
    private final Map<String, Executable> existenceLookup;
    private final SortedMap<Long, Executable> triggerSortedMap;
    private final Thread executorThread;
    private final ObserverList<ExecutableReplaced> replacementObservers;
    
    /**
     * Construct the DelayedExecutor and start the execution thread.
     */
    public DelayedExecutor() {
        lock = new Object();
        synchronized (lock) {
            existenceLookup = new HashMap<String, Executable>();
            triggerSortedMap = new TreeMap<Long, Executable>();
            replacementObservers = new ObserverList<ExecutableReplaced>();
            executorThread = new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        investigateFirstExecutable();
                    }
                }
            });
            executorThread.setDaemon(true);
            executorThread.setName("Delayed Executor");
        }
    }

    /**
     * Signals that an Executable has been replaced since it has not been
     * executed when an submission was made with an identically-named key.
     * @author matt
     *
     */
    static class ExecutableReplaced implements ObservableEvent {
        private Executable exec;

        /**
         * @param executable the executable that has been replaced
         */
        public ExecutableReplaced(final Executable executable) {
            exec = executable;
        }

        /**
         * @return the executable
         */
        public Executable getExecutable() {
            return exec;
        }
    }

    /**
     * Add an observre of replacements, for unit testing.
     * @param obs the observer
     */
    void addExecutableReplacementObserver(final Observer<ExecutableReplaced> obs) {
        replacementObservers.addObserver(obs);
    }

    /**
     * Encapsulates a Runnable, its key and trigger time.
     * @author matt
     *
     */
    static class Executable {
        private final String ekey;
        private final long etriggerTime;
        private final Runnable erunnable;

        /**
         * @param key the key
         * @param triggerTime when this Runnable will run
         * @param runnable the Runnable
         */
        public Executable(final String key, final long triggerTime, final Runnable runnable) {
            this.ekey = key;
            this.etriggerTime = triggerTime;
            this.erunnable = runnable;
        }
        
        /**
         * {@inheritDoc}
         */
        public String toString() {
            return String.format("[%s] trigger %d", ekey, etriggerTime);
        }

        /**
         * @return the key
         */
        public String getKey() {
            return ekey;
        }

        /**
         * @return the runnable
         */
        public Runnable getRunnable() {
            return erunnable;
        }
        
        /**
         * @return the triggerTime
         */
        public long getTriggerTime() {
            return etriggerTime;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((ekey == null) ? 0 : ekey.hashCode());
            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Executable other = (Executable) obj;
            if (ekey == null) {
                if (other.ekey != null) {
                    return false;
                }
            } else if (!ekey.equals(other.ekey)) {
                return false;
            }
            return true;
        }
    }
    
    private void investigateFirstExecutable() {
        Executable executable = null;
        long waitTime = DEFAULT_EMPTY_TREE_WAIT_TIME; 
        synchronized (lock) {
            // stay in sync block for as short as possible
            if (!triggerSortedMap.isEmpty()) {
                final Long firstTriggerTime = triggerSortedMap.firstKey();
                executable = triggerSortedMap.get(firstTriggerTime);
                final long now = System.currentTimeMillis();
                final long triggerTime = executable.getTriggerTime();
                if (triggerTime > now) {
                    waitTime = triggerTime - now; // wait for this first Executable to trigger
                    executable = null;            // since its time is not yet due
                } else {
                    final boolean contained = triggerSortedMap.containsKey(firstTriggerTime);
                    final boolean removed = triggerSortedMap.remove(firstTriggerTime) != null;  // wake up! time to die! you will be executed
                    if (!removed || !contained) {
                        LOGGER.warn("!! triggerSortedMap: contained is " + contained + " removed was " + removed);
                    }
                    final boolean containedInExistenceLookup = existenceLookup.containsKey(executable.getKey());
                    final boolean removedFromExistenceLookup = existenceLookup.remove(executable.getKey()) != null;
                    if (!removedFromExistenceLookup || !containedInExistenceLookup) {
                        LOGGER.warn("!! existenceLookup: contained is " + containedInExistenceLookup + " removed was " + removedFromExistenceLookup);
                    }
                    waitTime = 0L;                // don't sleep, go check the tree again
                }
            }
        } // lock
        if (executable != null) {
            try {
                executable.getRunnable().run();
            } catch (final Throwable t) {
                LOGGER.warn("Delayed execution caught a " + t.getClass().getSimpleName() + ": " + t.getMessage());
            }
        }
        if (waitTime > 0) {
            ThreadUtils.waitNoInterruption(waitTime);
        }
    }
    
    /**
     * Submit a Runnable for execution after a given delay. If a Runnable
     * with the same key is already scheduled for execution, cancel its
     * execution, replacing it with this Runnable, with the given delay.
     * 
     * Note that if Runnables are submitted regularly, at a frequency lower
     * than their delay, then they will never be executed. For example, if
     * submitting a Runnable every 400ms with a delay of 500ms, no such
     * Runnable will be executed, since the timer is reset before the delay
     * timeout expires.
     *  
     * @param key an identifier to define repeated instances of the same type
     * of Runnable
     * @param delay the delay after which this Runnable will be executed (unless
     * replaced). If 0, the Runnable should execute immediately.
     * @param runnable the Runnable to execute.
     */
    public void submit(final String key, final long delay, final Runnable runnable) {
        synchronized (lock) {
            if (!executorThread.isAlive()) {
                executorThread.start();
            }
            if (existenceLookup.containsKey(key)) {
                final Executable oldExec = existenceLookup.remove(key);
                // notify of replacement - for unit tests
                replacementObservers.eventOccurred(new ExecutableReplaced(oldExec));
                triggerSortedMap.remove(oldExec.getTriggerTime());
            }
            // Ensure uniqueness of the key of the trigger map
            long newTriggerTime = System.currentTimeMillis() + delay;
            while (triggerSortedMap.containsKey(newTriggerTime)) {
                newTriggerTime++;
            }
            final Executable executable = new Executable(key, newTriggerTime, runnable);
            existenceLookup.put(key, executable);
            triggerSortedMap.put(newTriggerTime, executable);
            executorThread.interrupt();
        }
    }

    /**
     * Submit a Runnable to run on the Swing Event Thread.
     * @see submit
     * 
     * @param key an identifier to define repeated instances of the same type
     * of Runnable
     * @param delay the delay after which this Runnable will be executed (unless
     * replaced). If 0, the Runnable should execute immediately.
     * @param runnable the Runnable to execute.
     */
    public void submitGui(final String key, final long delay, final Runnable runnable) {
        submit(key, delay, new Runnable() {
            public void run() {
                SwingUtilities.invokeLater(runnable);
            }
        });
    }
}
