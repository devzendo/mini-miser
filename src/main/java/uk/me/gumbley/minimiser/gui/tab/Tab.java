package uk.me.gumbley.minimiser.gui.tab;

import java.awt.Component;

/**
 * A Tab that handles all detail of a specific view into the
 * database.
 * <p>
 * Its lifecycle (startup/shutdown) is managed by various
 * parts of the application, and these lifecycle methods are
 * called either on a worker thread or the Swing Event thread,
 * as appropriate.
 * 
 * @author matt
 *
 */
public interface Tab {
    
    /**
     * Always called on the Swing Event thread, this method
     * is used by the Tab to initialise any Swing components
     * necessary to display the View aspect of the Tab.
     */
    void initComponent();
    
    /**
     * Obtain the main graphical component.
     * 
     * @return the main Component of this Tab, most likely a
     * JPanel. This will be what is seen when the user switches
     * to this Tab using the JTabbedPane that's opened for this
     * database.
     */
    Component getComponent();
}
