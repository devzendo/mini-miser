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
public class DelayedExecutor {
    private static final Logger LOGGER = Logger
            .getLogger(DelayedExecutor.class);
    private TreeSet<Executable> treeSet;
    private Thread executorThread;
    
    /**
     * Construct the DelayedExecutor and start the execution thread.
     */
    public DelayedExecutor() {
        treeSet = new TreeSet<Executable>(new Comparator<Executable>() {
            public int compare(final Executable o1, final Executable o2) {
                if (o1.getDelay() == o2.getDelay()) {
                    return 0;
                }
                if (o1.getDelay() < o2.getDelay()) {
                    return -1;
                }
                return 1;
            }
        });
        executorThread = new Thread(new DelayedExecutorRunnable());
        executorThread.setDaemon(true);
        executorThread.setName("Delayed Executor");
        executorThread.start();
    }
    
    private class Executable {
        private final String ekey;
        private final long edelay;
        private final Runnable erunnable;

        public Executable(final String key, final long delay, final Runnable runnable) {
            this.ekey = key;
            this.edelay = delay;
            this.erunnable = runnable;
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
        public void run() {
            while (true) {
                synchronized (treeSet) {
                    if (treeSet.isEmpty()) {
                        ThreadUtils.waitNoInterruption(1000);
                    } else {
                        Executable executable = treeSet.first();
                        treeSet.remove(executable);
                        try {
                            executable.getRunnable().run();
                        } catch (final Throwable t) {
                            LOGGER.warn("Delayed execution caught a " + t.getClass().getSimpleName() + ": " + t.getMessage());
                        }
                    }
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
        final Executable executable = new Executable(key, delay, runnable);
        synchronized (treeSet) {
            treeSet.add(executable);
            executorThread.interrupt();
        }
    }
}
