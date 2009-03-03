package uk.me.gumbley.minimiser.updatechecker;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Since the update checker is asynchronous, and executes via a
 * worker pool, I can't just tag on a 'wait' task after running
 * the update, hence this decorator.
 * 
 * @author matt
 *
 */
public final class WaitForFinishUpdateProgressAdapterDecorator implements UpdateProgressAdapter {
    private final UpdateProgressAdapter updateProgressAdapter;
    private CountDownLatch countDownLatch;

    /**
     * Construct the decorator
     * @param decorated the inner update progress adapter
     */
    public WaitForFinishUpdateProgressAdapterDecorator(final UpdateProgressAdapter decorated) {
        this.updateProgressAdapter = decorated;
        countDownLatch = new CountDownLatch(1);
    }

    /**
     * {@inheritDoc}
     */
    public void alreadyCheckedToday() {
        updateProgressAdapter.alreadyCheckedToday();
    }

    /**
     * {@inheritDoc}
     */
    public void checkStarted() {
        updateProgressAdapter.checkStarted();
    }

    /**
     * {@inheritDoc}
     */
    public void commsFailure(final IOException exception) {
        updateProgressAdapter.commsFailure(exception);
    }

    /**
     * {@inheritDoc}
     */
    public void noUpdateAvailable() {
        updateProgressAdapter.noUpdateAvailable();
    }

    /**
     * {@inheritDoc}
     */
    public void transformFailure(final IOException exception) {
        updateProgressAdapter.transformFailure(exception);
    }

    /**
     * {@inheritDoc}
     */
    public void transformFailure(final ParseException exception) {
        updateProgressAdapter.transformFailure(exception);
    }

    /**
     * {@inheritDoc}
     */
    public void updateAvailable() {
        updateProgressAdapter.updateAvailable();
        
    }

    /**
     * {@inheritDoc}
     */
    public void updateCheckDisallowed() {
        updateProgressAdapter.updateCheckDisallowed();
    }
    
    /**
     * {@inheritDoc}
     */
    public void finished() {
        updateProgressAdapter.finished();
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
            TestUpdateChecker.LOGGER.warn("Interrupted whilst waiting: " + e.getMessage());
        }
    }
}