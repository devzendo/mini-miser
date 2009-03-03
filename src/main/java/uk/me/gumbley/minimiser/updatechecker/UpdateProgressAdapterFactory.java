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
     * Create an UpdateProgressAdapter that uses the main window's
     * progress bar to inform the user of its status.
     * @return the adapter.
     */
    UpdateProgressAdapter createVisibleUpdateProgressAdapter();
    
    /**
     * Create an UpdateProgressAdapter that has no UI, for when
     * the periodic update checker fires.
     * @return the adapter
     */
    UpdateProgressAdapter createBackgroundUpdateProgressAdapter();
}
