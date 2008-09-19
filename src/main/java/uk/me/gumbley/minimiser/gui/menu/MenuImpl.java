package uk.me.gumbley.minimiser.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import uk.me.gumbley.minimiser.gui.tab.TabIdentifier;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;
import uk.me.gumbley.minimiser.prefs.Prefs;

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
    private JMenu viewMenu;
    private JMenu windowMenu;
    private JMenu helpMenu;

    private ObserverList<DatabaseNameChoice> windowMenuChoiceObservers;
    private ObserverList<DatabaseNameAndPathChoice> openRecentSubmenuChoiceObservers;
    private MenuWiring menuWiring;
    private Prefs prefs;  // TODO remove coupling
    private Map<String, Boolean> hiddenTabs;

    /**
     * Create the Menu
     * @param wiring the MenuWiring singleton
     * @param preferences the Prefs singleton
     */
    public MenuImpl(final MenuWiring wiring, final Prefs preferences) {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                synchronized (lock) {
                    menuWiring = wiring;
                    prefs = preferences; // TODO remove coupling
                    databases = new ArrayList<String>();
                    currentDatabaseIndex = -1;
                    recentDatabaseDescriptors = new DatabaseDescriptor[0];
            
                    windowMenuChoiceObservers = new ObserverList<DatabaseNameChoice>();
                    openRecentSubmenuChoiceObservers = new ObserverList<DatabaseNameAndPathChoice>();

                    hiddenTabs = new HashMap<String, Boolean>();
        
                    menuBar = new JMenuBar();
                    fileMenu = createFileMenu();
                    buildFileMenu(); // initially we'll have no recent files list
                    viewMenu = createViewMenu();
                    buildViewMenu();
                    windowMenu = createWindowMenu();
                    helpMenu = createHelpMenu();
    
                    menuBar.add(fileMenu);
                    menuBar.add(viewMenu);
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
        
        //createMenuItem(MenuIdentifier.HelpWelcome, "Welcome", 'W', menu);
        //createMenuItem(MenuIdentifier.HelpContents, "Help Contents", 'H', menu);
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

        // TODO enable import / export in 0.2
//        fileMenu.add(new JSeparator());
//
//        createMenuItem(MenuIdentifier.FileImport, "Import...", 'I', fileMenu);
//        createMenuItem(MenuIdentifier.FileExport, "Export...", 'E', fileMenu);

        fileMenu.add(new JSeparator());

        createMenuItem(MenuIdentifier.FileExit, "Exit", 'x', fileMenu);
    }

    // EDT
    private JMenu createViewMenu() {
        final JMenu menu = new JMenu("View");
        menu.setMnemonic('V');
        return menu;
    }
    
    // EDT
    private void buildViewMenu() {
        viewMenu.removeAll();
        if (databases.size() == 0) {
            viewMenu.setEnabled(false);
            LOGGER.debug("view menu is empty");
            return;
        }

        for (final TabIdentifier tabId : TabIdentifier.values()) {
            if (!tabId.isTabPermanent() && !isViewMenuItemHidden(tabId)) {
                final JMenuItem menuItem = new JMenuItem(tabId.getDisplayableName());
                menuItem.setMnemonic(tabId.getMnemonic());
                menuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent e) {
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    Thread.currentThread().setName("ViewOpener:" + tabId.getDisplayableName());
                                    Thread.currentThread().setPriority(Thread.MIN_PRIORITY + 1);
                                    LOGGER.info("Opening view '" + tabId.getDisplayableName() + "'");
                                    //openRecentSubmenuChoiceObservers.eventOccurred(new DatabaseNameAndPathChoice(recentDbName, recentDbPath));        
                                } catch (final Throwable t) {
                                    LOGGER.error("View opener thread caught unexpected " + t.getClass().getSimpleName(), t);
                                } finally {
                                    LOGGER.debug("Open view complete");
                                }
                            }
                        }).start();
                    }
                });
                viewMenu.add(menuItem);
            }
        }
        viewMenu.setEnabled(true);
    }

    private boolean isViewMenuItemHidden(final TabIdentifier tabId) {
        return prefs.isTabHidden(tabId.toString()); // TODO remove coupling
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
                                try {
                                    Thread.currentThread().setName("RecentOpener:" + recentDbName);
                                    Thread.currentThread().setPriority(Thread.MIN_PRIORITY + 1);
                                    LOGGER.info("Opening recent database '" + recentDbName + "'");
                                    openRecentSubmenuChoiceObservers.eventOccurred(new DatabaseNameAndPathChoice(recentDbName, recentDbPath));        
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
                    // TODO enable import / export in 0.2
//                    enableMenuItem(MenuIdentifier.FileImport, enabled);
//                    enableMenuItem(MenuIdentifier.FileExport, enabled);
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
                    buildViewMenu();
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
                    buildViewMenu();
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
                    buildViewMenu();
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
                    buildViewMenu();
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

    /**
     * {@inheritDoc}
     */
    public void rebuildViewMenu() {
        buildViewMenu();
    }

    /**
     * {@inheritDoc}
     */
    public void setTabHidden(final String tabName, final boolean tabHidden) {
        hiddenTabs.put(tabName, tabHidden);
    }
}
