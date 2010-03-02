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

package org.devzendo.minimiser.opener;

import java.awt.Frame;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.concurrency.ThreadUtils;
import org.devzendo.minimiser.gui.CursorManager;
import org.devzendo.minimiser.gui.MainFrameStatusBar;


/**
 * An OpenerAdapterfactory that creates OpenerAdapters that control the
 * hourglass cursor, and display progress updates on the main frame.
 * 
 * @author matt
 *
 */
public final class DefaultOpenerAdapterFactoryImpl implements OpenerAdapterFactory {
    private static final Logger LOGGER = Logger
            .getLogger(DefaultOpenerAdapterFactoryImpl.class);
    private final CursorManager cursorManager;
    private final Frame mainFrame;
    private final MainFrameStatusBar statusBar;

    /**
     * Create the factory
     * @param mainframe the main frame
     * @param cursorMgr the cursor manager
     * @param status the status bar
     */
    public DefaultOpenerAdapterFactoryImpl(final Frame mainframe, final CursorManager cursorMgr, final MainFrameStatusBar status) {
        this.mainFrame = mainframe;
        this.cursorManager = cursorMgr;
        this.statusBar = status;
    }
    
    /**
     * {@inheritDoc}
     */
    public OpenerAdapter createOpenerAdapter(final String databaseName) {
        return new MainFrameOpenerAdapter(mainFrame, databaseName, cursorManager);
    }

    private final class MainFrameOpenerAdapter extends AbstractOpenerAdapter {
        public MainFrameOpenerAdapter(final Frame frame, final String name, final CursorManager cursorMgr) {
            super(frame, name, cursorMgr);
        }

        public void reportProgress(final ProgressStage progressStage, final String description) {
            LOGGER.info("Open progress: " + progressStage + ": " + description);
            statusBar.setProgressLength(progressStage.getMaximumValue());
            if (progressStage.getValue() == progressStage.getMaximumValue()) {
                // not ideal, could use the delayed executor here...
                new Thread(new Runnable() {
                    public void run() {
                        ThreadUtils.waitNoInterruption(500);
                        statusBar.clearProgress();
                        statusBar.clearMessage();
                    }
                }).start();
            }
            statusBar.setProgressStep(progressStage.getValue());
            statusBar.displayMessage(description);
        }
    }
}
