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

import java.util.List;

import org.devzendo.minimiser.prefs.Prefs;


/**
 * The factory for creating one, or a list of Tools->Options dialog tabs given
 * a (change-collecting) Prefs.
 * 
 * @author matt
 *
 */
public interface ToolsOptionsTabFactory {

    /**
     * Load the supplied list of tabs.
     * <p>
     * Tabs are loaded as a list of beans from the Application Context.
     * <p>
     * The Prefs passed here will be available from the SpringLoader via a
     * <constructor-arg ref="changeCollectingPrefs" /> passed to
     * each tab's bean definition.
     * 
     * @param prefs the change-collecting Prefs that the Tools->Options tabs
     * modify.
     * @return the list of loaded ToolsOptionsTabss
     */
    List<ToolsOptionsTab> loadTabs(Prefs prefs);

    /**
     * Close a list of tabs.
     * 
     * @param tabs the tabs that are to be closed.
     */
    void closeTabs(List<ToolsOptionsTab> tabs);
}
