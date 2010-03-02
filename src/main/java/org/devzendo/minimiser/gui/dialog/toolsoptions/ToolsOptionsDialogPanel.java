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

package org.devzendo.minimiser.gui.dialog.toolsoptions;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

/**
 * The main panel of the Tools->Options dialog holds a tabbed pane, separator
 * and ok/cancel buttons.
 * 
 * @author matt
 *
 */
public final class ToolsOptionsDialogPanel extends JPanel {
    private static final long serialVersionUID = 226963124286383041L;
    private JTabbedPane tabbedPane;
    private JButton okButton;
    private JButton cancelButton;

    /**
     * Create the panel, with callbacks to the parent dialog
     * @param parentDialog the parent Tools->Options dialog
     */
    public ToolsOptionsDialogPanel(final ToolsOptionsDialog parentDialog) {
        super();
        setLayout(new BorderLayout());
        
        // Want a JTabbedPane as the main component, but sized to hold the
        // largest content.
        tabbedPane = new JTabbedPane();
        tabbedPane.setPreferredSize(new Dimension(350, 200));
        add(tabbedPane, BorderLayout.CENTER);
        
        // A separator, with the buttons under it, right-justified
        final JPanel buttonsAndSeparatorPanel = new JPanel();
        buttonsAndSeparatorPanel.setLayout(new BorderLayout());
        buttonsAndSeparatorPanel.add(new JSeparator(), BorderLayout.NORTH);
        
        final JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout());
        buttonsAndSeparatorPanel.add(buttonsPanel, BorderLayout.EAST);
        
        okButton = new JButton("OK");
        okButton.setEnabled(false);
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                parentDialog.okPressed();
            }
        });
        cancelButton = new JButton("Cancel");
        cancelButton.setEnabled(false);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                parentDialog.cancelPressed();
            }
        });
        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);
        add(buttonsAndSeparatorPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Enable the ok/cancel buttons, when all the tabs have been loaded.
     */
    public void enableButtons() {
        assert SwingUtilities.isEventDispatchThread();
        
        okButton.setEnabled(true);
        cancelButton.setEnabled(true);
    }

    /**
     * Add the named tab component to the JTabbedPane
     * @param tabName the name of the tab
     * @param tabComponent the component for this tab
     */
    public void addTab(final String tabName, final Component tabComponent) {
        assert SwingUtilities.isEventDispatchThread();
        
        tabbedPane.add(tabName, tabComponent);
    }
}
