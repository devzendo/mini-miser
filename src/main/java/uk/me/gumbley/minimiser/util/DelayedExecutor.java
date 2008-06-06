package uk.me.gumbley.minimiser.util;

import java.util.Comparator;
import java.util.TreeSet;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.concurrency.ThreadUtils;

/**
 * Allows Runnables to be enqueued and run after some specified delay.
 * Runnables are submitted with a key, and if many are submitted with the
 * same key, the last one submitted is executed (at the end of the delay).
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
    private TreeSet<Executable> treeSet;
    private Thread executorThread;
    
    /**
     * Construct the DelayedExecutor and start the execution thread.
     */
    public DelayedExecutor() {
        ExecutableComparator comparator = new ExecutableComparator();
        treeSet = new TreeSet<Executable>(comparator);
        executorThread = new Thread(new DelayedExecutorRunnable());
        executorThread.setDaemon(true);
        executorThread.setName("Delayed Executor");
        executorThread.start();
    }
    // Note: this comparator imposes orderings that are inconsistent with equals.
    // If it returns 0 for Executables that have the same trigger time,
    // then the TreeSet will say that the Executable is already contained.
    // TreeSet seems to use this comparator for detecting containment in the
    // set, rather than equals.
    static class ExecutableComparator implements Comparator<Executable> {
        public int compare(final Executable o1, final Executable o2) {
            if (o1.getKey().equals(o2.getKey())) {
                return 0;
            }
            if (o1.getTriggerTime() < o2.getTriggerTime()) {
                return -1;
            }
            return 1;
        }
    }
    
    static class Executable {
        private final String ekey;
        private final long edelay;
        private final long etriggerTime;
        private final Runnable erunnable;

        public Executable(final String key, final long delay, final Runnable runnable) {
            this.ekey = key;
            this.edelay = delay;
            this.etriggerTime = System.currentTimeMillis() + edelay;
            this.erunnable = runnable;
        }
        
        public String toString() {
            return String.format("[%s] delay %d trigger %d", ekey, edelay, etriggerTime);
        }

        /**
         * @return the delay
         */
        public long getDelay() {
            return edelay;
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
    
    private class DelayedExecutorRunnable implements Runnable {
        private static final long DEFAULT_EMPTY_TREE_WAIT_TIME = 1000L;

        public void run() {
            LOGGER.debug("Started DelayedExecutor");
            while (true) {
                Executable executable = null;
                long waitTime = DEFAULT_EMPTY_TREE_WAIT_TIME; 
                // stay in sync block for as short as possible
                synchronized (treeSet) {
                    LOGGER.debug("Tree size in loop is " + treeSet.size());
                    if (!treeSet.isEmpty()) {
                        executable = treeSet.first();
                        long now = System.currentTimeMillis();
                        final long triggerTime = executable.getTriggerTime();
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug(executable + " now=" + now);
                        }
                        if (triggerTime > now) {
                            waitTime = triggerTime - now; // wait for this first Executable to trigger
                            executable = null;            // since its time is not yet due
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("Not yet time; Waiting for " + waitTime + "ms");
                            }
                        } else {
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("Removing " + executable + " (expired " + (now - triggerTime) + " ms ago)");
                            }
                            treeSet.remove(executable);   // wake up! time to die! you will be executed
                            waitTime = 0L;                // don't sleep, go check the tree again
                        }
                    } else {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Empty tree");
                        }
                    }
                }
                if (executable != null) {
                    try {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug(">>> Executing " + executable);
                        }
                        executable.getRunnable().run();
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("<<< Executed " + executable);
                        }
                    } catch (final Throwable t) {
                        LOGGER.warn("Delayed execution caught a " + t.getClass().getSimpleName() + ": " + t.getMessage());
                    }
                }
                if (waitTime > 0) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Waiting for " + waitTime + "ms");
                    }
                    ThreadUtils.waitNoInterruption(waitTime);
                }
            }
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
        synchronized (treeSet) {
        final Executable executable = new Executable(key, delay, runnable);
//        if (LOGGER.isDebugEnabled()) {
//            LOGGER.debug("Submitted " + executable);
//        }
        LOGGER.debug("Tree size before add of " + key + " is " + treeSet.size());
        LOGGER.debug("hashCode of " + key + " is " + key.hashCode() + " executable's hashcode is " + executable.hashCode());
LOGGER.debug("Tree already contains " + key + "? " + treeSet.contains(executable));
            treeSet.add(executable);
            LOGGER.debug("Tree size after add is " + treeSet.size());
            executorThread.interrupt();
        }
    }
}
