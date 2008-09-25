package uk.me.gumbley.minimiser.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import org.apache.log4j.Logger;
import uk.me.gumbley.minimiser.gui.menu.Menu.MenuIdentifier;

/**
 * The File Menu is rebuilt when the Open Recent -> list is changed. It has
 * the Close / Close All (and later, other) menu items disabled when there
 * is no database open.
 * 
 * @author matt
 *
 */
public final class FileMenu extends AbstractRebuildableMenuGroup {
    private static final Logger LOGGER = Logger.getLogger(FileMenu.class);
    private JMenu fileMenu;

    /**
     * Construct the File Menu.
     * 
     * @param wiring the menu wiring
     * @param state the menu state
     * @param menu the main menu
     */
    public FileMenu(final MenuWiring wiring, final MenuState state, final MenuImpl menu) {
        super(wiring, state, menu);
        
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');


        // And trigger the first build; initially we'll have no recent files
        // list.
        rebuildMenuGroup();
        
        enableCloseAllMenuIfDatabasesOpen();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rebuildMenuGroup() {
        fileMenu.removeAll();
        getMainMenu().createMenuItem(MenuIdentifier.FileNew, "New...", 'N', fileMenu);
        getMainMenu().createMenuItem(MenuIdentifier.FileOpen, "Open...", 'O', fileMenu);
        fileMenu.add(buildRecentList());

        fileMenu.add(new JSeparator());

        getMainMenu().createMenuItem(MenuIdentifier.FileClose, "Close", 'C', fileMenu);
        getMainMenu().createMenuItem(MenuIdentifier.FileCloseAll, "Close all", 'l', fileMenu);

        // TODO enable import / export in 0.2
//        fileMenu.add(new JSeparator());
//
//        getMainMenu().createMenuItem(MenuIdentifier.FileImport, "Import...", 'I', fileMenu);
//        getMainMenu().createMenuItem(MenuIdentifier.FileExport, "Export...", 'E', fileMenu);

        fileMenu.add(new JSeparator());

        getMainMenu().createMenuItem(MenuIdentifier.FileExit, "Exit", 'x', fileMenu);
    }

    
    // EDT
    private JMenu buildRecentList() {
        final JMenu submenu = new JMenu("Open recent");
        submenu.setMnemonic('r');
        if (getMainMenu().getNumberOfRecentDatabaseDescriptors() == 0) {
            submenu.setEnabled(false);
        } else {
            for (int i = 0; i < getMainMenu().getNumberOfRecentDatabaseDescriptors(); i++) {
                final String recentDbName = getMainMenu().getRecentDatabaseDescriptor(i).getDatabaseName();
                final String recentDbPath = getMainMenu().getRecentDatabaseDescriptor(i).getDatabasePath();
                final int mnemonic = 1 + i;
                final JMenuItem menuItem = new JMenuItem("" + mnemonic + " " + recentDbName);
                menuItem.setMnemonic(KeyEvent.VK_0 + mnemonic);
                menuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent e) {
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    Thread.currentThread().setName("RecentOpener:" + recentDbName);
                                    Thread.currentThread().setPriority(Thread.MIN_PRIORITY + 1);
                                    LOGGER.info("Opening recent database '" + recentDbName + "'");
                                    getMainMenu().dispatchToRecentSubmenuChoiceObservers(
                                        new DatabaseNameAndPathChoice(recentDbName, recentDbPath));        
                                } catch (final Throwable t) {
                                    LOGGER.error("Recent opener thread caught unexpected " + t.getClass().getSimpleName(), t);
                                } finally {
                                    LOGGER.debug("Open recent complete");
                                }
                            }
                        }).start();
                    }
                });
                submenu.add(menuItem);
            }
        }
        return submenu;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public JMenu getJMenu() {
        return fileMenu;
    }
    
    /**
     * Enable the close all menu item, if there are any open databases.
     */
    public void enableCloseAllMenuIfDatabasesOpen() {
        final int numberOfDatabases = getMainMenu().getNumberOfDatabases();
        LOGGER.debug("Close All Menu - size of database list is "  + numberOfDatabases);
        getMenuWiring().setMenuItemEnabled(MenuIdentifier.FileCloseAll, numberOfDatabases != 0);
    }

    /**
     * Enable or disable the close menu item (and later, import/export items) 
     * @param enabled true to enable; false to disable
     */
    public void enableCloseMenu(final boolean enabled) {
        LOGGER.debug("Close menu is " + (enabled ? "enabled" : "disabled"));
        getMenuWiring().setMenuItemEnabled(MenuIdentifier.FileClose, enabled);
        // TODO enable import / export in 0.2
        // getMenuWiring().setMenuItemEnabled(MenuIdentifier.FileImport, enabled);
        // getMenuWiring().setMenuItemEnabled(MenuIdentifier.FileExport, enabled);
    }
}
