package uk.me.gumbley.minimiser.lifecycle;

import java.util.List;

/**
 * Manager that controls the lifecycle of various objects. Allows objects to
 * be started when the main window is made visible, and stopped when the
 * main window is closed.
 *  
 * @author matt
 *
 */
public interface LifecycleManager {

    /**
     * Obtain a list of Lifecycle bean names
     * @return the list of bean names
     */
    List<String> getLifecycleBeanNames();

    /**
     * Obtain a Lifecycle bean by its name
     * @param beanName the bean name
     * @return a Lifecycle
     */
    Lifecycle getLifecycle(String beanName);

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
