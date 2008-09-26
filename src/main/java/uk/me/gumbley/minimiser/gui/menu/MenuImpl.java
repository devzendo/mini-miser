package uk.me.gumbley.minimiser.gui.menu;

import java.awt.event.ActionListener;
import javax.swing.JMenuBar;
import org.apache.log4j.Logger;
import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.commoncode.patterns.observer.Observer;
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
    private MenuState menuState;
    private MenuWiring menuWiring;
    
    private JMenuBar menuBar;
    
    private FileMenu fileMenuGroup;
    private AbstractRebuildableMenuGroup viewMenuGroup;
    private WindowMenu windowMenuGroup;
    private AbstractMenuGroup helpMenuGroup;

    
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
                    menuState.addDatabase(dbName);
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
                    menuState.clearDatabasesList();
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
                    menuState.removeDatabase(dbName);
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
                    menuState.switchToDatabase(dbName);
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
            return menuBar;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addDatabaseSwitchObserver(final Observer<DatabaseNameChoice> observer) {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                synchronized (lock) {
                    windowMenuGroup.addDatabaseSwitchObserver(observer);
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
                    menuState.setRecentDatabaseDescriptors(dbPairs);
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
                    fileMenuGroup.addOpenRecentObserver(observer);
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
            menuState.putHiddenTab(tabName, tabHidden);
        }
    }
    
}
