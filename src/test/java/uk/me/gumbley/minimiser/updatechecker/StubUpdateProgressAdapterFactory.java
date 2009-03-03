package uk.me.gumbley.minimiser.updatechecker;


/**
 * Creates stub update progress adapters, for tests.
 * 
 * @author matt
 *
 */
public final class StubUpdateProgressAdapterFactory implements
        UpdateProgressAdapterFactory {
    private final UpdateProgressAdapter mAdapter;
    
    /**
     * Construct a factory that returns the given 
     * UpdateProgressAdapter for both situations.
     * @param adapter the adapter to return for both situations.
     */
    public StubUpdateProgressAdapterFactory(
            final UpdateProgressAdapter adapter) {
        this.mAdapter = adapter;
    }

    /**
     * {@inheritDoc}
     */
    public UpdateProgressAdapter createVisibleUpdateProgressAdapter() {
        return mAdapter;
    }

    /**
     * {@inheritDoc}
     */
    public UpdateProgressAdapter createBackgroundUpdateProgressAdapter() {
        return mAdapter;
    }
}
