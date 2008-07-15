package uk.me.gumbley.minimiser.gui;

/**
 * A facade into the main application frame
 * 
 * @author matt
 *
 */
public interface MainFrameFacade {
    /**
     * @param enable true to enable all the main frame's controls, false to disable
     */
    void enableDisableControls(boolean enable);
    
    /**
     * Shut down the application
     */
    void shutdown();
    
    /**
     * Have we got any databases open?
     * @return true iff one or more databases open.
     */
    boolean anyDatabasesOpen();
}
