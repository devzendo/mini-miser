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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.devzendo.commonapp.gui.CursorManager;
import org.devzendo.commonapp.gui.SwingWorker;
import org.devzendo.commonapp.spring.springloader.SpringLoader;
import org.devzendo.minimiser.lifecycle.LifecycleManager;
import org.devzendo.minimiser.openlist.OpenDatabaseList;


/**
 * Triggered when the main window is to close, this prompts for
 * confirmation if any databases are open, and if OK, triggers
 * a lifecycle shutdown surrounded by an hourglass cursor, then
 * exits the application.
 * 
 * @author matt
 *
 */
public class MainFrameCloseActionListener implements ActionListener {
    private static final Logger LOGGER = Logger
            .getLogger(MainFrameCloseActionListener.class);
    private final SpringLoader mSpringLoader;
    private final OpenDatabaseList mOpenDatabaseList;
    private final CursorManager mCursorManager;
    private final LifecycleManager mLifecycleManager;
    private final JFrame mMainFrame;

    /**
     * Construct the ActionListener that will prompt for
     * confirmation if any databases are open, and trigger a
     * lifecycle shutdown.
     * @param springLoader the SpringLoader
     */
    public MainFrameCloseActionListener(final SpringLoader springLoader) {
        mSpringLoader = springLoader;
        mOpenDatabaseList = mSpringLoader.getBean("openDatabaseList", OpenDatabaseList.class);
        mCursorManager = mSpringLoader.getBean("cursorManager", CursorManager.class);
        mLifecycleManager = mSpringLoader.getBean("lifecycleManager", LifecycleManager.class);
        mMainFrame = mSpringLoader.getBean("mainFrame", JFrame.class);
    }
    
    /**
     * Note that e can be null when called from the adapted WindowListener.
     * {@inheritDoc}
     * EDT
     */
    public void actionPerformed(final ActionEvent e) {
        // only ask if there are databases open
        final boolean anyDatabasesOpen = mOpenDatabaseList.getNumberOfDatabases() > 0;
        if (anyDatabasesOpen) {
            if (askExitSaysYes()) {
                doShutdown();
            }
        } else {
            doShutdown();
        }
    }

    // EDT
    private boolean askExitSaysYes() {
        final int opt = JOptionPane.showConfirmDialog(mMainFrame,
            "Are you sure you want to exit?", "Confirm exit",
            JOptionPane.YES_NO_OPTION);
        return (opt == 0);
    }
    
    // EDT
    private void doShutdown() {
        mCursorManager.hourglass(this.getClass().getSimpleName());
        
        final SwingWorker worker = new SwingWorker() {
            @Override
            public Object construct() {
                Thread.currentThread().setName("Lifecycle Shutdown");
                LOGGER.info("Shutting down...");
                if (mLifecycleManager != null) {
                    mLifecycleManager.shutdown();
                }
                return null;
            }
            
            @Override
            public void finished() {
                if (mMainFrame != null) {
                    mMainFrame.dispose();
                }
                mCursorManager.normal(this.getClass().getSimpleName());
                System.exit(0);
            }
        };
        worker.start();
    }
}
