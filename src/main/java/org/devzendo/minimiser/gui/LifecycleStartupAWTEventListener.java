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
import java.awt.event.AWTEventListener;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.devzendo.commonapp.gui.CursorManager;
import org.devzendo.commonapp.gui.SwingWorker;
import org.devzendo.commonapp.spring.springloader.SpringLoader;
import org.devzendo.minimiser.lifecycle.LifecycleManager;


/**
 * The LifecycleStartupAWTEventListener is attached as a listener
 * to the main JFrame, and listens for it becoming visible. At
 * this point, it triggers the Lifecycle startup on a separate
 * thread, surrounding this with an hourglass cursor.
 * 
 * @author matt
 *
 */
public final class LifecycleStartupAWTEventListener implements AWTEventListener {
    private static final Logger LOGGER = Logger
            .getLogger(LifecycleStartupAWTEventListener.class);
    private final SpringLoader mSpringLoader;
    private final JFrame mMainFrame;
    
    /**
     * Construct the window visible listener that triggers the
     * lifecycle startup.
     * @param springLoader the SpringLoader
     */
    public LifecycleStartupAWTEventListener(final SpringLoader springLoader) {
        mSpringLoader = springLoader;
        mMainFrame = mSpringLoader.getBean("mainFrame", JFrame.class);
    }
    
    /**
     * {@inheritDoc}
     */
    public void eventDispatched(final AWTEvent event) {
        if (event.getID() == WindowEvent.WINDOW_OPENED && event.getSource().equals(mMainFrame)) {
            LOGGER.info("Main frame visible; getting lifecycle dependencies");
            final CursorManager mCursorManager = mSpringLoader.getBean("cursorManager", CursorManager.class);
            mCursorManager.hourglass(this.getClass().getSimpleName());
            final SwingWorker worker = new SwingWorker() {
                @Override
                public Object construct() {
                    Thread.currentThread().setName("Lifecycle Startup");
                    LOGGER.info("Main frame visible; starting lifecycle manager");
                    final LifecycleManager mLifecycleManager = mSpringLoader.getBean("lifecycleManager", LifecycleManager.class);
                    mLifecycleManager.startup();
                    return null;
                }
                @Override
                public void finished() {
                    mCursorManager.normal(this.getClass().getSimpleName());
                    LOGGER.info("Main frame visible; lifecycle manager finished");
                }
            };
            worker.start();
        }
    }
}
