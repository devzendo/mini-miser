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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.devzendo.minimiser.messagequeue.BooleanFlagSettingMessage;

/**
 * Renders boolean flag-setting messages with a checkbox for the flag.
 * 
 * @author matt
 *
 */
public final class BooleanFlagSettingMessageRenderer extends AbstractSimpleMessageRenderer {
    private BooleanFlagSettingMessage booleanFlagSettingMessage;
    /**
     * Construct the renderer
     * @param message the message to render
     */
    public BooleanFlagSettingMessageRenderer(final BooleanFlagSettingMessage message) {
        super(message);
        this.booleanFlagSettingMessage = message;
    }

    /**
     * {@inheritDoc}
     */
    public Component renderControls() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        final JCheckBox checkbox = new JCheckBox(booleanFlagSettingMessage.getChoiceExplanationText());
        checkbox.setSelected(booleanFlagSettingMessage.isBooleanFlagSet());
        checkbox.addItemListener(new ItemListener() {
            public void itemStateChanged(final ItemEvent e) {
                booleanFlagSettingMessage.setBooleanFlagValue(checkbox.isSelected());
            }
        });
        panel.add(checkbox, BorderLayout.WEST);
        return panel;
    }
}
