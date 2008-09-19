package uk.me.gumbley.minimiser.gui.menu;

import java.awt.event.ActionListener;
import javax.swing.JMenuBar;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;


/**
 * The MiniMiser menu is controlled by an implementation of Menu.
 *  
 * @author matt
 *
 */
public interface Menu {
    
    public enum MenuIdentifier {
        /**
         * 
         */
        FileNew,
        /**
         * 
         */
        FileOpen,
        /**
         * 
         */
        FileClose,
        /**
         * 
         */
        FileCloseAll,
        /**
         * 
         */
        FileImport,
        /**
         * 
         */
        FileExport,
        /**
         * 
         */
        FileExit,
        
        // Window menu is handled internally by the menu
        // View menu is handled internally by the menu
        
        /**
         * 
         */
        HelpWelcome,
        /**
         * 
         */
        HelpAbout,
        /**
         * 
         */
        HelpContents
    }
    
    /**
     * Wire an ActionListener to a specific menu item
     * @param menuIdentifier the menu item's identifier
     * @param actionListener the ActionListener to fire upon menu item triggering
     */
    void addMenuActionListener(MenuIdentifier menuIdentifier, ActionListener actionListener);
    
    /**
     * Obtain the main GUI component for the menu, for attaching to the
     * main window.
     * @return the menu bar
     */
    JMenuBar getMenuBar();
    
    /**
     * Enable or disable the close menu item.
     * @param enabled true to enable, false to disable.
     */
    void enableCloseMenu(boolean enabled);
    

    /**
     * Add a database to the menu
     * @param dbName the name of the database, which will be unique
     */
    void addDatabase(String dbName);


    /**
     * Switch to a database that exists in the list
     * @param dbName the name of the databse
     */
    void switchDatabase(String dbName);


    /**
     * Remove a database from the menu
     * @param dbName the name of the database
     */
    void removeDatabase(String dbName);


    /**
     * The last database has been closed. Reset to initial state.
     */
    void emptyDatabaseList();
    
    /**
     * The list of recent databases has changed; refresh it.
     * @param databaseDescriptors the list of database name/paths, in order
     */
    void refreshRecentList(DatabaseDescriptor[] databaseDescriptors);

    /**
     * Add an observer of window menu database switches
     * @param observer of switches
     * TODO this appears to have not been added TDD - need to write tests
     * for observation of database switching?
     */
    void addDatabaseSwitchObserver(Observer<DatabaseNameChoice> observer);
    
    /**
     * Add an observer of open recent submenu database open requests
     * @param observer of open recent requests
     */
    void addOpenRecentObserver(Observer<DatabaseNameAndPathChoice> observer);

    /**
     * Set a view menu item hidden
     * @param tabName the menu item name
     * @param tabHidden true iff hidden
     */
    void setTabHidden(String tabName, boolean tabHidden);

    /**
     * Rebuild the view menu based on the current hidden flags.
     */
    void rebuildViewMenu();
}
