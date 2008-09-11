package uk.me.gumbley.minimiser.gui.tab.impl;

import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JPanel;
import uk.me.gumbley.minimiser.gui.tab.Tab;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;

/**
 * A do-very-little, non-TDD Demo tab
 * 
 * @author matt
 *
 */
public final class DemoTab implements Tab {
    private final DatabaseDescriptor databaseDescriptor;
    private volatile JPanel panel;

    /**
     * Create the demo tab given a database descriptor 
     * @param descriptor the database descriptor
     */
    public DemoTab(final DatabaseDescriptor descriptor) {
        this.databaseDescriptor = descriptor;
        
    }
    /**
     * {@inheritDoc}
     */
    public Component getComponent() {
        return panel;
    }

    /**
     * {@inheritDoc}
     */
    public void initComponent() {
        panel = new JPanel();
        panel.add(new JButton("This is a demo tab for the " + databaseDescriptor.getDatabaseName() + " database"));
    }
}
