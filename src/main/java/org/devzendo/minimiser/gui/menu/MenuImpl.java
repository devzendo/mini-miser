/**
 * Copyright (C) 2008-2010 Matt Gumbley, DevZendo.org <http://devzendo.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devzendo.minimiser.gui.menu;

import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import org.apache.log4j.Logger;
import org.devzendo.commonapp.gui.GUIUtils;
import org.devzendo.commonapp.gui.menu.AbstractMenuGroup;
import org.devzendo.commonapp.gui.menu.MenuIdentifier;
import org.devzendo.commonapp.gui.menu.MenuWiring;
import org.devzendo.commonapp.spring.springloader.SpringLoader;
import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;


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
    private ApplicationMenuCombiner mApplicationMenuCombiner;

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
     * @param applicationMenuCombiner the ApplicationMenuCombiner singleton
     */
    public MenuImpl(final SpringLoader loader, final MenuWiring wiring, final ApplicationMenuCombiner applicationMenuCombiner) {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                synchronized (lock) {
                    LOGGER.info("Constructing");
                    springLoader = loader;
                    menuWiring = wiring;
                    mApplicationMenuCombiner = applicationMenuCombiner;
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

                    final List<JMenu> customMenus = mApplicationMenuCombiner.combineMenus().getCustomMenus();
                    for (final JMenu jMenu : customMenus) {
                        menuBar.add(jMenu);
                    }

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
     * TODO: this can be removed
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
