package uk.me.gumbley.minimiser.lifecycle;

/**
 * @author matt
 *
 */
public final class OneLifecycle implements Lifecycle {
    private String state = "new-init";
    /**
     * 
     */
    public OneLifecycle() {
        state = "ctor";
    }
    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        state = "shut down";
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        state = "started";
    }
    
    /**
     * @return the state
     */
    public String getState() {
        return state;
    }
}
