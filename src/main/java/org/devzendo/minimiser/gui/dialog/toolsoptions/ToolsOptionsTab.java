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

import java.awt.Component;

/**
 * A Tab that handles all detail of a specific view into the
 * Preferences
 * <p>
 * Its construction is managed by the ToolsOptionsTabFactory,
 * and these lifecycle methods are called either on a worker thread or the
 * Swing Event thread, as appropriate.
 * <p>
 * The construction of a ToolsOptionsTab always takes place on a worker thread,
 * i.e. a non-Swing Event thread. Swing components are always
 * initialsed/disposed of on the Swing Event thread.
 * 
 * @author matt
 *
 */
public interface ToolsOptionsTab {
    
    /**
     * What is the name of this tab, to be displayed in its tab in the Dialog's
     * JTabbedPane?
     * 
     * @return the tab name
     */
    String getName();
    
    /**
     * Always called on the Swing Event thread, this method
     * is used by the Tab to initialise any Swing components
     * necessary to display the View aspect of the ToolsOptionsTab.
     * <p>
     * This method is called shortly after construction.
     */
    void initComponent();
    
    /**
     * Obtain the main graphical component.
     * 
     * @return the main Component of this ToolsOptionsTab, most likely a
     * JPanel. This will be what is seen when the user switches
     * to this ToolsOptionsTab using the JTabbedPane.
     */
    Component getComponent();
    
    /**
     * Always called on the Swing Event thread, this method is
     * used by the tab to tidy up any Swing components used by
     * the View aspect of the ToolsOptionsTab.
     * <p>
     * disposeComponent is always called before destroy(). 
     */
    void disposeComponent();
    
    /**
     * Always called on a non-Swing Event thread, this method is
     * used by the ToolsOptionsTab to free any non-Swing resources.
     * <p>
     * destroy() is always called after disposeComponent().
     */
    void destroy();
}
