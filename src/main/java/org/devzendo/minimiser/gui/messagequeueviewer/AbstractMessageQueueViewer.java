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

/**
 * Provide framework for MessageQueueViewers.
 * 
 * @author matt
 *
 */
public abstract class AbstractMessageQueueViewer implements MessageQueueViewer {
    private final MessageQueueViewerFactory messageQueueViewerFactory;
    
    /**
     * Create an AbstractMessageQueueViewer given its factory. The Viewer knows
     * of its factory, so it can inform the factory that it has opened or
     * closed. The factory encapsulates the informing of the other objects
     * that need to know, notably the status bar.
     * @param factory the factory that created this viewer
     */
    public AbstractMessageQueueViewer(final MessageQueueViewerFactory factory) {
        this.messageQueueViewerFactory = factory;
    }
    
    /**
     * @return this viewer's factory
     */
    protected final MessageQueueViewerFactory getMessageQueueViewerFactory() {
        return messageQueueViewerFactory;
    }
}
