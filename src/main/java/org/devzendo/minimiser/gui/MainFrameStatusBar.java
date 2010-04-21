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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;
import org.devzendo.commoncode.gui.GUIUtils;
import org.devzendo.minimiser.util.DelayedExecutor;
import org.devzendo.minimiser.util.Sleeper;


/**
 * A StatusBar on the bottom of the Main Frame
 * 
 * @author matt
 */
public final class MainFrameStatusBar extends AbstractStatusBar {
    private static final Logger LOGGER = Logger
            .getLogger(MainFrameStatusBar.class);
    private JProgressBar progressBar;

    private JLabel label;

    private JPanel statusBarPanel;
    private MessagesButton messageQueueButton;

    /**
     * @param exec the DelayedExecutor
     */
    public MainFrameStatusBar(final DelayedExecutor exec) {
        super(exec);
        statusBarPanel = new JPanel();
        statusBarPanel.setLayout(new BorderLayout());
        statusBarPanel.setBorder(BorderFactory.createEtchedBorder());
        
        final JPanel progressBarPanel = new JPanel();
        progressBar = new JProgressBar(SwingConstants.HORIZONTAL);
        progressBar.setPreferredSize(new Dimension(150, 16));
        progressBarPanel.setLayout(new FlowLayout());
        progressBarPanel.add(progressBar);
        label = new JLabel(" ");
        progressBarPanel.add(label);
        statusBarPanel.add(progressBarPanel, BorderLayout.WEST);
        
        messageQueueButton = new MessagesButton(new Sleeper()); // TODO: inject this
        messageQueueButton.setPreferredSize(new Dimension(150, 16));
        messageQueueButton.setVisible(false);
        statusBarPanel.add(messageQueueButton, BorderLayout.EAST);
    }

    /**
     * @return the panel for adding to the GUI
     */
    public JPanel getPanel() {
        return statusBarPanel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void internalSetMessageTextNow(final String message) {
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                label.setText(message);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void clearProgress() {
        LOGGER.debug("clearing progress");
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                progressBar.setMaximum(0);
                progressBar.setMinimum(0);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void setEncryptedIndicator(final boolean encrypted) {
        // TODO: Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    public void setProgressLength(final int max) {
        LOGGER.debug("progress length is " + max);
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                progressBar.setMaximum(max);
                progressBar.setMinimum(0);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void setProgressStep(final int step) {
        LOGGER.debug("progress step is " + step);
        GUIUtils.runOnEventThread(new Runnable() {
            public void run() {
                progressBar.setValue(step);
            }
        });
    }
    
    /**
     * Sets the number of queued messages
     * @param number the number of queued messages
     */
    public void setNumberOfQueuedMessages(final int number) {
        super.setNumberOfQueuedMessages(number);
        messageQueueButton.setNumberOfMessages(number);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setMessageQueueViewerShowing(final boolean showing) {
        super.setMessageQueueViewerShowing(showing);
        messageQueueButton.setMessageQueueViewerShowing(showing);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addLaunchMessageQueueActionListener(final ActionListener listener) {
        messageQueueButton.addActionListener(listener);
        
    }
}
