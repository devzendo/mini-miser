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

import org.devzendo.minimiser.util.DelayedExecutor;

/**
 * A StatusBar that has no GUI, but can be probed via tests.
 * @author matt
 *
 */
public final class HeadlessStatusBar extends AbstractStatusBar {
    private String displayedMessage = "";
    private ActionListener launchMessageViewerActionListener;
    
    /**
     * Construct with no head.
     * @param exec the Delayed Executor
     */
    public HeadlessStatusBar(final DelayedExecutor exec) {
        super(exec);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void internalSetMessageTextNow(final String message) {
        displayedMessage = message;
    }

    /**
     * {@inheritDoc}
     */
    public void clearProgress() {
    }

    /**
     * {@inheritDoc}
     */
    public void setEncryptedIndicator(final boolean encrypted) {
    }

    /**
     * {@inheritDoc}
     */
    public void setProgressLength(final int max) {
    }

    /**
     * {@inheritDoc}
     */
    public void setProgressStep(final int step) {
    }

    /**
     * For tests, what's currently being displayed?
     * @return the string
     */
    public String getDisplayedMessage() {
        return displayedMessage;
    }

    /**
     * Is the message queue indicator enabled?
     * @return true iff there are queued messages
     */
    public boolean isMessageQueueIndicatorEnabled() {
        return getNumberOfQueuedMessages() > 0 && !isMessageQueueViewerShowing();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addLaunchMessageQueueActionListener(final ActionListener listener) {
        launchMessageViewerActionListener = listener;
    }
    
    /**
     * Allow tests to press the message queue indicator button
     */
    public void internalTriggerLaunchMessageQueueActionListener() {
        launchMessageViewerActionListener.actionPerformed(new ActionEvent(this, 0, "click"));
    }
    
    /**
     * Allows tests to determine whether the message queue viewer launcher
     * has been set 
     * @return true if something set
     */
    public boolean isLaunchMessageQueueActionListenerSet() {
        return launchMessageViewerActionListener != null;
    }
}
