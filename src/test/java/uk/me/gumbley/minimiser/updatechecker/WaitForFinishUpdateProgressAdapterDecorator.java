package uk.me.gumbley.minimiser.updatechecker;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

/**
 * Since the update checker is asynchronous, and executes via a
 * worker pool, I can't just tag on a 'wait' task after running
 * the update, hence this decorator.
 * 
 * @author matt
 *
 */
public final class WaitForFinishUpdateProgressAdapterDecorator implements UpdateProgressAdapter {
    private static final Logger LOGGER = Logger
            .getLogger(WaitForFinishUpdateProgressAdapterDecorator.class);
    private final UpdateProgressAdapter mUpdateProgressAdapter;
    private final CountDownLatch countDownLatch;

    /**
     * Construct the decorator
     * @param decorated the inner update progress adapter
     */
    public WaitForFinishUpdateProgressAdapterDecorator(final UpdateProgressAdapter decorated) {
        this.mUpdateProgressAdapter = decorated;
        countDownLatch = new CountDownLatch(1);
    }

    /**
     * {@inheritDoc}
     */
    public void alreadyCheckedToday() {
        mUpdateProgressAdapter.alreadyCheckedToday();
    }

    /**
     * {@inheritDoc}
     */
    public void checkStarted() {
        mUpdateProgressAdapter.checkStarted();
    }

    /**
     * {@inheritDoc}
     */
    public void noApplicationVersionDeclared() {
        mUpdateProgressAdapter.noApplicationVersionDeclared();
    }

    /**
     * {@inheritDoc}
     */
    public void noUpdateURLDeclared() {
        mUpdateProgressAdapter.noUpdateURLDeclared();
    }

    /**
     * {@inheritDoc}
     */
    public void commsFailure(final IOException exception) {
        mUpdateProgressAdapter.commsFailure(exception);
    }

    /**
     * {@inheritDoc}
     */
    public void noUpdateAvailable() {
        mUpdateProgressAdapter.noUpdateAvailable();
    }

    /**
     * {@inheritDoc}
     */
    public void transformFailure(final IOException exception) {
        mUpdateProgressAdapter.transformFailure(exception);
    }

    /**
     * {@inheritDoc}
     */
    public void transformFailure(final ParseException exception) {
        mUpdateProgressAdapter.transformFailure(exception);
    }

    /**
     * {@inheritDoc}
     */
    public void updateAvailable() {
        mUpdateProgressAdapter.updateAvailable();
        
    }

    /**
     * {@inheritDoc}
     */
    public void updateCheckDisallowed() {
        mUpdateProgressAdapter.updateCheckDisallowed();
    }
    
    /**
     * {@inheritDoc}
     */
    public void finished() {
        mUpdateProgressAdapter.finished();
        countDownLatch.countDown();
    }
    
    /**
     * Wait until the worker thread has called the finished() method on the
     * adapter.
     */
    public void waitForFinished() {
        try {
            countDownLatch.await();
        } catch (final InterruptedException e) {
            LOGGER.warn("Interrupted whilst waiting: " + e.getMessage());
        }
    }
}