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
 * <p>
 * The construction of a Tab always takes place on a worker thread,
 * i.e. a non-Swing Event thread. Swing components are always
 * initialsed/disposed of on the Swing Event thread.
 * 
 * @author matt
 *
 */
public interface Tab {
    
    /**
     * Always called on the Swing Event thread, this method
     * is used by the Tab to initialise any Swing components
     * necessary to display the View aspect of the Tab.
     * <p>
     * This method is called shortly after construction.
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
    
    /**
     * Always called on the Swing Event thread, this method is
     * used by the tab to tidy up any Swing components used by
     * the View aspect of the Tab.
     * <p>
     * disposeComponent is always called before destroy(). 
     */
    void disposeComponent();
    
    /**
     * Always called on a non-Swing Event thread, this method is
     * used by the tab to free any non-Swing resources.
     * <p>
     * destroy() is always called after disposeComponent().
     */
    void destroy();
}
