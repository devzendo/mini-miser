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
    private final UpdateProgressAdapter mVisibleUpdateProgressAdapter;

    /**
     * Construct the factory using the singleton GUI based update
     * progress adapter.
     * @param visibleUpdateProgressAdapter the default GUI UPA.
     */
    public DefaultUpdateProgressAdapterFactory(final UpdateProgressAdapter visibleUpdateProgressAdapter) {
        this.mVisibleUpdateProgressAdapter = visibleUpdateProgressAdapter;
    }

    /**
     * {@inheritDoc}
     */
    public UpdateProgressAdapter createVisibleUpdateProgressAdapter() {
        return mVisibleUpdateProgressAdapter;
    }

    /**
     * {@inheritDoc}
     */
    public UpdateProgressAdapter createBackgroundUpdateProgressAdapter() {
        return new NullUpdateProgressAdapter();
    }
}
