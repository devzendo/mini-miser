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

import org.devzendo.minimiser.messagequeue.MessageQueue;

/**
 * MessageQueueViewerFactory objects create, and receive notification of
 * closure of, their MessageQueueViewer.
 * 
 * @author matt
 *
 */
public interface MessageQueueViewerFactory {

    /**
     * Create and show the MessageQueueViewer.
     * @return the MessageQueueViewer.
     */
    MessageQueueViewer createMessageQueueViewer();
    
    /**
     * Called by the MessageQueueViewer when it is closed by the user, so that
     * notification of this can be passed around (e.g. to the status bar, so
     * that it can enable the message queue indicator (if any messages remian))
     */
    void messageViewerClosed();

    /**
     * Called by the MessageQueueViewer when it is creating its dialog, so that
     * the dialog can be made a child of the main application frame.
     * 
     * @return the application main frame
     */
    Frame getMainFrame();
    
    /**
     * Called by the MessageQueueViewer so it can interact with the MessageQueue
     * @return the MessageQueue
     */
    MessageQueue getMessageQueue();
}
