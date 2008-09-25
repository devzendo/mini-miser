package uk.me.gumbley.minimiser.gui.menu;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
import uk.me.gumbley.commoncode.patterns.observer.ObserverList;
import uk.me.gumbley.minimiser.openlist.DatabaseDescriptor;

/**
 * The Swing Menu.
 * TODO before moving menus out, class data abstraction coupling is 14; fan-out
 * complexity is 22 
 * @author matt
 *
 */
public final class MenuImpl implements Menu {
    private static final Logger LOGGER = Logger.getLogger(MenuImpl.class);
    private Object lock = new Object();
    private MenuState menuState;
    
    private List<String> databases;
    private int currentDatabaseIndex;
    private DatabaseDescriptor[] recentDatabaseDescriptors;

    private JMenuBar menuBar;
    
    private FileMenu fileMenuGroup;
    private AbstractRebuildableMenuGroup viewMenuGroup;
    private AbstractRebuildableMenuGroup windowMenuGroup;
    private AbstractMenuGroup helpMenuGroup;

    private ObserverList<DatabaseNameChoice> windowMenuChoiceObservers;
    private ObserverList<DatabaseNameAndPathChoice> openRecentSubmenuChoiceObservers;
    private MenuWiring menuWiring;
    private Map<String, Boolean> hiddenTabs;

    /**
     * Create the Menu
     * @param wiring the MenuWiring singleton
     */
    public MenuImpl(final MenuWiring wiring) {
        GUIUtils.runOnEventThread(new Runnable() {

            public void run() {
                synchronized (lock) {
                    menuState = new MenuState();
                    menuWiring = wiring;
                    databases = new ArrayList<String>();
                    currentDatabaseIndex = -1;
                    recentDatabaseDescriptors = new DatabaseDescriptor[0];
            
                    windowMenuChoiceObservers = new ObserverList<DatabaseNameChoice>();
                    openRecentSubmenuChoiceObservers = new ObserverList<DatabaseNameAndPathChoice>();

                    hiddenTabs = new HashMap<String, Boolean>();
        
                    // The menu bar
                    menuBar = new JMenuBar();
                    
                    // The File menu - builds itself on construction
                    fileMenuGroup = new FileMenu(MenuImpl.this.menuWiring, MenuImpl.this.menuState, MenuImpl.this);
                    menuBar.add(fileMenuGroup.getJMenu());
                    
                    // The View menu
                    viewMenuGroup = new ViewMenu(MenuImpl.this.menuWiring, MenuImpl.this.menuState, MenuImpl.this);
                    viewMenuGroup.rebuildMenuGroup();
                    menuBar.add(viewMenuGroup.getJMenu());
                    
                    // The Window menu
                    windowMenuGroup = new WindowMenu(MenuImpl.this.menuWiring, MenuImpl.this.menuState, MenuImpl.this);
                    menuBar.add(windowMenuGroup.getJMenu());
                    
                    // The help menu
                    helpMenuGroup = new HelpMenu(MenuImpl.this.menuWiring, MenuImpl.this.menuState, MenuImpl.this);
                    menuBar.add(helpMenuGroup.getJMenu());
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void enableCloseMenu(final boolean enabled) {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                synchronized (lock) {
                    fileMenuGroup.enableCloseMenu(enabled);
                }
            }
        });
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
                    viewMenuGroup.rebuildMenuGroup();
                    windowMenuGroup.rebuildMenuGroup();
                    fileMenuGroup.enableCloseAllMenuIfDatabasesOpen();
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
                    viewMenuGroup.rebuildMenuGroup();
                    windowMenuGroup.rebuildMenuGroup();
                    fileMenuGroup.enableCloseMenu(false);
                    fileMenuGroup.enableCloseAllMenuIfDatabasesOpen();
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
                    viewMenuGroup.rebuildMenuGroup();
                    windowMenuGroup.rebuildMenuGroup();
                    fileMenuGroup.enableCloseAllMenuIfDatabasesOpen();
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
                    viewMenuGroup.rebuildMenuGroup();
                    windowMenuGroup.rebuildMenuGroup();
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
                    fileMenuGroup.rebuildMenuGroup();
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
        GUIUtils.invokeLaterOnEventThread(new Runnable() {
            public void run() {
                synchronized (lock) {
                    LOGGER.debug("Rebuilding view menu");
                    viewMenuGroup.rebuildMenuGroup();
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void setTabHidden(final String tabName, final boolean tabHidden) {
        LOGGER.debug("Tab " + tabName + " is " + (tabHidden ? "" : "not ") + "hidden");
        synchronized (lock) {
            hiddenTabs.put(tabName, tabHidden);
        }
    }
    
    // Default visibility routines for the *Menu classes to use ----------------

    /**
     * Create a new JMenuItem, and wire it into the MenuWiring.
     * Called on the EDT
     * TODO move into MenuWiring WOZERE 
     * @param menuIdentifier the MenuIdentifier
     * @param menuItemText the text for this menu item
     * @param mnemonic the mnemonic
     * @param menu the menu to add it to.
     */
    void createMenuItem(final MenuIdentifier menuIdentifier,
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
     * How manu open databases are there?
     * @return the number of open databases
     */
    int getNumberOfDatabases() {
        return databases.size();
    }

    /**
     * Get a database name
     * @param index index into the databases list
     * @return the database name
     */
    String getDatabase(final int index) {
        return databases.get(index);
    }

    /**
     * Get the index of the currently selected database
     * @return the current database index 
     */
    int getCurrentDatabaseIndex() {
        return currentDatabaseIndex;
    }

    /**
     * Get the number of recent database descriptors
     * @return the number of recent database descriptors
     */
    int getNumberOfRecentDatabaseDescriptors() {
        return recentDatabaseDescriptors.length;
    }

    /**
     * Get the details of a specific recent database descriptor
     * @param index an index into the list
     * @return the path and choice
     */
    DatabaseDescriptor getRecentDatabaseDescriptor(final int index) {
        return recentDatabaseDescriptors[index];
    }

    /**
     * Dispatch a choice from the open recent menu to observers
     * @param choice the choice
     */
    void dispatchToRecentSubmenuChoiceObservers(final DatabaseNameAndPathChoice choice) {
        openRecentSubmenuChoiceObservers.eventOccurred(choice);
    }
    
    /**
     * Is an item on the view menu hidden?
     * @param tabName the name of the tab
     * @return true off hidden
     */
    boolean isViewMenuItemHidden(final String tabName) {
        final Boolean hidden = hiddenTabs.get(tabName);
        return hidden != null && hidden;
    }

    /**
     * Dispatch a window choice to observers
     * @param choice the choice from the window menu
     */
    void dispatchToWindowMenuChoiceObservers(final DatabaseNameChoice choice) {
        windowMenuChoiceObservers.eventOccurred(choice);
    }
}
