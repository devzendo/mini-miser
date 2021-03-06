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

package org.devzendo.minimiser.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;
import org.devzendo.commonapp.gui.CursorManager;
import org.devzendo.commonapp.gui.MainFrameFactory;
import org.devzendo.commonapp.gui.WindowGeometryStore;
import org.devzendo.commonapp.spring.springloader.SpringLoader;
import org.devzendo.commoncode.exception.AppException;
import org.devzendo.commoncode.resource.ResourceLoader;
import org.devzendo.minimiser.gui.tabpanemanager.TabPaneManager;


/**
 * The MiniMiser main Frame - this is the main application start code.
 * 
 * @author matt
 */
public class MainFrame {
    private static final Logger LOGGER = Logger.getLogger(MainFrame.class);
    private static final String MAIN_FRAME_NAME = "main";

    private JFrame mMainFrame;
    private final SpringLoader mSpringLoader;
    private final CursorManager mCursorManager;
    private final WindowGeometryStore mWindowGeometryStore;
    private final MainFrameStatusBar mStatusBar;

    /**
     * @param springLoader the IoC container abstraction
     * @param argList the command line arguments after logging has been processed
     * @throws AppException upon fatal application failure
     */
    public MainFrame(final SpringLoader springLoader, 
            final List<String> argList)
            throws AppException {
        super();
        mSpringLoader = springLoader;
        mWindowGeometryStore = mSpringLoader.getBean("windowGeometryStore", WindowGeometryStore.class);

        // Create new Window and exit handler
        createMainFrame();
        mCursorManager = mSpringLoader.getBean("cursorManager", CursorManager.class);
        mCursorManager.setMainFrame(mMainFrame);
        
        mStatusBar = mSpringLoader.getBean("statusBar", MainFrameStatusBar.class);
        mMainFrame.add(mStatusBar.getPanel(), BorderLayout.SOUTH);
        
        // Menu
        //mainFrame.setJMenuBar(createMenu());
        
        // Main panel
        final TabPaneManager tabPaneManager = mSpringLoader.getBean("tabPaneManager", TabPaneManager.class);
        final JPanel mainPanel = tabPaneManager.getMainPanel();
        mainPanel.setPreferredSize(new Dimension(640, 480));
        mMainFrame.add(mainPanel, BorderLayout.CENTER);
        
        if (!mWindowGeometryStore.hasStoredGeometry(mMainFrame)) {
            mMainFrame.pack();
        }
        mWindowGeometryStore.loadGeometry(mMainFrame);
        attachVisibilityListenerForLifecycleManagerStartup();
        mMainFrame.setVisible(true);
    }

    private void attachVisibilityListenerForLifecycleManagerStartup() {
        Toolkit.getDefaultToolkit().addAWTEventListener(
            new LifecycleStartupAWTEventListener(mSpringLoader),
            AWTEvent.WINDOW_EVENT_MASK);
    }

    private void createMainFrame() {
        mMainFrame = new JFrame();

        mMainFrame.setIconImage(ResourceLoader.createResourceImageIcon("icons/application.gif").getImage());
        mMainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setMainFrameInFactory();
        
        createAndSetMainFrameTitleInFactory();

        mMainFrame.setName(MAIN_FRAME_NAME);
        mMainFrame.setLayout(new BorderLayout());
    }

    private void createAndSetMainFrameTitleInFactory() {
        final MainFrameTitleFactory mainFrameTitleFactory = mSpringLoader.getBean("&mainFrameTitle", MainFrameTitleFactory.class);
        mainFrameTitleFactory.setMainFrameTitle(
            new DefaultMainFrameTitleImpl(mMainFrame));
    }

    private void setMainFrameInFactory() {
        final MainFrameFactory mainFrameFactory = mSpringLoader.getBean("&mainFrame", MainFrameFactory.class);
        mainFrameFactory.setMainFrame(mMainFrame);
    }
}
