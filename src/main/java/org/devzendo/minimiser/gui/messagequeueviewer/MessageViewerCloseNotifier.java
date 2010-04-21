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
/**
 * 
 */
package org.devzendo.minimiser.gui.messagequeueviewer;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Adapts WindowClosing events to signal to the MessageQueueViewerFactory that
 * the viewer has closed.
 * 
 * @author matt
 *
 */
public final class MessageViewerCloseNotifier extends WindowAdapter {
    private final MessageQueueViewerFactory mMessageQueueViewerFactory;
    
    /**
     * @param messageQueueViewerFactory the message queue viewer factory
     */
    public MessageViewerCloseNotifier(final MessageQueueViewerFactory messageQueueViewerFactory) {
        mMessageQueueViewerFactory = messageQueueViewerFactory;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void windowClosing(final WindowEvent e) {
        mMessageQueueViewerFactory.messageViewerClosed();
    }
}