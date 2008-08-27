package uk.me.gumbley.minimiser.lifecycle;

/**
 * @author matt
 *
 */
public final class TwoLifecycle implements Lifecycle {
    private final OneLifecycle oneLifecycle;
    /**
     * @param one the one that's configured in
     */
    public TwoLifecycle(final OneLifecycle one) {
        this.oneLifecycle = one;
    }
    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        oneLifecycle.shutdown();
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        oneLifecycle.startup();
    }
    
    /**
     * @return the state
     */
    public String getState() {
        return oneLifecycle.getState();
    }
    /**
     * @return the one
     */
    public Object getOne() {
        return oneLifecycle;
    }
}
