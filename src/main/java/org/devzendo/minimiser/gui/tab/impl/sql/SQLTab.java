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

package org.devzendo.minimiser.gui.tab.impl.sql;

import java.awt.Component;

import org.devzendo.minimiser.gui.CursorManager;
import org.devzendo.minimiser.gui.dialog.dstamessage.DSTAMessageHelper;
import org.devzendo.minimiser.gui.dialog.dstamessage.DSTAMessageId;
import org.devzendo.minimiser.gui.tab.Tab;
import org.devzendo.minimiser.openlist.DatabaseDescriptor;
import org.devzendo.minimiser.pluginmanager.PluginRegistry;


/**
 * A developer-friendly (and moderately user-friendly, if you know SQL) SQL
 * investigation, debug and diagnosis tab.
 * <p>
 * No TDD here.
 *  
 * @author matt
 *
 */
public final class SQLTab implements Tab {
    private final DatabaseDescriptor mDatabaseDescriptor;
    private final CursorManager mCursorManager;
    private final PluginRegistry mPluginRegistry;
    private volatile SQLTabPanel mainPanel;

    /**
     * Construct the SQL tab
     * @param descriptor the database descriptor
     * @param cursor the cursor manager
     * @param pluginRegistry the plugin registry
     */
    public SQLTab(final DatabaseDescriptor descriptor,
            final CursorManager cursor,
            final PluginRegistry pluginRegistry) {
        mDatabaseDescriptor = descriptor;
        mCursorManager = cursor;
        mPluginRegistry = pluginRegistry;
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
    public void initComponent() {
        mainPanel = new SQLTabPanel(mDatabaseDescriptor, mCursorManager, mPluginRegistry);
        
        DSTAMessageHelper.possiblyShowMessage(DSTAMessageId.SQL_TAB_INTRO, 
            "The SQL view is intended to aid developers in diagnosing\n"
            + "problems with databases. For further information on its use,\n"
            + "please consult the Technical Guide.\n\n"
            + "The SQL view is not intended for day-to-day use.\n"
        );
    }

    /**
     * {@inheritDoc}
     */
    public void destroy() {
    }
    
    /**
     * {@inheritDoc}
     */
    public void disposeComponent() {
        mainPanel.finished();
    }
}
