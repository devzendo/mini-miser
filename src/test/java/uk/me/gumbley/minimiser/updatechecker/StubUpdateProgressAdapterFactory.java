package uk.me.gumbley.minimiser.updatechecker;

import uk.me.gumbley.minimiser.updatechecker.UpdateProgressAdapter;
import uk.me.gumbley.minimiser.updatechecker.UpdateProgressAdapterFactory;

/**
 * Creates stub update progress adapters.
 *  
 * @author matt
 *
 */
public final class StubUpdateProgressAdapterFactory implements
        UpdateProgressAdapterFactory {
    /**
     * {@inheritDoc}
     */
    public UpdateProgressAdapter createUpdateProgressAdapter() {
        return new StubUpdateProgressAdapter();
    }
}
