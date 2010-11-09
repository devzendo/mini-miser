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

package org.devzendo.minimiser.gui.dialog.toolsoptions.tabs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.devzendo.minimiser.gui.dialog.toolsoptions.ToolsOptionsTab;
import org.devzendo.minimiser.gui.tab.SystemTabIdentifiers;
import org.devzendo.minimiser.prefs.MiniMiserPrefs;

/**
 * The Advanced Tools->Options tab holds settings suitable for gurus.
 *
 * @author matt
 *
 */
public final class AdvancedTab implements ToolsOptionsTab {
    private MiniMiserPrefs changeCollectingPrefs;
    private JPanel mainPanel;
    private final Object lock;

    /**
     * Create the Advanced Tab
     * @param collectingPrefs the Prefs that will collect changes until
     * committed.
     */
    public AdvancedTab(final MiniMiserPrefs collectingPrefs) {
        lock = new Object();
        synchronized (lock) {
            this.changeCollectingPrefs = collectingPrefs;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "Advanced";
    }

    /**
     * {@inheritDoc}
     */
    public void initComponent() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        final JPanel innerPanel = new JPanel(new GridLayout(4, 1, 16, 16));

        final JCheckBox sqlViewEnabled = new JCheckBox("SQL view enabled?");
        synchronized (lock) {
            sqlViewEnabled.setSelected(!changeCollectingPrefs.isTabHidden(SystemTabIdentifiers.SQL.getTabName()));
        }
        sqlViewEnabled.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final boolean enabled = sqlViewEnabled.isSelected();
                synchronized (lock) {
                    if (enabled) {
                        changeCollectingPrefs.clearTabHidden(SystemTabIdentifiers.SQL.getTabName());
                    } else {
                        changeCollectingPrefs.setTabHidden(SystemTabIdentifiers.SQL.getTabName());
                    }
                }
            }
        });

        innerPanel.add(sqlViewEnabled);

//        innerPanel.add(new JCheckBox("Does nothing 1"));
//        innerPanel.add(new JCheckBox("Does nothing 1"));
//        innerPanel.add(new JCheckBox("Does nothing 1"));
//
        mainPanel.add(innerPanel, BorderLayout.NORTH);
    }

    /**
     * {@inheritDoc}
     */
    public Component getComponent() {
        return mainPanel;
    }

    /**
     * {@inheritDoc}
     */
    public void disposeComponent() {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    public void destroy() {
        // do nothing
    }
}
