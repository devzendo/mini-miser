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

import javax.swing.SwingUtilities;

import org.devzendo.minimiser.gui.CursorManager;
import org.devzendo.minimiser.gui.StatusBar;
import org.devzendo.minimiser.messagequeue.MessageQueue;
import org.devzendo.minimiser.pluginmanager.PluginRegistry;


/**
 * The factory for Swing-based MessageQueueViewers
 * @author matt
 *
 */
public final class DefaultMessageQueueViewerFactory extends 
        AbstractMessageQueueViewerFactory {
    private final CursorManager mCursorManager;
    private final PluginRegistry mPluginRegistry;

    /**
     * Pass the status bar on to the abstract base class for factories
     * @param bar the status bar
     * @param main the main application frame
     * @param queue the message queue
     * @param cursor the cursor manager
     * @param pluginRegistry the plugin registry
     */
    public DefaultMessageQueueViewerFactory(final StatusBar bar,
            final Frame main,
            final MessageQueue queue,
            final CursorManager cursor,
            final PluginRegistry pluginRegistry) {
        super(bar, main, queue);
        mCursorManager = cursor;
        mPluginRegistry = pluginRegistry;
    }

    /**
     * {@inheritDoc}
     */
    public MessageQueueViewer createMessageQueueViewer() {
        assert SwingUtilities.isEventDispatchThread();
        
        messageViewerCreated();
        mCursorManager.hourglass(this.getClass().getSimpleName());
        // TODO: inject MessageRendererFactory
        final DefaultMessageQueueViewer messageQueueViewer =
            new DefaultMessageQueueViewer(this, 
                new MessageRendererFactory(),
                mPluginRegistry.getApplicationName());
        mCursorManager.normal(this.getClass().getSimpleName());
        return messageQueueViewer;
    }
}
