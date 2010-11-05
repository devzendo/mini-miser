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

package org.devzendo.minimiser.wiring.lifecycle;

import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import org.apache.log4j.Logger;
import org.devzendo.commongui.GUIUtils;
import org.devzendo.commonspring.springloader.SpringLoader;
import org.devzendo.minimiser.gui.menu.Menu;
import org.devzendo.minimiser.gui.menu.MenuBuilder;
import org.devzendo.minimiser.gui.menu.MenuMediator;
import org.devzendo.minimiser.gui.menu.SystemMenuIdentifiers;
import org.devzendo.minimiser.lifecycle.Lifecycle;
import org.devzendo.minimiser.recentlist.RecentFilesList;


/**
 * A Lifecycle that intiialises the menu and attaches it to the
 * main frame.
 *
 * Non-TDD.
 *
 * @author matt
 *
 */
public final class MenuInitialisingLifecycle implements Lifecycle {
    private static final Logger LOGGER = Logger
            .getLogger(MenuInitialisingLifecycle.class);
    private final SpringLoader mSpringLoader;

    /**
     * Construct
     * @param springLoader the spring loader
     */
    public MenuInitialisingLifecycle(final SpringLoader springLoader) {
        mSpringLoader = springLoader;
    }

    /**
     * {@inheritDoc}
     */
    public void shutdown() {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void startup() {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                LOGGER.info("Starting up menu initialising lifecycle");
                final JFrame mainFrame = mSpringLoader.getBean("mainFrame", JFrame.class);
                mainFrame.setJMenuBar(createMenu());
                LOGGER.info("End of startup for menu initialising lifecycle");
            }
        });
    }

    private JMenuBar createMenu() {
        LOGGER.info("Getting the menu");
        final Menu menu = mSpringLoader.getBean("menu", Menu.class);
        menu.initialise();
        final RecentFilesList recentList = mSpringLoader.getBean("recentFilesList", RecentFilesList.class);
        menu.refreshRecentList(recentList.getRecentDatabases());
        // TODO: SMELL why not have it populated correctly at first?
        // we could pass in the recent strings to a ctor?
        // NOTE the view menu is initially populated via a lifecycle
        LOGGER.info("Got the menu");
        final ActionListener actionListener = mSpringLoader.getBean("mainFrameCloseActionListener", ActionListener.class);
        menu.addMenuActionListener(SystemMenuIdentifiers.FILE_EXIT, actionListener);
        // wire up dependencies
        LOGGER.info("Wiring menu dependencies and adapters");
        mSpringLoader.getBean("menuMediator", MenuMediator.class);
        LOGGER.info("Menu dependencies wired");
        mSpringLoader.getBean("menuBuilder", MenuBuilder.class).build();
        LOGGER.info("Menu ActionListeners built and wired");
        return menu.getMenuBar();
    }
}
