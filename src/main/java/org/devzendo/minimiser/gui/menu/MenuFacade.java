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
 * This is the amount of control MenuProvidingFacades have over the Menu.
 *
 * @author matt
 *
 */
public interface MenuFacade {
    /**
     * Rebuild the entire menu bar, all menus.
     */
    void rebuildEntireMenu();

    /**
     * Rebuild only the File menu, perhaps if the recent list has changed.
     */
    void rebuildFileMenu();

    /**
     * Rebuild only the view menu, perhaps if the list of custom views has changed.
     */
    void rebuildViewMenu();
}
