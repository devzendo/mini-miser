package uk.me.gumbley.minimiser.gui.mm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import uk.me.gumbley.commoncode.patterns.observer.ObserverList;
import uk.me.gumbley.minimiser.common.AppName;
import uk.me.gumbley.minimiser.gui.mm.Menu.MenuIdentifier;
import uk.me.gumbley.minimiser.gui.mm.MenuMediatorImpl.DatabaseSwitchObserver;

/**
 * The Swing Menu.
 * 
 * @author matt
 *
 */
public class MenuImpl implements Menu {
    private List<String> databases;
    private int currentDatabaseIndex;
    private class MenuDetails {
        private final JMenuItem menuItem;
        private ActionListener actionListener;

        public MenuDetails(final JMenuItem menuItem) {
            this.menuItem = menuItem;
            this.actionListener = null;
        }

        /**
         * @return the actionListener
         */
        public ActionListener getActionListener() {
            return actionListener;
        }

        /**
         * @param actionListener the actionListener to set
         */
        public void setActionListener(ActionListener actionListener) {
            this.actionListener = actionListener;
        }

        /**
         * @return the menuItem
         */
        public JMenuItem getMenuItem() {
            return menuItem;
        }
    }
    private Map<MenuIdentifier, MenuDetails> menuDetailsMap;

    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu windowMenu;
    private JMenu helpMenu;

    private ObserverList<WindowMenuChoice> windowMenuChoiceObservers;
    
    /**
     * 
     */
    public MenuImpl() {
        databases = new ArrayList<String>();
        currentDatabaseIndex = -1;
        menuDetailsMap = new HashMap<MenuIdentifier, MenuDetails>();

        windowMenuChoiceObservers = new ObserverList<WindowMenuChoice>();

        menuBar = new JMenuBar();
        fileMenu = createFileMenu();
        windowMenu = createWindowMenu();
        helpMenu = createHelpMenu();

        menuBar.add(fileMenu);
        menuBar.add(windowMenu);
        menuBar.add(helpMenu);
    }

    private JMenu createWindowMenu() {
        final JMenu menu = new JMenu("Window");
        menu.setMnemonic('W');
        // no need to call buildWindowMenu() as it's not populated initially
        menu.setEnabled(false);
        return menu;
    }

    private JMenu createHelpMenu() {
        final JMenu menu = new JMenu("Help");
        menu.setMnemonic('H');
        
        createMenuItem(MenuIdentifier.HelpWelcome, "Welcome", 'W', menu);
        createMenuItem(MenuIdentifier.HelpContents, "Help Contents", 'H', menu);
        createMenuItem(MenuIdentifier.HelpAbout, "About " + AppName.getAppName(), 'A', menu);
        
        return menu;
    }

    private JMenu createFileMenu() {
        final JMenu menu = new JMenu("File");
        menu.setMnemonic('F');

        createMenuItem(MenuIdentifier.FileNew, "New...", 'N', menu);
        createMenuItem(MenuIdentifier.FileOpen, "Open...", 'O', menu);

        menu.add(new JSeparator());

        createMenuItem(MenuIdentifier.FileClose, "Close", 'C', menu);
        createMenuItem(MenuIdentifier.FileCloseAll, "Close all", 'l', menu);

        menu.add(new JSeparator());

        createMenuItem(MenuIdentifier.FileImport, "Import...", 'I', menu);
        createMenuItem(MenuIdentifier.FileExport, "Export...", 'E', menu);

        menu.add(new JSeparator());

        createMenuItem(MenuIdentifier.FileExit, "Exit", 'x', menu);

        return menu;
    }
    
    private void createMenuItem(final MenuIdentifier menuIdentifier,
            final String menuItemText, final char mnemonic, final JMenu menu) {
        JMenuItem menuItem = new JMenuItem(menuItemText);
        menuItem.setMnemonic(mnemonic);
        menu.add(menuItem);
        // adding a level of temporal indirection here so that listeners
        // can be wired up after the menu has been created
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                synchronized (menuDetailsMap) {
                    MenuDetails menuDetails = menuDetailsMap.get(menuIdentifier);
                    ActionListener actionListener = menuDetails.getActionListener();
                    if (actionListener != null) {
                        actionListener.actionPerformed(e);
                    }
                }
            }
        });
        synchronized (menuDetailsMap) {
            menuDetailsMap.put(menuIdentifier, new MenuDetails(menuItem));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void enableCloseMenu(final boolean enabled) {
        enableMenuItem(MenuIdentifier.FileClose, enabled);
        enableMenuItem(MenuIdentifier.FileImport, enabled);
        enableMenuItem(MenuIdentifier.FileExport, enabled);
    }
    
    private void enableMenuItem(final MenuIdentifier menuIdentifier, final boolean enabled) {
        synchronized (menuDetailsMap) {
            MenuDetails menuDetails = menuDetailsMap.get(menuIdentifier);
            menuDetails.getMenuItem().setEnabled(enabled);
        }
    }

    private void buildWindowMenu() {
        windowMenu.removeAll();
        if (databases.size() == 0) {
            windowMenu.setEnabled(false);
            return;
        }
        
        for (int i = 0; i < databases.size(); i++) {
            final String database = databases.get(i);
            JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(database, i == currentDatabaseIndex);
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    windowMenuChoiceObservers.eventOccurred(new WindowMenuChoice(database));
                }
            });
            windowMenu.add(menuItem);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addDatabase(final String dbName) {
        databases.add(dbName);
        buildWindowMenu();
    }

    /**
     * {@inheritDoc}
     */
    public void emptyDatabaseList() {
        databases.clear();
        currentDatabaseIndex = -1;
        buildWindowMenu();
        enableCloseMenu(false);
    }

    /**
     * {@inheritDoc}
     */
    public void removeDatabase(final String dbName) {
        databases.remove(dbName);
        buildWindowMenu();
    }

    /**
     * {@inheritDoc}
     */
    public void switchDatabase(final String dbName) {
        currentDatabaseIndex = databases.indexOf(dbName);
        buildWindowMenu();
    }

    /**
     * {@inheritDoc}
     */
    public JMenuBar getMenuBar() {
        return menuBar;
    }

    /**
     * {@inheritDoc}
     */
    public void addDatabaseSwitchObserver(final DatabaseSwitchObserver observer) {
        windowMenuChoiceObservers.addObserver(observer);
    }

    public void addMenuActionListener(MenuIdentifier menuIdentifier, ActionListener actionListener) {
        synchronized (menuDetailsMap) {
            MenuDetails menuDetails = menuDetailsMap.get(menuIdentifier);
            if (menuDetails != null) {
                menuDetails.setActionListener(actionListener);
            }
        }
    }
}
