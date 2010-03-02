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

import java.awt.event.ActionListener;

import org.apache.log4j.Logger;
import org.devzendo.minimiser.gui.menu.Menu.MenuIdentifier;
import org.devzendo.minimiser.springloader.SpringLoader;


/**
 * Attaches all the ActionListeners to the relevant MenuItems, via
 * MenuWiring.
 *
 * @author matt
 *
 */
public class MenuBuilderImpl implements MenuBuilder {
    private static final Logger LOGGER = Logger.getLogger(MenuBuilderImpl.class);
    private final MenuWiring wiring;
    private final SpringLoader loader;

    /**
     * Create a MenuBuilder
     * @param menuWiring the MenuWiring instance to use.
     * @param springLoader the SpringLoader instance
     */
    public MenuBuilderImpl(final MenuWiring menuWiring, final SpringLoader springLoader) {
        this.wiring = menuWiring;
        this.loader = springLoader;
    }

    /**
     * Build the menu by wiring it all up.
     */
    public void build() {
        loadAndWire(MenuIdentifier.FileNew);
        loadAndWire(MenuIdentifier.FileOpen);
        loadAndWire(MenuIdentifier.FileClose);
        loadAndWire(MenuIdentifier.FileCloseAll);

        loadAndWire(MenuIdentifier.ToolsOptions);

        loadAndWire(MenuIdentifier.HelpAbout);
        loadAndWire(MenuIdentifier.HelpWelcome);
        loadAndWire(MenuIdentifier.HelpWhatsNew);
        loadAndWire(MenuIdentifier.HelpCheckForUpdates);
    }

    private void loadAndWire(final MenuIdentifier menuIdentifier) {
        LOGGER.info(String.format("Loading ActionListener '%s'", menuIdentifier.toString()));
        wiring.setActionListener(menuIdentifier, loader.getBean("menuAL" + menuIdentifier.toString(), ActionListener.class));
    }

}
