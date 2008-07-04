package uk.me.gumbley.minimiser.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.patterns.observer.ObserverList;
import uk.me.gumbley.minimiser.common.AppName;
import uk.me.gumbley.minimiser.gui.menu.MenuMediatorImpl.DatabaseSwitchObserver;

/**
 * The Swing Menu.
 * 
 * @author matt
 *
 */
public final class MenuImpl implements Menu {
    private static final Logger LOGGER = Logger.getLogger(MenuImpl.class);
    private List<String> databases;
    private int currentDatabaseIndex;

    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu windowMenu;
    private JMenu helpMenu;

    private ObserverList<WindowMenuChoice> windowMenuChoiceObservers;
    private final MenuWiring menuWiring;
    
    /**
     * Create the Menu
     * @param wiring the MenuWiring singleton
     */
    public MenuImpl(final MenuWiring wiring) {
        this.menuWiring = wiring;
        databases = new ArrayList<String>();
        currentDatabaseIndex = -1;

        windowMenuChoiceObservers = new ObserverList<WindowMenuChoice>();

        menuBar = new JMenuBar();
        fileMenu = createFileMenu();
        windowMenu = createWindowMenu();
        helpMenu = createHelpMenu();

        menuBar.add(fileMenu);
        menuBar.add(windowMenu);
        menuBar.add(helpMenu);
        
        enableCloseAllMenu();
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
        final JMenuItem menuItem = new JMenuItem(menuItemText);
        menuItem.setMnemonic(mnemonic);
        menu.add(menuItem);
        menuWiring.storeMenuItem(menuIdentifier, menuItem);
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
        menuWiring.getMenuItem(menuIdentifier).setEnabled(enabled);
    }

    private void buildWindowMenu() {
        LOGGER.debug("building window menu");
        windowMenu.removeAll();
        if (databases.size() == 0) {
            windowMenu.setEnabled(false);
            LOGGER.debug("window menu is empty");
            return;
        }
        LOGGER.debug("building window list");
        for (int i = 0; i < databases.size(); i++) {
            final String database = databases.get(i);
            LOGGER.debug("adding " + database + " to window list");
            final JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(database, i == currentDatabaseIndex);
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    windowMenuChoiceObservers.eventOccurred(new WindowMenuChoice(database));
                }
            });
            windowMenu.add(menuItem);
        }
        windowMenu.setEnabled(true);
    }

    private void enableCloseAllMenu() {
        enableMenuItem(MenuIdentifier.FileCloseAll, databases.size() != 0);
    }
    
    /**
     * {@inheritDoc}
     */
    public void addDatabase(final String dbName) {
        databases.add(dbName);
        buildWindowMenu();
        enableCloseAllMenu();
    }

    /**
     * {@inheritDoc}
     */
    public void emptyDatabaseList() {
        databases.clear();
        currentDatabaseIndex = -1;
        buildWindowMenu();
        enableCloseMenu(false);
        enableCloseAllMenu();
    }

    /**
     * {@inheritDoc}
     */
    public void removeDatabase(final String dbName) {
        databases.remove(dbName);
        buildWindowMenu();
        enableCloseAllMenu();
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
        synchronized (menuBar) {
            return menuBar;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addDatabaseSwitchObserver(final DatabaseSwitchObserver observer) {
        windowMenuChoiceObservers.addObserver(observer);
    }

    /**
     * {@inheritDoc}
     */
    public void addMenuActionListener(final MenuIdentifier menuIdentifier, final ActionListener actionListener) {
        menuWiring.setActionListener(menuIdentifier, actionListener);
    }
}