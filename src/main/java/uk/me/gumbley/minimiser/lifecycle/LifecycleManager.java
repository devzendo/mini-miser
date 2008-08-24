package uk.me.gumbley.minimiser.lifecycle;

import uk.me.gumbley.minimiser.springbeanlistloader.SpringBeanListLoader;


/**
 * Manager that controls the lifecycle of various objects. Allows objects to
 * be started when the main window is made visible, and stopped when the
 * main window is closed.
 *  
 * @author matt
 *
 */
public interface LifecycleManager extends SpringBeanListLoader<Lifecycle> {
    /**
     * Start up the beans managed by the LifecycleManager, in the order that
     * they were given when instantiating the LifecycleManager. 
     */
    void startup();

    /**
     * Shut down the beans managed by the LifecycleManager, in the reverse order
     * that they were given when instantiating the LifecycleManager. 
     */
    void shutdown();
}
