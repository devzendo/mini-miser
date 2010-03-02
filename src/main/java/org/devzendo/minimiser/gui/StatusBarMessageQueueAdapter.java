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

import org.devzendo.commoncode.patterns.observer.Observer;
import org.devzendo.minimiser.gui.messagequeueviewer.MessageQueueViewerFactory;
import org.devzendo.minimiser.messagequeue.MessageQueue;
import org.devzendo.minimiser.messagequeue.MessageQueueEvent;
import org.devzendo.minimiser.messagequeue.MessageQueueModifiedEvent;


/**
 * Adapts between the status bar and message queue. Specifically:
 * <ul>
 * <li> listens for message queue size modifications and passes this info
 * on to the status bar.
 * <li> the adapter updates the bar by listening to updates, but this wiring
 * also sets the indicator if there are any initial messages
 * <li> connects the message viewer factory to the viewer launch button
 * </ul>
 * @author matt
 *
 */
public final class StatusBarMessageQueueAdapter {

    private final StatusBar bar;
    private final MessageQueue queue;
    private final MessageQueueViewerFactory messageQueueViewerFactory;

    /**
     * Construct the adapter, and wire up.
     * @param statusBar the status bar
     * @param messageQueue the message queue
     * @param viewerFactory the message queue viewer factory
     */
    public StatusBarMessageQueueAdapter(final StatusBar statusBar, final MessageQueue messageQueue,
            final MessageQueueViewerFactory viewerFactory) {
        this.bar = statusBar;
        this.queue = messageQueue;
        this.messageQueueViewerFactory = viewerFactory;
    }

    /**
     * Wire the objects for this adapter together. This is done as a separate
     * method, not in the CTOR so that it can be controlled via lifecycles. 
     */
    public void wireAdapter() {
        queue.addMessageQueueEventObserver(new Observer<MessageQueueEvent>() {
            public void eventOccurred(final MessageQueueEvent observableEvent) {
                if (observableEvent instanceof MessageQueueModifiedEvent) {
                    final MessageQueueModifiedEvent mqme = (MessageQueueModifiedEvent) observableEvent;
                    bar.setNumberOfQueuedMessages(mqme.getNewQueueSize());
                }
            }
        });
        
        bar.setNumberOfQueuedMessages(queue.size());
        
        bar.addLaunchMessageQueueActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                messageQueueViewerFactory.createMessageQueueViewer();
            }
        });
    }
}
