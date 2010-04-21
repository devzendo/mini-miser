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

import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * The << Previous | Remove | Next >> button controls of the message queue
 * viewer.
 * 
 * @author matt
 *
 */
@SuppressWarnings("serial")
public final class DefaultMessageQueueViewerButtonsPanel extends JPanel {
    private final JButton mPreviousButton;
    private final JButton mRemoveButton;
    private final JButton mNextButton;

    /**
     * Construct the controls panel 
     */
    public DefaultMessageQueueViewerButtonsPanel() {
        setLayout(new FlowLayout());
        mPreviousButton = new JButton("<< Previous");
        add(mPreviousButton);
        mRemoveButton = new JButton("Remove");
        add(mRemoveButton);
        mNextButton = new JButton("Next >>");
        add(mNextButton);
    }

    /**
     * @param actionListener actioned by the Previous button
     */
    public void addPreviousActionListener(final ActionListener actionListener) {
        mPreviousButton.addActionListener(actionListener);
    }

    /**
     * @param actionListener actioned by the Remove button
     */
    public void addRemoveActionListener(final ActionListener actionListener) {
        mRemoveButton.addActionListener(actionListener);
    }

    /**
     * @param actionListener actioned by the Next button
     */
    public void addNextActionListener(final ActionListener actionListener) {
        mNextButton.addActionListener(actionListener);
    }

    /**
     * Set the enabled/disabled state of the buttons
     * @param enablePrevious the state of the previous button
     * @param enableRemove the state of the remove button
     * @param enableNext the state of the next button
     */
    public void setControlsEnabled(
            final boolean enablePrevious,
            final boolean enableRemove,
            final boolean enableNext) {
        mPreviousButton.setEnabled(enablePrevious);
        mRemoveButton.setEnabled(enableRemove);
        mNextButton.setEnabled(enableNext);
    }
}