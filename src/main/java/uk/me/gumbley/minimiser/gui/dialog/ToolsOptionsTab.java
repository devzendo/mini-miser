package uk.me.gumbley.minimiser.gui.dialog;

import java.awt.Component;

/**
 * A Tab that handles all detail of a specific view into the
 * Preferences
 * <p>
 * Its construction is managed by the ToolsOptionsTabFactory,
 * and these lifecycle methods are called either on a worker thread or the
 * Swing Event thread, as appropriate.
 * <p>
 * The construction of a ToolsOptionsTab always takes place on a worker thread,
 * i.e. a non-Swing Event thread. Swing components are always
 * initialsed/disposed of on the Swing Event thread.
 * 
 * @author matt
 *
 */
public interface ToolsOptionsTab {
    
    /**
     * Always called on the Swing Event thread, this method
     * is used by the Tab to initialise any Swing components
     * necessary to display the View aspect of the ToolsOptionsTab.
     * <p>
     * This method is called shortly after construction.
     */
    void initComponent();
    
    /**
     * Obtain the main graphical component.
     * 
     * @return the main Component of this ToolsOptionsTab, most likely a
     * JPanel. This will be what is seen when the user switches
     * to this ToolsOptionsTab using the JTabbedPane.
     */
    Component getComponent();
    
    /**
     * Always called on the Swing Event thread, this method is
     * used by the tab to tidy up any Swing components used by
     * the View aspect of the ToolsOptionsTab.
     * <p>
     * disposeComponent is always called before destroy(). 
     */
    void disposeComponent();
    
    /**
     * Always called on a non-Swing Event thread, this method is
     * used by the ToolsOptionsTab to free any non-Swing resources.
     * <p>
     * destroy() is always called after disposeComponent().
     */
    void destroy();
}
