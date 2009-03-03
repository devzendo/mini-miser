package uk.me.gumbley.minimiser.wiring.lifecycle;

import org.apache.log4j.Logger;

import uk.me.gumbley.minimiser.lifecycle.Lifecycle;
import uk.me.gumbley.minimiser.updatechecker.UpdateChecker;
import uk.me.gumbley.minimiser.updatechecker.UpdateProgressAdapter;
import uk.me.gumbley.minimiser.updatechecker.UpdateProgressAdapterFactory;
import uk.me.gumbley.minimiser.util.Sleeper;

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
