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

package org.devzendo.minimiser.gui.console.input;

import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;

/**
 * A text area with a slightly smaller font, and a bevelled border.
 * Also shows an intro message that clears on getting focus.
 * 
 * @author matt
 *
 */
@SuppressWarnings("serial")
public final class ConsoleTextArea extends JTextArea {
    private boolean firstFocus;

    /**
     * Create a console text area with the given title
     * @param title the title
     */
    public ConsoleTextArea(final String title) {
        super(title);
        setFont(new Font("Monospaced", Font.PLAIN, getFont().getSize()));
        setBorder(BorderFactory.createLoweredBevelBorder());
        setRows(3);

        firstFocus = true;

        addFocusListener(new FocusAdapter() {
            public void focusGained(final FocusEvent e) {
                if (firstFocus) {
                    firstFocus = false;
                    setText("");
                }
                
            }
        });
    }
}
