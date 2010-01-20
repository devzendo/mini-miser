package org.devzendo.minimiser.gui.menu;

import java.awt.event.ActionListener;

import javax.swing.JMenuBar;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.springloader.SpringLoader;

import uk.me.gumbley.commoncode.gui.GUIUtils;
import uk.me.gumbley.commoncode.patterns.observer.Observer;

/**
 * The Swing Menu.
 *
 * @author matt
 *
 */
public final class MenuImpl implements Menu {
    private static final Logger LOGGER = Logger.getLogger(MenuImpl.class);
    private final Object lock = new Object();
    private SpringLoader springLoader;
    private MenuWiring menuWiring;

    private JMenuBar menuBar;

    private FileMenu fileMenuGroup;
    private ViewMenu viewMenuGroup;
    private AbstractMenuGroup toolsMenuGroup;
    private WindowMenu windowMenuGroup;
    private HelpMenu helpMenuGroup;


    /**
     * Create the Menu
     * @param loader the SpringLoader singleton
     * @param wiring the MenuWiring singleton
     */
    public MenuImpl(final SpringLoader loader, final MenuWiring wiring) {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                synchronized (lock) {
                    LOGGER.info("Constructing");
                    springLoader = loader;
                    menuWiring = wiring;
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void initialise() {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                synchronized (lock) {
                    LOGGER.info("Initialising the menu bar and groups");
                    // The menu bar
                    menuBar = new JMenuBar();
                    // The File menu - builds itself on construction
                    fileMenuGroup = springLoader.getBean("fileMenu", FileMenu.class);

                    // The View menu
                    viewMenuGroup = springLoader.getBean("viewMenu", ViewMenu.class);

                    // The Tools menu
                    toolsMenuGroup = springLoader.getBean("toolsMenu", ToolsMenu.class);

                    // The Window menu
                    windowMenuGroup = springLoader.getBean("windowMenu", WindowMenu.class);

                    // The help menu
                    helpMenuGroup = springLoader.getBean("helpMenu", HelpMenu.class);
                    LOGGER.info("Menu bar and groups initialised");

                    rebuildEntireMenu();
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void rebuildEntireMenu() {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                synchronized (lock) {
                    LOGGER.info("Rebuilding the menu bar and groups");
                    menuBar.removeAll();

                    fileMenuGroup.rebuildMenuGroup();
                    menuBar.add(fileMenuGroup.getJMenu());

                    viewMenuGroup.rebuildMenuGroup();
                    menuBar.add(viewMenuGroup.getJMenu());

                    menuBar.add(toolsMenuGroup.getJMenu());

                    menuBar.add(windowMenuGroup.getJMenu());

                    helpMenuGroup.rebuildMenuGroup();
                    menuBar.add(helpMenuGroup.getJMenu());
                    LOGGER.info("Menu bar and groups rebuilt");
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
            LOGGER.info("Obtaining the menu bar");
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
    public void rebuildFileMenu() {
        GUIUtils.invokeLaterOnEventThread(new Runnable() {
            public void run() {
                synchronized (lock) {
                    LOGGER.debug("Rebuilding file menu");
                    fileMenuGroup.rebuildMenuGroup();
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     * TODO this can be removed
     */
    public void setTabHidden(final String tabName, final boolean tabHidden) {
        LOGGER.debug("Tab " + tabName + " is " + (tabHidden ? "" : "not ") + "hidden");
    }

    /**
     * {@inheritDoc}
     */
    public void addViewChoiceObserver(final Observer<ViewMenuChoice> observer) {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                synchronized (lock) {
                    viewMenuGroup.addViewObserver(observer);
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public boolean isHelpCheckForUpdatesEnabled() {
        return helpMenuGroup.isHelpCheckForUpdatesEnabled();
    }

    /**
     * {@inheritDoc}
     */
    public void setHelpCheckForUpdatesEnabled(final boolean newEnabled) {
        helpMenuGroup.setHelpCheckForUpdatesEnabled(newEnabled);
    }

    /**
     * {@inheritDoc}
     */
    public void rebuildHelpMenu(final String applicationName) {
        GUIUtils.invokeLaterOnEventThread(new Runnable() {
            public void run() {
                synchronized (lock) {
                    helpMenuGroup.setApplicationName(applicationName);
                    helpMenuGroup.rebuildMenuGroup();
                }
            }
        });
    }
}
