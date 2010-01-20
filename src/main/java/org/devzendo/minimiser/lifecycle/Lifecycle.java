package org.devzendo.minimiser.lifecycle;

/**
 * A component that has its lifecycle managed by the application.
 * 
 * @author matt
 *
 */
public interface Lifecycle {
    
    /**
     * Start up the component.
     */
    void startup();
    
    /**
     * Shut down the component.
     */
    void shutdown();
}
