package uk.me.gumbley.minimiser.gui.menu;

import java.awt.event.ActionListener;
import javax.swing.JMenuBar;
import uk.me.gumbley.minimiser.gui.menu.MenuMediatorImpl.DatabaseSwitchObserver;


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
     * Add an observer of window menu database switches
     * @param observer of switches
     */
    void addDatabaseSwitchObserver(final DatabaseSwitchObserver observer);
}