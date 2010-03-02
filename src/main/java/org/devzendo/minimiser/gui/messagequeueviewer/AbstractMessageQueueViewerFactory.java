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

package org.devzendo.minimiser.gui.messagequeueviewer;

import java.awt.Frame;

import org.devzendo.minimiser.gui.StatusBar;
import org.devzendo.minimiser.messagequeue.MessageQueue;

/**
 * Provide framework for MessageQueueViewerFactories.
 * @author matt
 *
 */
public abstract class AbstractMessageQueueViewerFactory implements MessageQueueViewerFactory {

    private final StatusBar statusBar;
    private final Frame mainFrame;
    private final MessageQueue messageQueue;

    /**
     * Encapsulate the status bar so that viewers only need to tell their
     * factory that they have opened or closed.
     * @param bar the status bar
     * @param main the main application frame
     * @param queue the message queue
     */
    public AbstractMessageQueueViewerFactory(final StatusBar bar, final Frame main, final MessageQueue queue) {
        this.statusBar = bar;
        this.mainFrame = main;
        this.messageQueue = queue;
    }

    /**
     * Called by factory create methods when viewers are created, notify the
     * status bar that the viewer is open.
     */
    protected final void messageViewerCreated() {
        statusBar.setMessageQueueViewerShowing(true);
    }
    
    /**
     * {@inheritDoc}
     */
    public final void messageViewerClosed() {
        statusBar.setMessageQueueViewerShowing(false);
    }

    /**
     * Obtain the application's main frame, against which message viewer dialogs
     * are shown.
     * @return the main frame
     */
    public final Frame getMainFrame() {
        return mainFrame;
    }
    
    /**
     * {@inheritDoc}
     */
    public final MessageQueue getMessageQueue() {
        return messageQueue;
    }
}
