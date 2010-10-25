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

package org.devzendo.minimiser.gui.menu;

import javax.swing.JMenu;


/**
 * The Tools menu. Small, has no interesting functionality that couldn't be
 * implemented directly in MenuImpl, but encapsulated here to reduce the
 * coupling in MenuImpl.
 * 
 * @author matt
 *
 */
public final class ToolsMenu extends AbstractMenuGroup {
    private JMenu toolsMenu;

    /**
     * Construct the tools menu
     * 
     * @param wiring the menu wiring
     */
    public ToolsMenu(final MenuWiring wiring) {
        super(wiring);

        toolsMenu = new JMenu("Tools");
        toolsMenu.setMnemonic('T');
        
        createMenuItem(SystemMenuIdentifiers.TOOLS_OPTIONS, "Options...", 'O', toolsMenu);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JMenu getJMenu() {
        return toolsMenu;
    }
}
