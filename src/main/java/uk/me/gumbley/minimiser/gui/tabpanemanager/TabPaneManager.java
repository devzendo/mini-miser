package uk.me.gumbley.minimiser.gui.tabpanemanager;

/**
 * The Tab Pane Manager's responsibilities are:
 * <ul>
 * <li> adding/removing cards for open/closed databases (upon calls from an
 *      OpenDatabaseList listener)</li>
 * <li> adding/removing tabs for open/closed views (upon calls from menu
 *      actionlisteners)</li>
 * <li> switching the current card to match the current database (upon calls
 *      from an OpenDatabaseList listener)</li>
 * <li> providing notification mechanism for open/close/switch tab</li>
 * </ul>
 * 
 * @author matt
 *
 */
public interface TabPaneManager {
    
    /**
     * Identifiers for the possible set of tabs that may be enabled, depending
     * on current state, and in some cases, prefs flags.
     * 
     * @author matt
     *
     */
    public enum TabIdentifier {
        /**
         * The SQL debug/developer tab
         */
        SQL
    }
}
