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

package org.devzendo.minimiser.gui.menu.actionlisteners;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import org.devzendo.commongui.CursorManager;
import org.devzendo.commongui.menu.actionlisteners.SnailActionListener;
import org.devzendo.minimiser.gui.dialog.toolsoptions.ToolsOptionsDialog;
import org.devzendo.minimiser.gui.dialog.toolsoptions.ToolsOptionsTabFactory;
import org.devzendo.minimiser.prefs.Prefs;

/**
 * Triggers display of the tools->options dialog.
 * 
 * @author matt
 *
 */
public final class ToolsOptionsActionListener extends SnailActionListener {
    private final Frame mainFrame;
    private final Prefs prefs;
    private final ToolsOptionsTabFactory toolsOptionsTabFactory;

    /**
     * @param frame the main frame
     * @param cursor the cursor manager
     * @param preferences the preferences 
     * @param tabFactory the Tools->Options tab factory
     */
    public ToolsOptionsActionListener(final Frame frame, final CursorManager cursor,
            final Prefs preferences, final ToolsOptionsTabFactory tabFactory) {
        super(cursor);
        this.mainFrame = frame;
        this.prefs = preferences;
        this.toolsOptionsTabFactory = tabFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformedSlowly(final ActionEvent e) {
        ToolsOptionsDialog.showOptions(mainFrame, getCursorManager(), prefs, toolsOptionsTabFactory);
    }
}
