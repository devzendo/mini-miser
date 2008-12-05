package uk.me.gumbley.minimiser.updatechecker;

/**
 * Allows the progress of update checks to be fed back to the user in varying
 * ways.
 * 
 * @author matt
 *
 */
public interface UpdateProgressAdapterFactory {

    /**
     * Create an UpdateProgressAdapter.
     * @return the adapter.
     */
    UpdateProgressAdapter createUpdateProgressAdapter();
}
