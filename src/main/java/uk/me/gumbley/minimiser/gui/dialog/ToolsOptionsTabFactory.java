package uk.me.gumbley.minimiser.gui.dialog;

import java.util.List;
import uk.me.gumbley.minimiser.prefs.Prefs;

/**
 * The factory for creating one, or a list of Tools->Options dialog tabs given
 * a change-collecting Prefs.
 * 
 * @author matt
 *
 */
public interface ToolsOptionsTabFactory {

    /**
     * Load the supplied list of tabs.
     * <p>
     * Tabs are loaded as a list of beans from the Application Context.
     * <p>
     * The Prefs passed here will be available from the SpringLoader via a
     * <constructor-arg ref="changeCollectingPrefs" /> passed to
     * each tab's bean definition.
     * 
     * @param prefs the change-collecting Prefs that the Tools->Options tabs
     * modify.
     * @return the list of loaded ToolsOptionsTabss
     */
    List<ToolsOptionsTab> loadTabs(Prefs prefs);

    /**
     * Close a list of tabs.
     * 
     * @param tabs the tabs that are to be closed.
     */
    void closeTabs(List<ToolsOptionsTab> tabs);
}
