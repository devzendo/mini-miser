package uk.me.gumbley.minimiser.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.commoncode.patterns.observer.ObserverList;
import uk.me.gumbley.minimiser.common.AppName;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;

/**
 * The Swing Menu.
 * 
 * @author matt
 *
 */
public final class MenuImpl implements Menu {
    private static final Logger LOGGER = Logger.getLogger(MenuImpl.class);
    private Object lock = new Object();
    private List<String> databases;
    private int currentDatabaseIndex;
    private DatabaseDescriptor[] recentDatabaseDescriptors;

    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu windowMenu;
    private JMenu helpMenu;

    private ObserverList<DatabaseNameChoice> windowMenuChoiceObservers;
    private ObserverList<DatabaseNameAndPathChoice> openRecentSubmenuChoiceObservers;
    private MenuWiring menuWiring;
    
    /**
     * Create the Menu
     * @param wiring the MenuWiring singleton
     */
    public MenuImpl(final MenuWiring wiring) {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                synchronized (lock) {
                    menuWiring = wiring;
                    databases = new ArrayList<String>();
                    currentDatabaseIndex = -1;
                    recentDatabaseDescriptors = new DatabaseDescriptor[0];
            
                    windowMenuChoiceObservers = new ObserverList<DatabaseNameChoice>();
                    openRecentSubmenuChoiceObservers = new ObserverList<DatabaseNameAndPathChoice>();
        
                    menuBar = new JMenuBar();
                    fileMenu = createFileMenu();
                    buildFileMenu(); // initially we'll have no recent files list
                    windowMenu = createWindowMenu();
                    helpMenu = createHelpMenu();
    
                    menuBar.add(fileMenu);
                    menuBar.add(windowMenu);
                    menuBar.add(helpMenu);
                    
                    enableCloseAllMenu();
                }
            }
        });
    }

    // EDT
    private JMenu createWindowMenu() {
        final JMenu menu = new JMenu("Window");
        menu.setMnemonic('W');
        // no need to call buildWindowMenu() as it's not populated initially
        menu.setEnabled(false);
        return menu;
    }

    // EDT
    private JMenu createHelpMenu() {
        final JMenu menu = new JMenu("Help");
        menu.setMnemonic('H');
        
        createMenuItem(MenuIdentifier.HelpWelcome, "Welcome", 'W', menu);
        createMenuItem(MenuIdentifier.HelpContents, "Help Contents", 'H', menu);
        createMenuItem(MenuIdentifier.HelpAbout, "About " + AppName.getAppName(), 'A', menu);
        
        return menu;
    }

    // EDT
    private JMenu createFileMenu() {
        final JMenu menu = new JMenu("File");
        menu.setMnemonic('F');
        return menu;
    }

    // EDT
    private void buildFileMenu() {
        fileMenu.removeAll();
        createMenuItem(MenuIdentifier.FileNew, "New...", 'N', fileMenu);
        createMenuItem(MenuIdentifier.FileOpen, "Open...", 'O', fileMenu);
        fileMenu.add(buildRecentList());

        fileMenu.add(new JSeparator());

        createMenuItem(MenuIdentifier.FileClose, "Close", 'C', fileMenu);
        createMenuItem(MenuIdentifier.FileCloseAll, "Close all", 'l', fileMenu);

        fileMenu.add(new JSeparator());

        createMenuItem(MenuIdentifier.FileImport, "Import...", 'I', fileMenu);
        createMenuItem(MenuIdentifier.FileExport, "Export...", 'E', fileMenu);

        fileMenu.add(new JSeparator());

        createMenuItem(MenuIdentifier.FileExit, "Exit", 'x', fileMenu);
    }
    
    // EDT
    private JMenu buildRecentList() {
        final JMenu submenu = new JMenu("Open recent");
        submenu.setMnemonic('r');
        if (recentDatabaseDescriptors.length == 0) {
            submenu.setEnabled(false);
        } else {
            for (int i = 0; i < recentDatabaseDescriptors.length; i++) {
                final String recentDbName = recentDatabaseDescriptors[i].getDatabaseName();
                final String recentDbPath = recentDatabaseDescriptors[i].getDatabasePath();
                final int mnemonic = 1 + i;
                final JMenuItem menuItem = new JMenuItem("" + mnemonic + " " + recentDbName);
                menuItem.setMnemonic(KeyEvent.VK_0 + mnemonic);
                menuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent e) {
                        new Thread(new Runnable() {
                            public void run() {
                                Thread.currentThread().setName("RecentOpener:" + recentDbName);
                                Thread.currentThread().setPriority(Thread.MIN_PRIORITY + 1);
                                LOGGER.info("Opening recent database '" + recentDbName + "'");
                                openRecentSubmenuChoiceObservers.eventOccurred(new DatabaseNameAndPathChoice(recentDbName, recentDbPath));        
                            }
                        }).start();
                    }
                });
                submenu.add(menuItem);
            }
        }
        return submenu;
    }

    // EDT
    private void createMenuItem(final MenuIdentifier menuIdentifier,
            final String menuItemText, final char mnemonic, final JMenu menu) {
        final JMenuItem menuItem;
        if (menuWiring.getMenuItem(menuIdentifier) != null) {
            menuItem = menuWiring.getMenuItem(menuIdentifier);
        } else {
            menuItem = new JMenuItem(menuItemText);
            menuItem.setMnemonic(mnemonic);
            menuWiring.storeMenuItem(menuIdentifier, menuItem);
        }
        menu.add(menuItem);
    }

    /**
     * {@inheritDoc}
     */
    // EDT
    public void enableCloseMenu(final boolean enabled) {
        LOGGER.debug("Close menu is " + (enabled ? "enabled" : "disabled"));
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                synchronized (lock) {
                    enableMenuItem(MenuIdentifier.FileClose, enabled);
                    enableMenuItem(MenuIdentifier.FileImport, enabled);
                    enableMenuItem(MenuIdentifier.FileExport, enabled);
                }
            }
        });
    }
    
    // EDT
    private void enableMenuItem(final MenuIdentifier menuIdentifier, final boolean enabled) {
        LOGGER.debug((enabled ? "Enabling " : "Disabling ") + menuIdentifier);
        final JMenuItem menuItem = menuWiring.getMenuItem(menuIdentifier);
        menuItem.setEnabled(enabled);
    }

    // EDT
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
                    windowMenuChoiceObservers.eventOccurred(new DatabaseNameChoice(database));
                }
            });
            windowMenu.add(menuItem);
        }
        windowMenu.setEnabled(true);
    }

    // EDT
    private void enableCloseAllMenu() {
        LOGGER.debug("Close All Menu - size of database list is "  + databases.size());
        enableMenuItem(MenuIdentifier.FileCloseAll, databases.size() != 0);
    }
    
    /**
     * {@inheritDoc}
     */
    // EDT
    public void addDatabase(final String dbName) {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                synchronized (lock) {
                    databases.add(dbName);
                    buildWindowMenu();
                    enableCloseAllMenu();
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void emptyDatabaseList() {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                synchronized (lock) {
                    databases.clear();
                    currentDatabaseIndex = -1;
                    buildWindowMenu();
                    enableCloseMenu(false);
                    enableCloseAllMenu();
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void removeDatabase(final String dbName) {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                synchronized (lock) {
                    databases.remove(dbName);
                    buildWindowMenu();
                    enableCloseAllMenu();
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void switchDatabase(final String dbName) {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                synchronized (lock) {
                    currentDatabaseIndex = databases.indexOf(dbName);
                    buildWindowMenu();
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public JMenuBar getMenuBar() {
        synchronized (lock) {
            synchronized (menuBar) {
                return menuBar;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addDatabaseSwitchObserver(final Observer<DatabaseNameChoice> observer) {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                synchronized (lock) {
                    windowMenuChoiceObservers.addObserver(observer);
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void addMenuActionListener(final MenuIdentifier menuIdentifier, final ActionListener actionListener) {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                synchronized (lock) {
                    menuWiring.setActionListener(menuIdentifier, actionListener);
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void refreshRecentList(final DatabaseDescriptor[] dbPairs) {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                synchronized (lock) {
                    recentDatabaseDescriptors = dbPairs;
                    buildFileMenu();
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void addOpenRecentObserver(final Observer<DatabaseNameAndPathChoice> observer) {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                synchronized (lock) {
                    openRecentSubmenuChoiceObservers.addObserver(observer);
                }
            }
        });
    }
}
