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

import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.devzendo.commonapp.gui.GUIUtils;


/**
 * Main Frame Title controller.
 * 
 * @author matt
 *
 */
public final class DefaultMainFrameTitleImpl implements MainFrameTitle {
    private static final Logger LOGGER = Logger
            .getLogger(DefaultMainFrameTitleImpl.class);
    private String mDatabaseName;
    private String mApplicationName;
    private final JFrame mMainFrame;
    private final Object mLock;
    
    /**
     * Create the Main Frame Title controller, updating the main frame.
     * @param mainframe the main application frame
     */
    public DefaultMainFrameTitleImpl(final JFrame mainframe) {
        LOGGER.debug("Starting Main Frame Title");
        mMainFrame = mainframe;
        mLock = new Object();
        synchronized (mLock) {
            mDatabaseName = null;
            mApplicationName = null;
        }
        update();
    }

    /**
     * {@inheritDoc}
     */
    public void clearCurrentDatabaseName() {
        synchronized (mLock) {
            mDatabaseName = null;
        }
        update();
    }

    /**
     * {@inheritDoc}
     */
    public String getCurrentDatabaseName() {
        synchronized (mLock) {
            return mDatabaseName;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setCurrentDatabaseName(final String databasename) {
        synchronized (mLock) {
            mDatabaseName = databasename;
        }
        update();
    }

    private void update() {
        GUIUtils.invokeLaterOnEventThread(new Runnable() {
            public void run() {
                final StringBuilder title = new StringBuilder();
                synchronized (mLock) {
                    if (mApplicationName != null) {
                        title.append(mApplicationName);
                    }
                    if (mApplicationName != null && mDatabaseName != null) {
                        title.append(" - ");
                    }
                    if (mDatabaseName != null) {
                        title.append(mDatabaseName);
                    }
                }
                mMainFrame.setTitle(title.toString());
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationName(final String applicationName) {
        synchronized (mLock) {
            mApplicationName = applicationName;
        }
        update();
    }
}
