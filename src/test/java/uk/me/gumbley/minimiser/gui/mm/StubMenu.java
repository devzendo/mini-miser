package uk.me.gumbley.minimiser.gui.mm;

/**
 * A Menu that tests can interrogate to test the correctness of the
 * MenuMediator.
 * 
 * @author matt
 *
 */
public final class StubMenu implements Menu {
    private boolean closeMenuEnabled;

    /**
     * @return whether close is enabled
     */
    public boolean isCloseEnabled() {
        return closeMenuEnabled;
    }

    /**
     * {@inheritDoc}
     */
    public void enableCloseMenu(final boolean enabled) {
        closeMenuEnabled = enabled;
    }
}
