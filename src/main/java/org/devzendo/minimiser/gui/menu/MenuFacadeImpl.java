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

/**
 * The default implementation of the MenuFacade.
 * @author matt
 *
 */
public final class MenuFacadeImpl implements MenuFacade {
    private final Menu mMenu;

    /**
     * Construct the MenuFacadeImpl with the main menu
     * @param menu the menu
     */
    public MenuFacadeImpl(final Menu menu) {
        mMenu = menu;
    }

    /**
     * {@inheritDoc}
     */
    public void rebuildEntireMenu() {
        mMenu.rebuildEntireMenu();
    }

    /**
     * {@inheritDoc}
     */
    public void rebuildFileMenu() {
        mMenu.rebuildFileMenu();
    }

    /**
     * {@inheritDoc}
     */
    public void rebuildViewMenu() {
        mMenu.rebuildViewMenu();
    }
}