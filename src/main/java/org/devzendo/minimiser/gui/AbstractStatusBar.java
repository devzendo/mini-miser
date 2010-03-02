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

import java.awt.event.ActionListener;

import org.devzendo.minimiser.util.DelayedExecutor;

/**
 * The logic of some parts of the status bar (those parts that can be done TDD),
 * handling the message.
 * 
 * @author matt
 *
 */
public abstract class AbstractStatusBar implements StatusBar {
    private Object lock;
    private String permanentMessage;
    private String tempMessage;
    private DelayedExecutor delayedExecutor;
    private int numberOfQueuedMessages;
    private boolean isMessageQueueViewerShowing;

    /**
     * Abstract base class constructor 
     * @param exec the Delayed Executor
     */
    public AbstractStatusBar(final DelayedExecutor exec) {
        delayedExecutor = exec;
        lock = new Object();
        numberOfQueuedMessages = 0;
        synchronized (lock) {
            tempMessage = "";
            permanentMessage = "";
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public final void clearMessage() {
        synchronized (lock) {
            permanentMessage = "";
        }
        update();
    }

    /**
     * {@inheritDoc}
     */
    public final void displayMessage(final String message) {
        synchronized (lock) {
            permanentMessage = message == null ? "" : message;
        }
        update();
    }

    /**
     * {@inheritDoc}
     */
    public final void displayTemporaryMessage(final String message, final int seconds) {
        synchronized (lock) {
            tempMessage = message;
        }
        update();
        delayedExecutor.submit("AbstractStatusBar", seconds * 1000L, new Runnable() {
            public void run() {
                synchronized (lock) {
                    tempMessage = "";
                }
                update();
            }
        });
    }
    
    /**
     * Used by the message timing system to set some text now in the underlying
     * GUI.
     * @param message the text, or "" to clear.
     */
    public abstract void internalSetMessageTextNow(String message);
    
    /**
     * What's the currently-displayed permanent or temporary message?
     * @return the current message.
     */
    private String getDisplayMessage() {
        synchronized (lock) {
            return tempMessage.equals("") ? permanentMessage : tempMessage;
        }
    }
    
    /**
     * Update the display
     */
    private void update() {
        internalSetMessageTextNow(getDisplayMessage());
    }

    /**
     * Obtain the number of queued messages
     * @return the number of queued messages
     */
    public final int getNumberOfQueuedMessages() {
        return numberOfQueuedMessages;
    }

    /**
     * Sets the number of queued messages
     * @param number the number of queued messages
     */
    public void setNumberOfQueuedMessages(final int number) {
        numberOfQueuedMessages = number;
    }

    /**
     * {@inheritDoc}
     */
    public void setMessageQueueViewerShowing(final boolean showing) {
        isMessageQueueViewerShowing = showing;
    }

    /**
     * Is the message queue viewer showing?
     * @return true iff showing
     */
    public final boolean isMessageQueueViewerShowing() {
        return isMessageQueueViewerShowing;
    }

    /**
     * {@inheritDoc}
     */
    public abstract void addLaunchMessageQueueActionListener(final ActionListener listener);
}
