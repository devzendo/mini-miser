package uk.me.gumbley.minimiser.opentablist;

import java.awt.Component;
import uk.me.gumbley.minimiser.gui.tab.Tab;

/**
 * A Tab that can be used for simple tests.
 * 
 * @author matt
 *
 */
public final class StubTab implements Tab {
    
    private final Component component;

    /**
     * Create a StubTab with a Component
     * @param compo the component
     */
    public StubTab(final Component compo) {
        this.component = compo;
    }
    
    /**
     * {@inheritDoc}
     */
    public Component getComponent() {
        return component;
    }

    /**
     * {@inheritDoc}
     */
    public void initComponent() {
        // do nothing
    }
}
