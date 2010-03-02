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

import org.devzendo.minimiser.gui.CursorManager;
import org.devzendo.minimiser.gui.dialog.about.AboutDialog;
import org.devzendo.minimiser.pluginmanager.PluginRegistry;


/**
 * Triggers display of the about dialog.
 * 
 * @author matt
 *
 */
public final class HelpAboutActionListener extends SnailActionListener {
    private final Frame mMainFrame;
    private final PluginRegistry mPluginRegistry;

    /**
     * @param pluginRegistry the plugin registry
     * @param frame the main frame
     * @param cursor the cursor manager 
     */
    public HelpAboutActionListener(final PluginRegistry pluginRegistry,
            final Frame frame, final CursorManager cursor) {
        super(cursor);
        mPluginRegistry = pluginRegistry;
        mMainFrame = frame;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformedSlowly(final ActionEvent e) {
        AboutDialog.showAbout(mPluginRegistry, mMainFrame, getCursorManager());
    }
}
