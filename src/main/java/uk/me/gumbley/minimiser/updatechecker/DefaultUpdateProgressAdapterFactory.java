package uk.me.gumbley.minimiser.updatechecker;

/**
 * Creates UpdateProgressAdapters that either provide feedback via
 * the progress bar, or are invisible.
 * 
 * @author matt
 *
 */
public final class DefaultUpdateProgressAdapterFactory implements
        UpdateProgressAdapterFactory {
    /**
     * {@inheritDoc}
     */
    public UpdateProgressAdapter createVisibleUpdateProgressAdapter() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public UpdateProgressAdapter createBackgroundUpdateProgressAdapter() {
        // TODO Auto-generated method stub
        return null;
    }
}
